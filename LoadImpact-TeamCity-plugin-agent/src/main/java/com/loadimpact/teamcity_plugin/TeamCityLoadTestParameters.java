package com.loadimpact.teamcity_plugin;

import com.loadimpact.eval.DelayUnit;
import com.loadimpact.eval.LoadTestParameters;
import com.loadimpact.util.Parameters;
import com.loadimpact.eval.Threshold;
import com.loadimpact.eval.Operator;
import com.loadimpact.eval.LoadTestResult;
import com.loadimpact.resource.testresult.StandardMetricResult;

import java.util.Set;

/**
 * Wrapper around the TC load-test parameters.
 *
 * @author jens
 */
public class TeamCityLoadTestParameters implements LoadTestParameters {
    private Parameters parameters;

    public TeamCityLoadTestParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getApiToken() {
        return parameters.get(Constants.apiToken_key, "");
    }

    @Override
    public int getTestConfigurationId() {
        return parameters.get(Constants.testConfigurationId_key, 0);
    }

    @Override
    public Threshold[] getThresholds() {
        Set<String> keys = parameters.keys("threshold\\.\\d+\\.value");
        final int   N    = keys.size();

        Threshold[] thresholds = new Threshold[N];
        for (int k = 1; k <= N; ++k) {
            int thresholdValue = parameters.get(Constants.thresholdValueKey(k), -1);
            if (thresholdValue >= 0) {
                StandardMetricResult.Metrics metric = parameters.get(Constants.thresholdMetricKey(k), StandardMetricResult.Metrics.USER_LOAD_TIME);
                Operator operator = parameters.get(Constants.thresholdOperatorKey(k), Operator.greaterThan);
                LoadTestResult result = parameters.get(Constants.thresholdResultKey(k), LoadTestResult.unstable);

                thresholds[k - 1] = new Threshold(k, metric, operator, thresholdValue, result);
            }
        }

        return thresholds;
    }

    @Override
    public DelayUnit getDelayUnit() {
        return parameters.get(Constants.delayUnit_key, DelayUnit.seconds);
    }

    @Override
    public int getDelayValue() {
        return parameters.get(Constants.delayValue_key, 0);
    }

    @Override
    public int getDelaySize() {
        return parameters.get(Constants.delaySize_key, 1);
    }

    @Override
    public boolean isAbortAtFailure() {
        return parameters.get(Constants.abortAtFailure_key, false);
    }

    @Override
    public int getPollInterval() {
        return parameters.get(Constants.pollInterval_key, 5);
    }

    @Override
    public boolean isLogHttp() {
        return parameters.get(Constants.logHttp_key, false);
    }

    @Override
    public boolean isLogReplies() {
        return parameters.get(Constants.logReplies_key, false);
    }

    @Override
    public boolean isLogDebug() {
        return parameters.get(Constants.logDebug_key, false);
    }

    String getTeamCityVersion() {
        String v = parameters.get(Constants.teamCityVersion_key, (String) null);
        return v != null ? v : "no-version-passed-from-server";
    }
    
    @Override
    public String toString() {
        return parameters.toString();
    }
}
