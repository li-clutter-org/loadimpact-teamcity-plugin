package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentBuildRunner;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Descriptor for the load-test job.
 */
public class LoadImpactAgentBuildRunner implements AgentBuildRunner, AgentBuildRunnerInfo {
    private static final Logger LOG = Logger.getLogger(LoadImpactAgentBuildRunner.class);
    @NotNull
    private final ArtifactsWatcher artifactsWatcher;

    public LoadImpactAgentBuildRunner(@NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        LOG.info("init");
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
        LOG.info("createBuildProcess");
        return new LoadImpactBuildProcess(build, context, artifactsWatcher);
    }

}
