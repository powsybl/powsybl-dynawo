/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.job;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Solver {

    private final String lib;
    private final String parFile;
    private final String parId;

    public Solver(com.powsybl.dynawo.simulator.DynawoSimulationParameters.Solver solverParameters) {
        this("lib", solverParameters.getParametersFile(), solverParameters.getParametersId());
    }

    public Solver(String lib, String parFile, String parId) {
        this.lib = Objects.requireNonNull(lib);
        this.parFile = Objects.requireNonNull(parFile);
        this.parId = Objects.requireNonNull(parId);
    }

    public String getLib() {
        return lib;
    }

    public String getParFile() {
        return parFile;
    }

    public String getParId() {
        return parId;
    }
}
