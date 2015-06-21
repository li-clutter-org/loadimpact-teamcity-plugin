package com.loadimpact.teamcity_plugin;

import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.util.StringUtils;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * AJAX controller that proxies WS invocations to the LoadImpact REST API.
 *
 * @author jens
 */
public class ApiController extends BaseController {
    private final       Debug  debug = new Debug(this);
    public static final String URI   = "/loadImpactProxy.html";
    @NotNull
    private final LoadImpactSettings settings;

    public ApiController(@NotNull SBuildServer server, @NotNull WebControllerManager web, @NotNull LoadImpactSettings settings) {
        super(server);
        this.settings = settings;
        web.registerController(URI, this);
        debug.print("Initialized");
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        String task = request.getParameter("task");
        debug.print("doHandle: task=%s", task);

        settings.load();

        PrintWriter out = response.getWriter();
        try {
            if (StringUtils.isBlank(settings.getApiToken())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing API key");
            } else if (task == null || task.equals("help")) {
                response.setContentType("text/plain");
                help(out);
            } else if (task.equals("testConfigurations")) {
                response.setContentType("application/json");
                testConfigurations(out);
            } else if (task.equals("testConfiguration")) {
                response.setContentType("application/json");
                String id = request.getParameter("id");
                testConfiguration(id, out);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Illegal task '" + task + "'");
            }
        } catch (Exception e) {
            debug.print("Exception: %s", e);
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        out.flush();

        return null;
    }

    protected void help(PrintWriter out) {
        debug.print("help");

        out.println("Foobar AJAX Tasks");
        out.println("- help");
        out.println("- testConfigurations");
        out.println("- testConfiguration ID");
        out.println("- validateApiKey key");
        out.println("API key: " + settings.getApiToken());
    }

    protected void testConfiguration(String id, PrintWriter out) {
        debug.print("testConfiguration(%s)", id);

        ApiTokenClient client = new ApiTokenClient(settings.getApiToken());
        client.setDebug(true);
        TestConfiguration cfg = client.getTestConfiguration(Integer.parseInt(id));

        JsonObject obj = Json.createObjectBuilder()
                .add("id", cfg.id)
                .add("name", cfg.name)
                .add("url", cfg.url.toString())
                .build();

        StringWriter buf = new StringWriter();
        Json.createWriter(buf).writeObject(obj);
        String payload = buf.toString();

        debug.print("testConfiguration: %s", payload);
        out.println(payload);
    }

    protected void testConfigurations(PrintWriter out) {
        debug.print("testConfigurations");

        ApiTokenClient client = new ApiTokenClient(settings.getApiToken());
        client.setDebug(true);
        List<TestConfiguration> testConfigurations = client.getTestConfigurations();
        {
            int cnt = 1;
            for (TestConfiguration tc : testConfigurations) {
                debug.print("[%d] %s", cnt++, tc);
            }
        }

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (TestConfiguration cfg : testConfigurations) {
            JsonObject obj = Json.createObjectBuilder()
                    .add("id", cfg.id)
                    .add("name", cfg.name)
                    .add("url", toString(cfg.url))
                    .build();
            builder.add(obj);
        }

        StringWriter buf = new StringWriter();
        Json.createWriter(buf).writeArray(builder.build());
        String payload = buf.toString();

        debug.print("testConfigurations: %s", payload);
        out.println(payload);
    }

    private String toString(final Object obj) {
        if (obj == null) return "";
        String result = obj.toString();
        if (result == null) return "";
        return result;
    }

}
