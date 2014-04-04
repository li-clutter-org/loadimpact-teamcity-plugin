package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class LoadImpactResultsTab extends LoadTestSummaryTab {
    public LoadImpactResultsTab(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server, @NotNull PluginDescriptor descriptor) {
        super(pagePlaces, server, descriptor);
    }

    @Override
    protected String getTitle() {
        return "Load Impact Results";
    }
}
