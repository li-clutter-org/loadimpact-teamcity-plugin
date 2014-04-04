package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class LoadTestSummaryTab extends ViewLogTab {
    private Debug debug = new Debug(this);

    public LoadTestSummaryTab(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server, @NotNull PluginDescriptor descriptor) {
        super("", "", pagePlaces, server);
        
        setTabTitle(getTitle());
        setPluginName(this.getClass().getSimpleName());
        setIncludeUrl(descriptor.getPluginResourcesPath(getJspName()));
        addCssFile(descriptor.getPluginResourcesPath("css/style.css"));
        debug.print("created");
    }

    protected String getTitle() {
        return "Load Test Summary";
    }
    
    protected String getJspName() {
        return this.getClass().getSimpleName() + ".jsp";
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request, @NotNull SBuild build) {
        debug.print("fillModel");

        Properties results = loadResultProperties(request);
        if (results == null) {
            model.put("noResults", "true");
            return;
        }

        model.put("hasResults", "true");
        for (Map.Entry<Object, Object> e : results.entrySet()) {
            model.put(e.getKey().toString(), e.getValue().toString());
        }

        debug.print("model: %s", model);
    }

    Properties loadResultProperties(HttpServletRequest request) {
        SBuild sBuild = getBuild(request);
        if (sBuild == null) return null;

        BuildArtifacts artifacts = sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_ALL);
        BuildArtifact resultsArtifact = artifacts.getArtifact(Constants.resultsFile);
        if (resultsArtifact == null) return null;

        try {
            InputStream is = resultsArtifact.getInputStream();
            Properties results = new Properties();
            results.load(is);
            is.close();
            return results;
        } catch (Exception e) {
            debug.print("Loading of results failed: %s", e);
        }

        return null;
    }


}
