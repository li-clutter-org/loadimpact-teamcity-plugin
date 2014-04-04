package com.loadimpact.teamcity_plugin;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class Util {

    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String toInitialCase(String s) {
        if (isBlank(s)) return s;
        if (s.length() == 1) return s.toUpperCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Creates a percentage ASCII bar.
     * @param percentage    value in [0, 100]
     * @return "[###......] nn%"
     */
    public static String percentageBar(double percentage) {
        final char  dot   = '.';
        final char  mark  = '#';
        final int   slots = 40;

        StringBuilder bar = new StringBuilder(replicate(String.valueOf(dot), slots));
        int numSlots = (int) (slots * percentage / 100.0);
        for (int k = 0; k < numSlots; ++k) bar.setCharAt(k, mark);

        return String.format("[%s] %3.0f%%", bar, percentage);
    }

    /**
     * Replicates a string.
     * @param s         the string to replicate
     * @param times     number of times
     * @return combined string
     */
    public static String replicate(String s, int times) {
        StringBuilder b = new StringBuilder(s.length() * times);
        for (int k = 1; k <= times; ++k) b.append(s);
        return b.toString();
    }

    public static boolean startsWith(Object obj, String prefix) {
        return obj != null && obj.toString().trim().startsWith(prefix);
    }
    
}
