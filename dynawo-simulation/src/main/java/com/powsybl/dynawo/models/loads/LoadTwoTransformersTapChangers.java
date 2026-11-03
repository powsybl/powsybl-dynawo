/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.transformers.TapChangerModel;
import com.powsybl.dynawo.models.versionablevariable.Versionable;
import com.powsybl.dynawo.models.versionablevariable.VersionableVariable;
import com.powsybl.dynawo.models.versionablevariable.VersionableHandler;
import com.powsybl.iidm.network.Load;

import java.util.List;
import java.util.function.Function;

import static com.powsybl.dynawo.models.TransformerSide.*;
import static com.powsybl.dynawo.models.versionablevariable.VersionableHandler.DYNAWO_VERSION_1_8_0;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformersTapChangers extends AbstractLoadTwoTransformers implements TapChangerModel {

    private static final VersionableVariable TC_SIDE_LOCKED = new VersionableVariable("TC_LOCKED",
            new Versionable.Step<>("tapChanger%s_locked"),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, "transformer%s_locked"));

    private final Versionable<Function<EquipmentConnectionPoint, List<VarConnection>>> versionableVarConnections = () -> List.of(
            new Versionable.Step<>(c -> {
                List<VarConnection> varConnections = super.getVarConnectionsWith(c);
                c.getSwitchOffSignalVarName()
                        .ifPresent(switchOff -> {
                            varConnections.add(new VarConnection(String.format("tapChanger%s_switchOffSignal1", HIGH_VOLTAGE.getSideSuffix()), switchOff));
                            varConnections.add(new VarConnection(String.format("tapChanger%s_switchOffSignal1", LOW_VOLTAGE.getSideSuffix()), switchOff));
                        });
                return varConnections;
            }),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, super::getVarConnectionsWith)
    );

    protected LoadTwoTransformersTapChangers(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig);
    }

    @Override
    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return VersionableHandler.getInstance().getCurrentVersionable(versionableVarConnections).apply(connected);
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        String lockedVar = VersionableHandler.getInstance().getCurrentVariable(TC_SIDE_LOCKED);
        return List.of(getTapChangerBlockerVarConnection(LOW_VOLTAGE, lockedVar),
                getTapChangerBlockerVarConnection(HIGH_VOLTAGE, lockedVar));
    }

    private VarConnection getTapChangerBlockerVarConnection(TransformerSide side, String lockedVar) {
        return new VarConnection(getTapChangerBlockingVarName(side), String.format(lockedVar, side.getSideSuffix()));
    }
}
