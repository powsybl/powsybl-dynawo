/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class SynchronousGenerator extends SynchronizedGenerator implements SynchronousGeneratorModel {

    public SynchronousGenerator(String dynamicModelId, Generator generator, String parameterSetId, String generatorLib) {
        super(dynamicModelId, generator, parameterSetId, generatorLib);
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
}
