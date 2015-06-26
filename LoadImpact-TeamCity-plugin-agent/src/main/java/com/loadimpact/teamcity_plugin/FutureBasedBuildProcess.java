package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public abstract class FutureBasedBuildProcess implements BuildProcess, Callable<BuildFinishedStatus> {
    private static final Logger LOG = Logger.getLogger(FutureBasedBuildProcess.class);
    private Future<BuildFinishedStatus> futureStatus;

    public void start() throws RunBuildException {
        try {
            futureStatus = Executors.newSingleThreadExecutor().submit(this);
            LOG.info("Build process started");
        } catch (final RejectedExecutionException e) {
            LOG.error("Build process failed to start", e);
            throw new RunBuildException(e);
        }
    }

    @NotNull
    public BuildFinishedStatus waitFor() throws RunBuildException {
        try {
            final BuildFinishedStatus status = futureStatus.get();
            LOG.info("Build process was finished");
            return status;
        } catch (final InterruptedException e) {
            LOG.warn("Build process was interrupted", e);
            return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
        } catch (final ExecutionException e) {
            LOG.error(e.getMessage());
            return BuildFinishedStatus.FINISHED_FAILED;
        } catch (final CancellationException e) {
            LOG.warn("Build process was cancelled", e);
            return BuildFinishedStatus.INTERRUPTED;
        }
    }

    public void interrupt() {
        futureStatus.cancel(true);
    }

    public boolean isInterrupted() {
        return futureStatus.isCancelled() && isFinished();
    }

    public boolean isFinished() {
        return futureStatus.isDone();
    }

}
