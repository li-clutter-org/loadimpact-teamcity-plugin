package com.loadimpact.teamcity_plugin;

import com.loadimpact.teamcity_plugin.eval.LoadTestLogger;
import com.loadimpact.util.StringUtils;
import jetbrains.buildServer.agent.BuildProgressLogger;

/**
 * DESCRIPTION
 *
 * @author jens
 */
@SuppressWarnings("UnusedDeclaration")
public class TeamCityLoadTestLogger implements LoadTestLogger {
    private BuildProgressLogger logger;

    public TeamCityLoadTestLogger(BuildProgressLogger logger) {
        this.logger = logger;
    }

    public BuildProgressLogger getLogger() {
        return logger;
    }

    @Override
    public void message(String msg) {
        logger.progressStarted(msg);
    }

    @Override
    public void message(String fmt, Object... args) {
        logger.progressMessage(String.format(fmt, args));
    }

    @Override
    public void failure(String reason) {
        logger.buildFailureDescription(reason);
    }

    @Override
    public void started(String msg) {
        logger.progressStarted(msg);
    }

    @Override
    public void finished(String msg) {
        if (!StringUtils.isBlank(msg)) message(msg);
        logger.progressFinished();
    }    
}
