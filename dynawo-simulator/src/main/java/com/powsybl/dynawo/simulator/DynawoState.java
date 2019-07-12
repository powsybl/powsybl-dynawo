package com.powsybl.dynawo.simulator;

import com.powsybl.simulation.SimulationState;

public class DynawoState implements SimulationState {

    @Override
    public String getName() {
        return "DynawoState";
    }

}
