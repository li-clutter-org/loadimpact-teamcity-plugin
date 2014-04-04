package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class SettingsPage extends AdminPage {

    @NotNull
    private final PagePlaces       pagePlaces;
    @NotNull
    private final PluginDescriptor descriptor;
    @NotNull
    private final LoadImpactSettings settings;

    public SettingsPage(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor descriptor, @NotNull LoadImpactSettings settings) {
        super(pagePlaces);
        this.pagePlaces = pagePlaces;
        this.descriptor = descriptor;
        this.settings = settings;

        setPluginName(Constants.TYPE);
        setTabTitle(Constants.NAME);
        setIncludeUrl(descriptor.getPluginResourcesPath("/editSettings.jsp"));
        
        register();
    }

    @NotNull
    @Override
    public String getGroup() {
        return INTEGRATIONS_GROUP;
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        super.fillModel(model, request);

        model.put("apiToken", settings.getApiToken());
        model.put("actionUri", SettingsController.URI);
    }
    
}
