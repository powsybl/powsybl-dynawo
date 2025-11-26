package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.generators.SpecifiedGeneratorModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LowVoltageRideThroughAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    protected final Generator generator;
    private boolean isConnected = true;

    protected LowVoltageRideThroughAutomationSystem(String dynamicModelId, String parameterSetId, Generator generator, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, modelConfig);
        this.generator = Objects.requireNonNull(generator);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        isConnected = !adder.createMacroConnectionsOrSkip(this, generator, SpecifiedGeneratorModel.class, this::getVarConnectionsWith);
        if (!isConnected) {
            DynawoSimulationReports.reportFailedDefaultModelHandling(adder.getReportNode(), getName(), getDynamicModelId(), IdentifiableType.GENERATOR.toString());
        }
    }

    protected List<VarConnection> getVarConnectionsWith(SpecifiedGeneratorModel connected) {
        return Arrays.asList(
                new VarConnection("hvrt_UMonitoredPu", connected.getUPuVarName()),
                new VarConnection("hvrt_switchOffSignal", connected.getSwitchOffSignalAutomatonVarName())
        );
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (isConnected) {
            super.write(writer, parFileName);
        }
    }
}
