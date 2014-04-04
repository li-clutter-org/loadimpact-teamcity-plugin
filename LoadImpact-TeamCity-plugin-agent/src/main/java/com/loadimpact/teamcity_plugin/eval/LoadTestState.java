package com.loadimpact.teamcity_plugin.eval;

import com.loadimpact.resource.Status;

import java.util.Arrays;

/**
 * DESCRIPTION
 *
 * @author jens
 */
@SuppressWarnings("UnusedDeclaration")
public enum LoadTestState {
    notStarted, initializing, warmingUp, checkingThresholds, finishing, terminated;

    public boolean isBeforeCheckingThresholds() {
        return this == warmingUp;
    }

    public boolean isCheckingThresholds() {
        return this == checkingThresholds;
    }

    public boolean isActive() {
        return Arrays.asList(warmingUp, checkingThresholds).contains(this);
    }

    public LoadTestState moveToNext(Status status) {
        return moveToNext(status, false);
    }
    
    public LoadTestState moveToNext(Status status, boolean condition) {
        if (this == notStarted && status == Status.initializing) return initializing;
        if (this == initializing && status == Status.running) return warmingUp;
        if (this == warmingUp && status == Status.running && condition) return checkingThresholds;
        if (this == checkingThresholds && status == Status.running && condition) return finishing;
        if (this == finishing && status == Status.finished) return terminated;

        return this;
    }

}
