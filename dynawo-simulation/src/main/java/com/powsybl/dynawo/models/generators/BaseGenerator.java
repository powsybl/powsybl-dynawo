/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.xml.MacroStaticReference;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseGenerator extends AbstractEquipmentBlackBoxModel<Generator> implements GeneratorModel {

    private static final MacroStaticReference MACRO_STATIC_REFERENCE = MacroStaticReference.of("gen",
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping("generator_state", "state"));

    protected BaseGenerator(String dynamicModelId, Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, generator, modelConfig);
    }

    @Override
    public Optional<MacroStaticReference> getMacroStaticReference() {
        return Optional.of(MACRO_STATIC_REFERENCE);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(getSwitchOffSignalNodeVarName(), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    public String getTerminalVarName() {
        return "generator_terminal";
    }

    public String getSwitchOffSignalNodeVarName() {
        return "generator_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "generator_switchOffSignal2";
    }

    public String getSwitchOffSignalAutomatonVarName() {
        return "generator_switchOffSignal3";
    }

    public String getRunningVarName() {
        return "generator_running";
    }

    public String getQStatorPuVarName() {
        return "generator_QStatorPu";
    }

    @Override
    public String getUPuVarName() {
        return "generator_UPu";
    }
}
