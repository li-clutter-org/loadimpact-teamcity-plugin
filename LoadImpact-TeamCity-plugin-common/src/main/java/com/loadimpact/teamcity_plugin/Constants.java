package com.loadimpact.teamcity_plugin;

@SuppressWarnings("UnusedDeclaration")
public class Constants {
    public static final String TYPE        = "LoadImpact";
    public static final String NAME        = "Load Impact";
    public static final String DESCRIPTION = "Runs Load Impact load tests using the REST API.";

    public static final String apiToken_key            = "api.token";
    public static final String testConfigurationId_key = "test.configuration.id";

    public static final String delayValue_key     = "delay.value";
    public static final String delayUnit_key      = "delay.unit";
    public static final String delaySize_key      = "delay.size";
    public static final String abortAtFailure_key = "abort.failure";

    public static final String pollInterval_key = "poll.interval";
    public static final String logHttp_key      = "log.http";
    public static final String logReplies_key   = "log.replies";
    public static final String logDebug_key     = "log.debug";
    public static final String resultsFile      = "load-test-results.properties";
    public static final String teamCityVersion_key = "teamcity.version";

    public static String thresholdMetricKey(int n) {
        return thresholdKey(n, "metric");
    }

    public static String thresholdOperatorKey(int n) {
        return thresholdKey(n, "operator");
    }

    public static String thresholdValueKey(int n) {
        return thresholdKey(n, "value");
    }

    public static String thresholdResultKey(int n) {
        return thresholdKey(n, "result");
    }

    private static String thresholdKey(int n, String field) {
        return String.format("threshold.%d.%s", n, field);
    }

}
