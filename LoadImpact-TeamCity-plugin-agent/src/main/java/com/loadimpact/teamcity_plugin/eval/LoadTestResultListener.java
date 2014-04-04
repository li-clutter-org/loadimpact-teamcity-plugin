package com.loadimpact.teamcity_plugin.eval;

import com.loadimpact.teamcity_plugin.LoadTestResult;
import jetbrains.buildServer.agent.BuildFinishedStatus;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public interface LoadTestResultListener {

    void markAs(LoadTestResult result, String reason);

    boolean isFailure();
    
    boolean isNonSuccessful();

    String getReason();

    void stopBuild();

    BuildFinishedStatus getStatus();
}
