package com.loadimpact.teamcity_plugin;

import com.loadimpact.resource.testresult.StandardMetricResult;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates/Prepares JSON data for inclusion on a JSP page.
 *
 * @author jens
 */
public class JsonGenerator {
    private Map<String, String> settings;
    private Debug debug = new Debug(JsonGenerator.class);

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }


    private static class MetricDescriptor {
        public StandardMetricResult.Metrics metric;
        public String                       label;
        public String                       unit;

        private MetricDescriptor(StandardMetricResult.Metrics metric, String label, String unit) {
            this.metric = metric;
            this.label = label;
            this.unit = unit;
        }
    }

    public String getOperators() {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        for (int i = 0; i < Operator.values().length; i++) {
            Operator operator = Operator.values()[i];
            builder.add(Json.createObjectBuilder()
                            .add("name", operator.name())
                            .add("label", operator.label)
                            .build());
        }

        StringWriter buf = new StringWriter();
        Json.createWriter(buf).writeArray(builder.build());
        return buf.toString();
    }

    public String getActions() {
        List<LoadTestResult> results = Arrays.asList(LoadTestResult.unstable, LoadTestResult.failed);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (int i = 0; i < results.size(); i++) {
            LoadTestResult action = results.get(i);
            builder.add(Json.createObjectBuilder()
                            .add("name", action.getId())
                            .add("label", action.getDisplayName())
                            .build());
        }

        StringWriter buf = new StringWriter();
        Json.createWriter(buf).writeArray(builder.build());
        return buf.toString();
    }


    public String getMetrics() {
        List<MetricDescriptor> metrics = Arrays.asList(
                new MetricDescriptor(StandardMetricResult.Metrics.BANDWIDTH, "Bandwidth", "bits/s"),
                new MetricDescriptor(StandardMetricResult.Metrics.FAILURE_RATE, "Failure Rate", "%"),
                new MetricDescriptor(StandardMetricResult.Metrics.REQUESTS_PER_SECOND, "Requests per Seconds", "*/s"),
                new MetricDescriptor(StandardMetricResult.Metrics.USER_LOAD_TIME, "User Load Time", "ms")
        );

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (int i = 0; i < metrics.size(); i++) {
            MetricDescriptor m = metrics.get(i);
            builder.add(Json.createObjectBuilder()
                            .add("name", m.metric.name())
                            .add("label", m.label)
                            .add("unit", m.unit)
                            .build());
        }

        StringWriter buf = new StringWriter();
        Json.createWriter(buf).writeArray(builder.build());
        return buf.toString();
    }

    public String getThresholds(Map<String, String> settings) {
        debug.print("getThresholds: %s", settings);

        JsonArrayBuilder builder = Json.createArrayBuilder();

        Pattern pattern = Pattern.compile("threshold\\.(\\d+)\\.value");
        for (String key : settings.keySet()) {
            Matcher m = pattern.matcher(key);
            if (m.matches()) {
                String id = m.group(1);
                debug.print("getThresholds: id=%s", id);
                
                String metric   = settings.get("threshold." + id + ".metric");
                String operator = settings.get("threshold." + id + ".operator");
                String value    = settings.get("threshold." + id + ".value");
                String action   = settings.get("threshold." + id + ".result");

                builder.add(Json.createObjectBuilder()
                                .add("metric", metric)
                                .add("operator", operator)
                                .add("value", Integer.parseInt(value))
                                .add("action", action)
                                .build());
            }
        }

        StringWriter buf = new StringWriter();
        Json.createWriter(buf).writeArray(builder.build());
        return buf.toString();
    }

    public String getThresholds() {
        return getThresholds(settings);
    }

}
