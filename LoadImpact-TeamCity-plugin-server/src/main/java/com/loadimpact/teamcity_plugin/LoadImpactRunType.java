package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LoadImpactRunType extends RunType {
    private final Debug debug = new Debug(this);
    
    @NotNull
    private final PluginDescriptor    descriptor;
    @NotNull
    private final LoadImpactSettings  settings;
    @NotNull
    private final Map<String, String> defaultParameters;

    public LoadImpactRunType(@NotNull RunTypeRegistry registry, @NotNull PluginDescriptor descriptor, @NotNull LoadImpactSettings settings) {
        this.descriptor = descriptor;
        this.settings = settings;
        this.defaultParameters = new HashMap<String, String>();

        registry.registerRunType(this);
        initDefaultParams();
        debug.print("init: defaultParameters=%s", defaultParameters);
    }

    private void initDefaultParams() {
        addDefault(Constants.apiToken_key, settings.getApiToken());

        addDefault(Constants.delayValue_key, 2);
        addDefault(Constants.delayUnit_key, DelayUnit.users.name());
        addDefault(Constants.delaySize_key, 1);
        addDefault(Constants.abortAtFailure_key, true);

        addDefault(Constants.pollInterval_key, 10);
        addDefault(Constants.logHttp_key, false);
        addDefault(Constants.logReplies_key, false);
        addDefault(Constants.logDebug_key, false);
    }

    private void addDefault(String key, String value) {
        defaultParameters.put(key, value);
    }
    private void addDefault(String key, int value) {
        defaultParameters.put(key, Integer.toString(value));
    }
    private void addDefault(String key, boolean value) {
        defaultParameters.put(key, Boolean.toString(value));
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return defaultParameters;
    }

    @Nullable
    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new ParametersValidator();
    }

    @NotNull
    @Override
    public String getType() {
        return Constants.TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return Constants.NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return Constants.DESCRIPTION;
    }

    @Nullable
    @Override
    public String getEditRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("EditLoadImpactRunType.jsp");
    }

    @Nullable
    @Override
    public String getViewRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("ShowLoadImpactRunType.jsp");
    }
    
}
