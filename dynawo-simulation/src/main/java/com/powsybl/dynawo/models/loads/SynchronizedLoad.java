/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.dynawo.models.versionablevariable.VersionableHandler;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Load;

import java.util.List;

import static com.powsybl.dynawo.models.versionablevariable.GlobalVersionableVariables.INDEXED_VARIABLE;
import static com.powsybl.dynawo.models.versionablevariable.GlobalVersionableVariables.INDEXED_VARIABLE_VALUE;
import static com.powsybl.dynawo.models.versionablevariable.GlobalVersionableVariables.VARIABLE_VALUE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SynchronizedLoad extends BaseLoad implements FrequencySynchronizedModel {

    protected SynchronizedLoad(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig);
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        VersionableHandler versionableHandler = VersionableHandler.getInstance();
        return List.of(
                new VarConnection(versionableHandler.getCurrentVariable(INDEXED_VARIABLE_VALUE, "omegaRef_grp"), getOmegaRefPuVarName()),
                new VarConnection(versionableHandler.getCurrentVariable(INDEXED_VARIABLE, "running_grp"), getRunningVarName())
        );
    }

    @Override
    public String getOmegaRefPuVarName() {
        return VersionableHandler.getInstance().getCurrentVariable(VARIABLE_VALUE, "load_omegaRefPu");
    }

    @Override
    public String getRunningVarName() {
        return "load_running";
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }
}
