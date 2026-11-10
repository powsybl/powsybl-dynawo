/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.versionablevariable.Versionable;
import com.powsybl.dynawo.models.versionablevariable.VersionableHandler;
import com.powsybl.dynawo.models.versionablevariable.VersionableVariable;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.models.versionablevariable.VersionableHandler.DYNAWO_VERSION_1_8_0;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class WeccGen extends AbstractEquipmentBlackBoxModel<Generator> implements SpecifiedGeneratorModel {

    private static final VersionableVariable P_WECC_STATIC_REF = new VersionableVariable("P_WECC_STATIC_REF",
            new Versionable.Step<>("%s_measurements_PPuSnRef"),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, "%s_HvMeasurements_PPuSnRef"));

    private static final VersionableVariable Q_WECC_STATIC_REF = new VersionableVariable("Q_WECC_STATIC_REF",
            new Versionable.Step<>("%s_measurements_QPuSnRef"),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, "%s_HvMeasurements_QPuSnRef"));

    private final List<VarMapping> varsMapping;
    protected final String weccPrefix;

    protected WeccGen(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
        this.weccPrefix = Objects.requireNonNull(modelConfig.internalModelPrefix());
        VersionableHandler handler = VersionableHandler.getInstance();
        varsMapping = List.of(
                new VarMapping(handler.getCurrentVariable(P_WECC_STATIC_REF, weccPrefix), "p"),
                new VarMapping(handler.getCurrentVariable(Q_WECC_STATIC_REF, weccPrefix), "q"),
                new VarMapping(weccPrefix + "_injector_state", "state"));
    }

    private String switchOffSignalNodeVarName() {
        return weccPrefix + "_injector_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return weccPrefix + "_injector_switchOffSignal2";
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return weccPrefix + "_injector_switchOffSignal3";
    }

    @Override
    public String getUPuVarName() {
        return weccPrefix + "_injector_UPu";
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>();
        varConnections.add(new VarConnection(weccPrefix + "_terminal", connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(switchOffSignalNodeVarName(), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varsMapping;
    }
}
