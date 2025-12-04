package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneratorAlphaBeta extends AbstractEquipmentBlackBoxModel<Generator> implements SpecifiedGeneratorModel {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("PPu", "p"),
            new VarMapping("QPu", "q"),
            new VarMapping("state", "state"));

    protected GeneratorAlphaBeta(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(getSwitchOffSignalNodeVarName(), switchOff + "_value"))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    public String getTerminalVarName() {
        return "terminal";
    }

    public String getSwitchOffSignalNodeVarName() {
        return "switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "switchOffSignal2";
    }

    public String getSwitchOffSignalAutomatonVarName() {
        return "switchOffSignal3";
    }

    public String getRunningVarName() {
        return "running";
    }

    @Override
    public String getUPuVarName() {
        return "UPu";
    }
}
