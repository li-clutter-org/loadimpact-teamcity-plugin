package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import org.jetbrains.annotations.NotNull;

/**
 * Descriptor for the load-test job.
 */
public class LoadImpactAgentBuildRunner implements AgentBuildRunner, AgentBuildRunnerInfo {
    @NotNull
    private final ArtifactsWatcher artifactsWatcher;

    public LoadImpactAgentBuildRunner(@NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @NotNull
    public String getType() {
        return Constants.TYPE;
    }

    public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
        return true;
    }

    @NotNull
    public AgentBuildRunnerInfo getRunnerInfo() {
        return this;
    }

    @NotNull
    public BuildProcess createBuildProcess(@NotNull AgentRunningBuild build, @NotNull BuildRunnerContext context) throws RunBuildException {
        return new LoadImpactBuildProcess(build, context, artifactsWatcher);
    }

}
