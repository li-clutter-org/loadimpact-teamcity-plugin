package com.loadimpact.teamcity_plugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * Simple console logger.
 *
 * @author jens
 */
public class Debug {
    private final String prefix;
    private static boolean enabled = false;

    static {
        FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        BufferedOutputStream buffer = new BufferedOutputStream(fdOut, 10000);
        PrintStream logStream = new PrintStream(buffer, false);
        System.setOut(logStream);
    }

    public Debug(String prefix) {
        this.prefix = prefix;
    }

    public Debug(Class cls) {
        this(cls.getSimpleName());
    }

    public Debug(Object obj) {
        this(obj.getClass());
    }

    public Debug print(String msg) {
        if (enabled) {
            String logtxt = String.format("*** [%2$s] %1$tF %1$tT%4$s    %3$s%n", new Date(), prefix, msg, System.getProperty("line.separator"));
            System.out.print(logtxt);
            System.out.flush();
        }
        return this;
    }

    public Debug print(String msg, Object... args) {
        return print(String.format(msg, args));
    }

    public static void setEnabled(boolean enabled) {
        Debug.enabled = enabled;
    }
}
