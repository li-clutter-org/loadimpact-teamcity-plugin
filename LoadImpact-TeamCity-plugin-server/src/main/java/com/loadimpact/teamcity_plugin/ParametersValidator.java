package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION
 *
 * @author jens
 */
@SuppressWarnings("UnusedDeclaration")
public class ParametersValidator implements PropertiesProcessor {
    private List<InvalidProperty>   invalid = new ArrayList<InvalidProperty>();
    private LoadTestParameters      parameters;

    @Override
    public Collection<InvalidProperty> process(Map<String, String> rawParams) {
        parameters = new LoadTestParameters(rawParams);

        checkNotNegative(Constants.delayValue_key);
        checkNotNegativeOrZero(Constants.delaySize_key);
        checkNotNegativeOrZero(Constants.pollInterval_key);
        
        for (int k = 1; k <= Constants.thresholdCount; ++k) {
            String key = Constants.thresholdValueKey(k);
            if (parameters.has(key)) checkNotNegativeOrZero(key);
        }
        
        return invalid;
    }

    void checkNotEmpty(String key) {
        String value = parameters.get(key, "");
        if (StringUtils.isEmpty(value)) {
            invalid.add(new InvalidProperty(key, "Must not be blank"));
        }
    }

    void checkNotNegative(String key) {
        int value = parameters.get(key, -1);
        if (value < 0) {
            invalid.add(new InvalidProperty(key, "Must not be negative"));
        }
    }

    void checkNotNegativeOrZero(String key) {
        int value = parameters.get(key, -1);
        if (value <= 0) {
            invalid.add(new InvalidProperty(key, "Must not be zero or negative"));
        }
    }

}
