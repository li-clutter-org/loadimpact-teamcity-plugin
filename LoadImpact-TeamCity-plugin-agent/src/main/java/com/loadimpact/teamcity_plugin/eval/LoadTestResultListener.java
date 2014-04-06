package com.loadimpact.teamcity_plugin.eval;

import com.loadimpact.eval.LoadTestResult;
import jetbrains.buildServer.agent.BuildFinishedStatus;

/**
 * DESCRIPTION
 *
 * @author jens
 */
@Deprecated
public interface LoadTestResultListener {

    void markAs(LoadTestResult result, String reason);

    boolean isFailure();
    
    boolean isNonSuccessful();

    String getReason();

    void stopBuild();

    BuildFinishedStatus getStatus();
}
