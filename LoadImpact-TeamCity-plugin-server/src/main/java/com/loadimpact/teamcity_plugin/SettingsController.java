package com.loadimpact.teamcity_plugin;

import com.loadimpact.ApiTokenClient;
import jetbrains.buildServer.controllers.ActionMessage;
import jetbrains.buildServer.controllers.ActionMessages;
import jetbrains.buildServer.controllers.AjaxRequestProcessor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import org.jdom.Element;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class SettingsController extends BaseController {
    private final       Debug  debug = new Debug(this);
    public static final String URI   = "/saveSettings.html";
    @NotNull
    private final LoadImpactSettings settings;
    private String teamCityVersion;


    public SettingsController(@NotNull SBuildServer server, @NotNull WebControllerManager web, @NotNull LoadImpactSettings settings) {
        super(server);
        this.teamCityVersion = server.getFullServerVersion();
        this.settings = settings;
        web.registerController(URI, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        dumpParams(request);

        new AjaxRequestProcessor().processRequest(request, response, new AjaxRequestProcessor.RequestHandler() {
            public void handleRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Element xmlResponse) {
                try {
                    String task = request.getParameter("task");
                    String msg = "";
                    if ("saveSettings".equals(task)) {
                        msg = saveSettings(request);
                    } else if ("checkToken".equals(task)) {
                        msg = checkToken(request);
                    } else {
                        msg = "Invalid task: " + task;
                    }

                    ActionMessage result = new ActionMessage("result", msg);
                    ActionMessages.getOrCreateMessages(request).addMessage(result);
                } catch (Exception e) {
                    ActionMessage error = new ActionMessage("error", e.getMessage());
                    ActionMessages.getOrCreateMessages(request).addMessage(error);
                }
            }
        });
        
        return null;
    }

    private String checkToken(HttpServletRequest request) {
        String apiToken = request.getParameter("apiToken");

        ApiTokenClient client = new ApiTokenClient(apiToken);
        client.setDebug(true);
        if (client.isValidToken()) {
            settings.setApiToken(apiToken); //keep the value for the page refresh
            return "API Token is OK";
        }

        return "API Token is not valid/recognized";
    }


    private String saveSettings(HttpServletRequest request) {
        String apiToken = request.getParameter("apiToken");

        if (StringUtils.isNotEmpty(apiToken)) {
            new ApiTokenClient(apiToken); //if this succeeds, we're ok to proceed

            settings.setApiToken(apiToken);
            settings.store();
            debug.print("LoadImpact Settings saved to %s%n", settings.getSettingsFile().getAbsolutePath());
        } else {
            throw new RuntimeException("Empty or invalid API token: " + apiToken);
        }

        return "Load Impact settings saved";
    }

    private void dumpParams(HttpServletRequest request) {Enumeration<String> parameterNames = request.getParameterNames();
        debug.print("--- REQ Params ---");
        while (parameterNames.hasMoreElements()) {
            String param = parameterNames.nextElement();
            String value = request.getParameter(param);
            debug.print("%s: %s%n", param, value);
        }
    }
    
}
