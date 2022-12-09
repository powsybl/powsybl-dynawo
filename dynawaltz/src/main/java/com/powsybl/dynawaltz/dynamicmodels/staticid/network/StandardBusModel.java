package com.powsybl.dynawaltz.dynamicmodels.staticid.network;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.staticid.AbstractBlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.dynamicmodels.staticid.staticref.generators.GeneratorModel;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

public class StandardBusModel extends AbstractBlackBoxModelWithStaticId implements BusModel {

    private final String id;
    private final AtomicInteger atomicInteger = new AtomicInteger();

    public StandardBusModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
        this.id = "bus" + atomicInteger.incrementAndGet();
    }

    @Override
    public String getLib() {
        return "Bus";
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("staticId", getStaticId());
        writer.writeEndElement();
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (!(connected instanceof GeneratorModel)) {
            throw new PowsyblException("StandardBusModel can only connect to GeneratorModel");
        }
        GeneratorModel connectedGeneratorModel = (GeneratorModel) connected;
        return Arrays.asList(
                Pair.of(getTerminalVarName(), connectedGeneratorModel.getTerminalVarName()),
                Pair.of(getSwitchOffSignalVarName(), connectedGeneratorModel.getSwitchOffSignalNodeVarName())
        );
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext dynaWaltzContext) {
        Bus bus = dynaWaltzContext.getNetwork().getBusBreakerView().getBus(getStaticId());
        if (bus == null) {
            throw new PowsyblException("Bus static id unknown: " + getStaticId());
        }
        List<BlackBoxModel> connectedBbm = new ArrayList<>();
        for (Generator g : dynaWaltzContext.getNetwork().getGenerators()) {
            if (g.getTerminal().getBusBreakerView().getConnectableBus().equals(bus)) {
                connectedBbm.add(dynaWaltzContext.getStaticIdBlackBoxModelMap().get(g.getId()));
            }
        }
        return connectedBbm;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTerminalVarName() {
        return "bus_terminal";
    }

    @Override
    public String getSwitchOffSignalVarName() {
        return getId() + "_switchOff";
    }

    @Override
    public String getNumCCVarName() {
        return "@NAME@_numcc";
    }
}
