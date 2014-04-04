package com.loadimpact.teamcity_plugin.eval;

import com.loadimpact.ApiTokenClient;
import com.loadimpact.RunningTestListener;
import com.loadimpact.exception.AbortTest;
import com.loadimpact.exception.ApiException;
import com.loadimpact.resource.Test;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.configuration.LoadSchedule;
import com.loadimpact.resource.test_result.StandardMetricResult;
import com.loadimpact.teamcity_plugin.Constants;
import com.loadimpact.teamcity_plugin.Debug;
import com.loadimpact.teamcity_plugin.DelayUnit;
import com.loadimpact.teamcity_plugin.LoadTestParameters;
import com.loadimpact.teamcity_plugin.LoadTestResult;
import com.loadimpact.teamcity_plugin.Operator;
import com.loadimpact.teamcity_plugin.Util;
import com.loadimpact.util.ListUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.loadimpact.resource.test_result.StandardMetricResult.Metrics;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class LoadTestListener implements RunningTestListener {
    private final Debug                  debug;
    private final LoadTestLogger         logger;
    private final LoadTestParameters     params;
    private       LoadTestState          state;
    private       int                    totalTimeMinutes;
    private       Long                   startTime;
    private       Double                 lastPercentage;
    private       URL                    resultsUrl;
    private final List<Threshold>        thresholds;
    private final LoadTestResultListener loadTestResultListener;


    public LoadTestListener(LoadTestParameters params, LoadTestLogger logger, LoadTestResultListener loadTestResultListener) {
        this.debug = new Debug(this);
        this.params = params;
        this.logger = logger;
        this.loadTestResultListener = loadTestResultListener;
        this.state = LoadTestState.notStarted;
        this.lastPercentage = -1D;

        BoundedDroppingQueue.setDefaultSize(params.get(Constants.delaySize_key, 1));

        this.thresholds = new ArrayList<Threshold>();
        for (int k = 1; k <= Constants.thresholdCount; ++k) {
            int thresholdValue = params.get(Constants.thresholdValueKey(k), -1);
            if (thresholdValue >= 0) {
                Metrics         metric   = params.get(Constants.thresholdMetricKey(k), Metrics.user_load_time);
                Operator        operator = params.get(Constants.thresholdOperatorKey(k), Operator.greaterThan);
                LoadTestResult  result   = params.get(Constants.thresholdResultKey(k), LoadTestResult.unstable);                
                this.thresholds.add(new Threshold(k, metric, operator, thresholdValue, result));
            }
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void onSetup(TestConfiguration configuration, ApiTokenClient client) {
        if (params.get(Constants.logReplies_key, false))
            debug.print("test-configuration: %s", configuration.toString());

        totalTimeMinutes = ListUtils.reduce(configuration.schedules, 0, new ListUtils.ReduceClosure<Integer, LoadSchedule>() {
            @Override
            public Integer eval(Integer sum, LoadSchedule s) {
                return sum + s.duration;
            }
        });
    }

    @Override
    public void onProgress(Test test, ApiTokenClient client) {
        if (params.get(Constants.logReplies_key, false)) debug.print(test.toString());

        LoadTestState lastState = state;
        state = state.moveToNext(test.status);
        if (state.isActive()) {
            if (startTime == null) startTime = now();

            Double percentage = getProgressPercentage(test, client);
            if (percentage != null && lastPercentage < percentage) {
                lastPercentage = percentage;
                logger.message(String.format("Running: %s (~ %.1f minutes remaining)",
                        Util.percentageBar(percentage),
                        totalTimeMinutes * (100D - percentage) / 100D
                ));
            }
        } else {
            if (state != lastState) logger.message("Load Test State: " + state);
        }

        if (resultsUrl == null && Util.startsWith(test.publicUrl, "http")) {
            resultsUrl = test.publicUrl;
            logger.message(String.format("Start sending load traffic [%d] %s", test.id, test.title));
            logger.message("Follow the test progress at " + test.publicUrl);
        }

        if (state.isBeforeCheckingThresholds()) {
            DelayUnit delayUnit = DelayUnit.valueOf(params.get(Constants.delayUnit_key, DelayUnit.seconds.name()));
            int delayValue = params.get(Constants.delayValue_key, 0);

            String reason = "";
            if (DelayUnit.seconds == delayUnit) {
                state = state.moveToNext(test.status, (startTime + delayValue * 1000) < now());
                reason = String.format("Passed %d seconds after running test start (current=%d seconds)", delayValue, (now() - startTime) / 1000);
            } else if (DelayUnit.users == delayUnit) {
                List<? extends StandardMetricResult> results = client.getStandardMetricResults(test.id, Metrics.clients_active, null, null);
                int usersCount = results.isEmpty() ? 0 : ListUtils.last(results).value.intValue();
                state = state.moveToNext(test.status, delayValue < usersCount);
                reason = String.format("Passed %d users (current=%d users)", delayValue, usersCount);
            }

            if (state.isCheckingThresholds()) logger.message("Start checking thresholds: " + reason);
        }

        if (state.isCheckingThresholds()) {
            for (Threshold t : thresholds) {
                List<? extends StandardMetricResult> metricValues = client.getStandardMetricResults(test.id, t.getMetric(), null, null);
                t.accumulate(metricValues);
                debug.print("Checking %s", t);

                if (t.isExceeded()) {
                    loadTestResultListener.markAs(t.getResult(), t.getReason());
                    debug.print("Threshold %d EXCEEDED: Build marked %s. Reason: %s", t.getId(), t.getResult().getDisplayName(), t.getReason());

                    if (loadTestResultListener.isFailure() && params.get(Constants.abortAtFailure_key, false)) {
                        throw new AbortTest();
                    }
                }
            }

            state = state.moveToNext(test.status, lastPercentage >= 100D);
            if (state != lastState && !state.isCheckingThresholds()) logger.message("Load Test State: " + state);
        }
    }

    @Override
    public void onSuccess(Test test) {
        logger.message("Load Test Completed");
    }

    @Override
    public void onFailure(Test test) {
        logger.failure("Load Test Failed: " + test.status);
    }

    @Override
    public void onAborted() {
        logger.failure("Load Test requested to be aborted");
        loadTestResultListener.stopBuild();
    }

    @Override
    public void onError(ApiException e) {
        logger.failure("Load Test Internal Error: " + e);
        loadTestResultListener.markAs(LoadTestResult.error, e.toString());
        loadTestResultListener.stopBuild();
    }

    private Double getProgressPercentage(Test test, ApiTokenClient client) {
        List<? extends StandardMetricResult> progress = client.getStandardMetricResults(test.id, Metrics.progress_percent_total, null, null);
        if (progress == null || progress.isEmpty()) return null;
        return ListUtils.last(progress).value.doubleValue();
    }

    /**
     * Returns the current time stamp.
     *
     * @return now
     */
    private long now() {
        return System.currentTimeMillis();
    }

}
