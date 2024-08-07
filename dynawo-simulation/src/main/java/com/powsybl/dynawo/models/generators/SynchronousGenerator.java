/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class SynchronousGenerator extends SynchronizedGenerator {

    private final EnumGeneratorComponent generatorComponent;

    protected SynchronousGenerator(String dynamicModelId, Generator generator, String parameterSetId, String generatorLib, EnumGeneratorComponent generatorComponent) {
        super(dynamicModelId, generator, parameterSetId, generatorLib);
        this.generatorComponent = Objects.requireNonNull(generatorComponent);
    }

    @Override
    public String getTerminalVarName() {
        return generatorComponent.getTerminalVarName();
    }

    public String getOmegaPuVarName() {
        return "generator_omegaPu";
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return Arrays.asList(
                new VarConnection("omega_grp_@INDEX@", getOmegaPuVarName()),
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public double getWeightGen(DynawoSimulationParameters dynawoSimulationParameters) {
        double h = dynawoSimulationParameters.getModelParameters(getParameterSetId()).getDouble("generator_H");
        double sNom = dynawoSimulationParameters.getModelParameters(getParameterSetId()).getDouble("generator_SNom");
        return h * sNom;
    }
}
