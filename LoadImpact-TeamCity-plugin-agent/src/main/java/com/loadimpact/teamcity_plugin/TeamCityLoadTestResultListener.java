package com.loadimpact.teamcity_plugin;

import com.loadimpact.teamcity_plugin.eval.LoadTestLogger;
import com.loadimpact.teamcity_plugin.eval.LoadTestResultListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;

import java.util.Arrays;

import static com.loadimpact.teamcity_plugin.LoadTestResult.*;
import static jetbrains.buildServer.agent.BuildFinishedStatus.*;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class TeamCityLoadTestResultListener implements LoadTestResultListener {
    private final LoadTestLogger    logger;
    private final AgentRunningBuild build;
    private final Result            current;

    private static class Result {
        public BuildFinishedStatus status;
        public String              reason;

        public Result(BuildFinishedStatus status, String reason) {
            this.status = status;
            this.reason = reason;
        }
    }


    public TeamCityLoadTestResultListener(LoadTestLogger logger, AgentRunningBuild build) {
        this.logger  = logger;
        this.build   = build;
        this.current = new Result(BuildFinishedStatus.FINISHED_SUCCESS, "");
    }

    @Override
    public void markAs(LoadTestResult result, String reason) {
        if (current.status == FINISHED_SUCCESS) {
            BuildFinishedStatus lastStatus = current.status;
            if (result == failed) {
                current.status = FINISHED_FAILED;
            } else if (result == unstable && current.status != FINISHED_FAILED) {
                current.status = FINISHED_WITH_PROBLEMS;
            }
            if (lastStatus != current.status) {
                current.reason = String.format("Build marked as %s: %s", result.getDisplayName(), reason);
                logger.failure(current.reason);
            }
        }
    }

    @Override
    public boolean isFailure() {
        return current.status == FINISHED_FAILED;
    }

    @Override
    public boolean isNonSuccessful() {
        return Arrays.asList(FINISHED_FAILED, FINISHED_WITH_PROBLEMS).contains(current.status);
    }

    @Override
    public String getReason() {
        return current.reason;
    }

    @Override
    public void stopBuild() {
        build.stopBuild("Stopping TeamCity build");
    }

    @Override
    public BuildFinishedStatus getStatus() {
        return current.status;
    }
}
