package com.loadimpact.teamcity_plugin;

import com.loadimpact.util.StringUtils;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public enum LoadTestResult {
    aborted, unstable, failed, error;

    public String getId() {
        return name();
    }

    public String getDisplayName() {
        return StringUtils.toInitialCase(name());
    }
    
}
