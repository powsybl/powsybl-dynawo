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
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseGenerator extends AbstractEquipmentBlackBoxModel<Generator> implements SpecifiedGeneratorModel {

    private static final String DEFAULT_TERMINAL = "generator_terminal";
    private static final String DEFAULT_SWITCH_OFF_SIGNAL_1 = "generator_switchOffSignal1";

    static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping("generator_state", "state"));

    private String terminal = DEFAULT_TERMINAL;
    private String switchOffSignal1 = DEFAULT_SWITCH_OFF_SIGNAL_1;
    private List<VarMapping> varMapping = VAR_MAPPING;

    protected BaseGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
        String internalPrefix = modelConfig.internalModelPrefix();
        if (internalPrefix != null) {
            this.terminal = internalPrefix + "_terminal";
            this.switchOffSignal1 = internalPrefix + "_switchOffSignal1";
        }
        List<VarMapping> configVarMapping = modelConfig.varMapping();
        if (!varMapping.isEmpty()) {
            this.varMapping = configVarMapping;
        }
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varMapping;
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
        return terminal;
    }

    public String getSwitchOffSignalNodeVarName() {
        return switchOffSignal1;
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "generator_switchOffSignal2";
    }

    public String getSwitchOffSignalAutomatonVarName() {
        return "generator_switchOffSignal3";
    }

    @Override
    public String getUPuVarName() {
        return "generator_UPu";
    }
}
