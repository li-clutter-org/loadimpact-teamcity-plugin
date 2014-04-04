package com.loadimpact.teamcity_plugin;

import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.Test;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.test_result.StandardMetricResult;
import com.loadimpact.teamcity_plugin.eval.LoadTestListener;
import com.loadimpact.util.ListUtils;
import com.loadimpact.util.StringUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.loadimpact.resource.test_result.StandardMetricResult.Metrics.bandwidth;
import static com.loadimpact.resource.test_result.StandardMetricResult.Metrics.clients_active;
import static com.loadimpact.resource.test_result.StandardMetricResult.Metrics.requests_per_second;
import static com.loadimpact.resource.test_result.StandardMetricResult.Metrics.user_load_time;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class LoadImpactBuildProcess extends FutureBasedBuildProcess {
    private final Debug debug = new Debug(this);
    private final AgentRunningBuild  build;
    private final BuildRunnerContext context;
    private final ArtifactsWatcher   artifactsWatcher;

    public LoadImpactBuildProcess(AgentRunningBuild build, BuildRunnerContext context, ArtifactsWatcher artifactsWatcher) {
        this.build = build;
        this.context = context;
        this.artifactsWatcher = artifactsWatcher;
    }

    public BuildFinishedStatus call() throws Exception {
        final TeamCityLoadTestLogger logger = new TeamCityLoadTestLogger(build.getBuildLogger());
        logger.started("Load Test");

        LoadTestParameters params = new LoadTestParameters(context.getRunnerParameters());
        Debug.setEnabled(params.get(Constants.logDebug_key, false));
        debug.print(params.toString());

        String apiToken = params.get(Constants.apiToken_key, "");
        if (StringUtils.isBlank(apiToken)) {
            throw new RunBuildException("Empty API Token");
        }
        final ApiTokenClient client = new ApiTokenClient(apiToken);
        client.setDebug(params.get(Constants.logHttp_key, false));

        TeamCityLoadTestResultListener resultListener = new TeamCityLoadTestResultListener(logger, build);
        LoadTestListener listener = new LoadTestListener(params, logger, resultListener);

        logger.message("Fetching the test-configuration");
        TestConfiguration testConfiguration = client.getTestConfiguration(params.get(Constants.testConfigurationId_key, 0));
        listener.onSetup(testConfiguration, client);

        logger.message("Launching the load test");
        int testId = client.startTest(testConfiguration.id);

        Test test = client.monitorTest(testId, params.get(Constants.pollInterval_key, 5), listener);

        Properties results = new Properties();
        if (test == null) {
            logger.failure("Load test failed");
            results.setProperty("status", "error");
            results.setProperty("reason", resultListener.getReason());
        } else {
            if (resultListener.isNonSuccessful()) {
                logger.failure(resultListener.getReason());
                logger.message("Collecting load-test failure data");
                results.setProperty("status", "failure");
                results.setProperty("reason", resultListener.getReason());
            } else {
                logger.message("Collecting load-test success data");
                results.setProperty("status", "success");
            }
            results = populateResults(results, test, client);
        }
        File file = storeResultProperties(results);
        debug.print("--- Load Test Results ---%nFile=%s%n%s", file, toString(results));
        
        logger.finished(null);
        return resultListener.getStatus();
    }

    Properties populateResults(Properties results, Test tst, ApiTokenClient client) {
        results.setProperty("testId", Integer.toString(tst.id));
        results.setProperty("testName", tst.title);
        results.setProperty("targetUrl", tst.url.toString());
        results.setProperty("resultUrl", tst.publicUrl.toString() + "/embed");
        results.setProperty("elapsedTime", computeElapsedTime(tst));
        results.setProperty("responseTime", computeResponseTime(tst, client));
        results.setProperty("clientsCount", computeClientsCount(tst, client));
        results.setProperty("requestsCount", computeRequestsCount(tst, client));
        results.setProperty("bandwidth", computeBandwidth(tst, client));

        return results;
    }
    
    String computeElapsedTime(Test tst) {
        return timeFmt().print(new Period(tst.started.getTime(), tst.ended.getTime()));
    }

    @SuppressWarnings("unchecked")
    String computeResponseTime(Test tst, ApiTokenClient client) {
        List<StandardMetricResult> results = (List<StandardMetricResult>) client.getStandardMetricResults(tst.id, user_load_time, null, null);
        List<Double> values = ListUtils.map(results, new ListUtils.MapClosure<StandardMetricResult, Double>() {
            @Override
            public Double eval(StandardMetricResult r) { return r.value.doubleValue(); }
        });
        return timeFmt().print(new Period((long) ListUtils.average(values)));
    }

    @SuppressWarnings("unchecked")
    String computeClientsCount(Test tst, ApiTokenClient client) {
        List<StandardMetricResult> results = (List<StandardMetricResult>) client.getStandardMetricResults(tst.id, clients_active, null, null);
        List<Integer> values = ListUtils.map(results, new ListUtils.MapClosure<StandardMetricResult, Integer>() {
            @Override
            public Integer eval(StandardMetricResult r) { return r.value.intValue(); }
        });
        return String.valueOf(Collections.max(values));
    }

    @SuppressWarnings("unchecked")
    String computeRequestsCount(Test tst, ApiTokenClient client) {
        List<StandardMetricResult> results = (List<StandardMetricResult>) client.getStandardMetricResults(tst.id, requests_per_second, null, null);
        List<Double> values = ListUtils.map(results, new ListUtils.MapClosure<StandardMetricResult, Double>() {
            @Override
            public Double eval(StandardMetricResult r) { return r.value.doubleValue(); }
        });
        int avg = (int) ListUtils.average(values);
        int max = Collections.max(values).intValue();
        return String.format("%d (max %d) requests per second", avg, max);
    }
    
    @SuppressWarnings("unchecked")
    String computeBandwidth(Test tst, ApiTokenClient client) {
        List<StandardMetricResult> results = (List<StandardMetricResult>) client.getStandardMetricResults(tst.id, bandwidth, null, null);
        List<Double> values = ListUtils.map(results, new ListUtils.MapClosure<StandardMetricResult, Double>() {
            @Override
            public Double eval(StandardMetricResult r) { return r.value.doubleValue(); }
        });
        double avg = ListUtils.average(values) / 1E6;
        double max = Collections.max(values).intValue() / 1E6;
        return String.format("%.3f (max %.3f) MBits per second", avg, max);
    }
    

    File storeResultProperties(Properties p) throws IOException {
        File buildDir = build.getBuildTempDirectory();
        File resultsFile = new File(buildDir, Constants.resultsFile);
        FileWriter fileWriter = new FileWriter(resultsFile);
        p.store(fileWriter, "");
        fileWriter.close();
        artifactsWatcher.addNewArtifactsPath(resultsFile.getAbsolutePath());
        return resultsFile;
    }

    PeriodFormatter timeFmt() {
        return new PeriodFormatterBuilder()
                .minimumPrintedDigits(0)
                .printZeroNever()
                .appendHours()
                .appendSeparator("h ")
                .appendMinutes()
                .appendSeparator("m ")
                .appendSeconds()
                .appendSuffix("s")
                .toFormatter();
    }

    String toString(Properties properties) {
        StringBuilder buf = new StringBuilder(10000);
        for (Map.Entry<Object, Object> e : properties.entrySet()) {
            buf.append(String.format("  %s: %s%n", e.getKey(), e.getValue()));
        }
        return buf.toString();
    }

}
