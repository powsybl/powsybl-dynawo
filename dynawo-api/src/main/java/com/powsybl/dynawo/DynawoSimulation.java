package com.powsybl.dynawo;

public class DynawoSimulation {

    private final int startTime;
    private final int stopTime;
    private final boolean activeCriteria;

    public DynawoSimulation(int startTime, int stopTime, boolean activateCriteria) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.activeCriteria = activateCriteria;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getStopTime() {
        return stopTime;
    }

    public boolean isActiveCriteria() {
        return activeCriteria;
    }

}
