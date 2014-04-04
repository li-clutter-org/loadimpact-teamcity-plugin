package com.loadimpact.teamcity_plugin;

import jetbrains.buildServer.serverSide.ServerPaths;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class LoadImpactSettings {
    private final Debug  debug    = new Debug(this);
    private final String fileName = "loadimpact.properties";
    private final  File               settingsFile;
    private        String             apiToken;
    private static LoadImpactSettings _instance;

    public LoadImpactSettings(@NotNull ServerPaths serverPaths) {
        _instance = this;

        settingsFile = new File(serverPaths.getConfigDir(), fileName);
        try {
            settingsFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        load();
    }
    
    public static LoadImpactSettings instance() {
        return _instance;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public void store() {
        try {
            FileWriter out = new FileWriter(settingsFile);
            Properties p = new Properties();
            p.setProperty(Constants.apiToken_key, apiToken);
            p.store(out, "");
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        try {
            FileReader in = new FileReader(settingsFile);
            Properties p = new Properties();
            p.load(in);
            in.close();

            debug.print("Settings loaded: %s", p);

            apiToken = p.getProperty(Constants.apiToken_key);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
