/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class SynchronousGenerator extends SynchronizedGenerator {

    private static final String DEFAULT_TERMINAL_VAR_NAME = "generator_terminal";

    private final String terminalVarName;

    public SynchronousGenerator(String dynamicModelId, Generator generator, String parameterSetId, String generatorLib, String terminalVarNamePrefix) {
        super(dynamicModelId, generator, parameterSetId, generatorLib);
        terminalVarName = terminalVarNamePrefix != null ? terminalVarNamePrefix + "_terminal1" : DEFAULT_TERMINAL_VAR_NAME;
    }

    public SynchronousGenerator(String dynamicModelId, Generator generator, String parameterSetId, String generatorLib) {
        super(dynamicModelId, generator, parameterSetId, generatorLib);
        terminalVarName = DEFAULT_TERMINAL_VAR_NAME;
    }

    @Override
    public String getTerminalVarName() {
        return terminalVarName;
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
    public double getWeightGen(DynaWaltzParameters dynaWaltzParameters) {
        double h = dynaWaltzParameters.getModelParameters(getParameterSetId()).getDouble("generator_H");
        double sNom = dynaWaltzParameters.getModelParameters(getParameterSetId()).getDouble("generator_SNom");
        return h * sNom;
    }
}
