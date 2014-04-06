package com.loadimpact.teamcity_plugin.eval;

import com.loadimpact.resource.Status;

import java.util.Arrays;

/**
 * DESCRIPTION
 *
 * @author jens
 */
@SuppressWarnings("UnusedDeclaration")
@Deprecated
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
        if (this == notStarted && status == Status.INITIALIZING) return initializing;
        if (this == initializing && status == Status.RUNNING) return warmingUp;
        if (this == warmingUp && status == Status.RUNNING && condition) return checkingThresholds;
        if (this == checkingThresholds && status == Status.RUNNING && condition) return finishing;
        if (this == finishing && status == Status.FINISHED) return terminated;

        return this;
    }

}
