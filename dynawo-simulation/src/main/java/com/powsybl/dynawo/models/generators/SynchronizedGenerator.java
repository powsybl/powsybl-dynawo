/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarPrefix;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.List;
import java.util.Map;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedGenerator extends BaseGenerator implements FrequencySynchronizedModel {

    private static final String DEFAULT_OMEGA_REF_PU = "generator_omegaRefPu";
    private static final String DEFAULT_RUNNING = "generator_running";

    private String omegaRefPu = DEFAULT_OMEGA_REF_PU;
    private String running = DEFAULT_RUNNING;

    protected SynchronizedGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
        Map<String, VarPrefix> configVarPrefix = modelConfig.varPrefix();
        if (!configVarPrefix.isEmpty()) {
            VarPrefix varPrefix = configVarPrefix.get("omegaRefPu");
            if (varPrefix != null) {
                this.omegaRefPu = varPrefix.toVarName();
            }
            if ((varPrefix = configVarPrefix.get("running")) != null) {
                this.running = varPrefix.toVarName();
            }
        }
    }

    @Override
    public String getOmegaRefPuVarName() {
        return omegaRefPu;
    }

    @Override
    public String getRunningVarName() {
        return running;
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }
}
