package com.loadimpact.teamcity_plugin;

import com.loadimpact.eval.LoadTestLogger;
import com.loadimpact.eval.LoadTestResultListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import com.loadimpact.eval.LoadTestResult;
import java.util.Arrays;

import static com.loadimpact.eval.LoadTestResult.*;
import static jetbrains.buildServer.agent.BuildFinishedStatus.*;

/**
 * Callback implementation of results processing.
 *
 * @author jens
 */
public class TeamCityLoadTestResultListener implements LoadTestResultListener {
    private final LoadTestLogger    logger;
    private final AgentRunningBuild build;
    private final Result            currentResult;

    private static class Result {
        public BuildFinishedStatus status;
        public String              reason;

        public Result(BuildFinishedStatus status, String reason) {
            this.status = status;
            this.reason = reason;
        }
    }


    public TeamCityLoadTestResultListener(LoadTestLogger logger, AgentRunningBuild build) {
        this.logger = logger;
        this.build = build;
        this.currentResult = new Result(BuildFinishedStatus.FINISHED_SUCCESS, "");
    }

    @Override
    public LoadTestResult getResult() {
        return null;
    }

    @Override
    public void markAs(LoadTestResult loadTestResult, String reason) {
        if (currentResult.status == FINISHED_SUCCESS) {
            BuildFinishedStatus lastStatus = currentResult.status;
            
            if (loadTestResult == failed) {
                currentResult.status = FINISHED_FAILED;
            } else if (loadTestResult == unstable && currentResult.status != FINISHED_FAILED) {
                currentResult.status = FINISHED_WITH_PROBLEMS;
            }
            
            if (lastStatus != currentResult.status) {
                currentResult.reason = String.format("Build marked as %s: %s", loadTestResult.getDisplayName(), reason);
                logger.failure(currentResult.reason);
            }
        }
    }

    @Override
    public boolean isFailure() {
        return currentResult.status == FINISHED_FAILED;
    }

    @Override
    public boolean isNonSuccessful() {
        return Arrays.asList(FINISHED_FAILED, FINISHED_WITH_PROBLEMS).contains(currentResult.status);
    }

    @Override
    public String getReason() {
        return currentResult.reason;
    }

    @Override
    public void stopBuild() {
        build.stopBuild("Stopping TeamCity build");
    }

    public BuildFinishedStatus getStatus() {
        return currentResult.status;
    }
}
