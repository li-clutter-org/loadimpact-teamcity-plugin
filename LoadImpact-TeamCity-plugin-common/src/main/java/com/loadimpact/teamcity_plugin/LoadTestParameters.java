package com.loadimpact.teamcity_plugin;

import com.loadimpact.resource.test_result.StandardMetricResult;

import java.util.HashMap;
import java.util.Map;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class LoadTestParameters {
    private Map<String, String> parameters;
    private static final String NULL_String = null;

    public LoadTestParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String get(String key, String defaultValue) {
        return parameters.get(key);
    }
    
    public int get(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, NULL_String));
        } catch (Exception e) { return defaultValue; }
    }

    public boolean get(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(key, NULL_String));
        } catch (Exception e) { return defaultValue; }
    }
    
    public float get(String key, float defaultValue) {
        try {
            return Float.parseFloat(get(key, NULL_String));
        } catch (Exception e) { return defaultValue; }
    }

    public StandardMetricResult.Metrics get(String key, StandardMetricResult.Metrics defaultValue) {
        try {
            return StandardMetricResult.Metrics.valueOf(get(key, defaultValue.name()));
        } catch (Exception e) { return defaultValue; }
    }

    public Operator get(String key, Operator defaultValue) {
        return Operator.valueOf(get(key, defaultValue.name()));
    }

    public LoadTestResult get(String key, LoadTestResult defaultValue) {
        return LoadTestResult.valueOf(get(key, defaultValue.name()));
    }

    public boolean has(String key) {
        return parameters.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(10000);
        buf.append(String.format("--- Load Test Parameters ---%n"));
        for (Map.Entry<String, String> e : parameters.entrySet()) {
            buf.append(String.format("  %s=%s%n", e.getKey(), e.getValue()));
        }
        buf.append(String.format("--- END ---%n"));
        return buf.toString();
    }
}
