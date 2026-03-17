/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.GeneratorModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Olivier Perrin {@literal <olivier.perrin at rte-france.com>}
 */
public class BaseGenerator extends AbstractEquipmentBlackBoxModel<Generator> implements SpecifiedGeneratorModel, GeneratorModel {

    private static final ComponentDescription DEFAULT_COMPONENT_DESCRIPTION = new Description();
    private final ComponentDescription componentDescription;

    protected BaseGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        this(generator, parameterSetId, modelConfig,
                isGeneratorCustom(modelConfig) ? CustomGeneratorComponent.fromModelConfig(modelConfig) : DEFAULT_COMPONENT_DESCRIPTION);
    }

    protected BaseGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig, ComponentDescription componentDescription) {
        super(generator, parameterSetId, modelConfig);
        this.componentDescription = componentDescription;
    }

    protected static boolean isGeneratorCustom(ModelConfig modelConfig) {
        return !modelConfig.varPrefix().isEmpty() || !modelConfig.varMapping().isEmpty();
    }

    protected ComponentDescription getComponentDescription() {
        return componentDescription;
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

    @Override
    public List<VarMapping> getVarsMapping() {
        return getComponentDescription().varMapping();
    }

    public String getTerminalVarName() {
        return getComponentDescription().terminal();
    }

    public String getSwitchOffSignalNodeVarName() {
        return getComponentDescription().switchOffSignalNode();
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return getComponentDescription().switchOffSignalEvent();
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return getComponentDescription().switchOffSignalAutomaton();
    }

    @Override
    public String getUPuVarName() {
        return "generator_UPu";
    }

    static class Description implements ComponentDescription {

        @Override
        public List<VarMapping> varMapping() {
            return EnumGeneratorComponent.NONE.getVarMapping();
        }

        @Override
        public String terminal() {
            return EnumGeneratorComponent.NONE.getTerminalVarName();
        }

        @Override
        public String switchOffSignalNode() {
            return "generator_switchOffSignal1";
        }

        @Override
        public String switchOffSignalEvent() {
            return "generator_switchOffSignal2";
        }

        @Override
        public String switchOffSignalAutomaton() {
            return "generator_switchOffSignal3";
        }
    }
}
