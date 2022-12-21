/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class GeneratorSynchronous extends AbstractGeneratorModel
        implements GeneratorSynchronousModel {

    private final String generatorLib;

    public GeneratorSynchronous(String dynamicModelId, String staticId, String parameterSetId, String generatorLib) {
        super(dynamicModelId, staticId, parameterSetId,
                "generator_terminal",
                "generator_switchOffSignal1",
                "generator_switchOffSignal2",
                "generator_switchOffSignal3",
                "generator_running");
        this.generatorLib = generatorLib;
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "generator_omegaRefPu";
    }

    @Override
    public String getOmegaPuVarName() {
        return "generator_omegaPu";
    }

    @Override
    public String getLib() {
        return generatorLib;
    }
}
