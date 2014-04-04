package com.loadimpact.teamcity_plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public enum DelayUnit {
    seconds, users;

    public final String label;

    DelayUnit() {
        label = Util.toInitialCase(name());
    }

    public static List<String> names() {
        DelayUnit[] units = values();
        List<String> result = new ArrayList<String>(units.length);
        for (int i = 0; i < units.length; i++) {
            result.add(units[i].name());
        }
        return result;
    }
    
    public String getId() {
        return name();
    }
    
    public String getDisplayName() {
        return label;
    }
    
}
