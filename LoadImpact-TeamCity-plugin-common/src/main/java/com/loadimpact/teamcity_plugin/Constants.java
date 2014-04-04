package com.loadimpact.teamcity_plugin;

@SuppressWarnings("UnusedDeclaration")
public class Constants {
    public static final String TYPE        = "LoadImpact";
    public static final String NAME        = "Load Impact";
    public static final String DESCRIPTION = "Runs Load Impact load tests using the REST web services API.";

    public static final String apiToken_key            = "api.token";
    public static final String testConfigurationId_key = "test.configuration.id";

    public static final String delayValue_key     = "delay.value";
    public static final String delayUnit_key      = "delay.unit";
    public static final String delaySize_key      = "delay.size";
    public static final String abortAtFailure_key = "abort.failure";

    public static final int    thresholdCount           = 4;
    public static final String threshold_1_metric_key   = "threshold.1.metric";
    public static final String threshold_1_operator_key = "threshold.1.operator";
    public static final String threshold_1_value_key    = "threshold.1.value";
    public static final String threshold_1_result_key   = "threshold.1.result";

    public static final String threshold_2_metric_key   = "threshold.2.metric";
    public static final String threshold_2_operator_key = "threshold.2.operator";
    public static final String threshold_2_value_key    = "threshold.2.value";
    public static final String threshold_2_result_key   = "threshold.2.result";

    public static final String threshold_3_metric_key   = "threshold.3.metric";
    public static final String threshold_3_operator_key = "threshold.3.operator";
    public static final String threshold_3_value_key    = "threshold.3.value";
    public static final String threshold_3_result_key   = "threshold.3.result";

    public static final String threshold_4_metric_key   = "threshold.4.metric";
    public static final String threshold_4_operator_key = "threshold.4.operator";
    public static final String threshold_4_value_key    = "threshold.4.value";
    public static final String threshold_4_result_key   = "threshold.4.result";

    public static final String pollInterval_key = "poll.interval";
    public static final String logHttp_key      = "log.http";
    public static final String logReplies_key   = "log.replies";
    public static final String logDebug_key     = "log.debug";
    public static final String resultsFile      = "load-test-results.properties";

    public static String thresholdMetricKey(int n) { return thresholdKey(n, "metric"); }

    public static String thresholdOperatorKey(int n) { return thresholdKey(n, "operator"); }

    public static String thresholdValueKey(int n) { return thresholdKey(n, "value"); }

    public static String thresholdResultKey(int n) { return thresholdKey(n, "result"); }

    private static String thresholdKey(int n, String field) { return String.format("threshold.%d.%s", n, field); }

}
