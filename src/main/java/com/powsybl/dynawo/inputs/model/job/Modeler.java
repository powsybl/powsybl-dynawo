/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.job;

import java.util.Objects;

import com.powsybl.dynawo.simulator.DynawoSimulationParameters.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Modeler {

    private final String compileDir;
    private final String iidm;
    private final String parameters;
    private final String parameterId;
    private final String dyd;

    public Modeler(Network networkParameters) {
        this("iidm", networkParameters.getParametersFile(), networkParameters.getParametersId(), "dyd");
    }

    public Modeler(String iidm, String parameters, String parameterId, String dyd) {
        this("outputs/compilation", iidm, parameters, parameterId, dyd);
    }

    private Modeler(String compile, String iidm, String parameters, String parameterId, String dyd) {
        this.compileDir = Objects.requireNonNull(compile);
        this.iidm = Objects.requireNonNull(iidm);
        this.parameters = Objects.requireNonNull(parameters);
        this.parameterId = Objects.requireNonNull(parameterId);
        this.dyd = Objects.requireNonNull(dyd);
    }

    public String getCompileDir() {
        return compileDir;
    }

    public String getIidm() {
        return iidm;
    }

    public String getParameters() {
        return parameters;
    }

    public String getParameterId() {
        return parameterId;
    }

    public String getDyd() {
        return dyd;
    }
}
