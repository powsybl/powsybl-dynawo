package com.powsybl.dynawaltz.models.lines;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Line;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

public class StandardLine extends AbstractBlackBoxModel implements LineModel {

    private final String id;
    private final String sidePostfix;
    private final AtomicInteger atomicInteger = new AtomicInteger();

    public StandardLine(String dynamicModelId, String statidId, String parameterSetId, Branch.Side side) {
        super(dynamicModelId, statidId, parameterSetId);
        this.sidePostfix = LineModel.getSuffix(side);
        this.id = "line" + atomicInteger.incrementAndGet();
    }

    @Override
    public String getLib() {
        return "Line";
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", getParFile(context));
        writer.writeAttribute("parId", getParameterSetId());
        writer.writeAttribute("staticId", getStaticId().orElse(null));
        writer.writeEndElement();
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (connected instanceof BusModel) {
            BusModel busModel = (BusModel) connected;
            return Arrays.asList(
                    new VarConnection(getIVarName(), busModel.getNumCCVarName()),
                    new VarConnection(getStateVarName(), busModel.getTerminalVarName())
            );
        } else {
            throw new PowsyblException("StandardLineModel can only connect to BusModel");
        }
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext dynaWaltzContext) {
        Line line = dynaWaltzContext.getNetwork().getLine(getStaticId().orElse(null));
        if (line == null) {
            throw new PowsyblException("Line static id unkwown: " + getStaticId());
        }
        List<Model> connectedBbm = new ArrayList<>();
        for (Bus b : dynaWaltzContext.getNetwork().getBusBreakerView().getBuses()) {
            if (b.getLineStream().anyMatch(l -> l.equals(line))) {
                connectedBbm.add(dynaWaltzContext.getStaticIdBlackBoxModelMap().get(b.getId()));
            }
        }
        return connectedBbm;
    }

    @Override
    public String getName() {
        return getLib();
    }

    @Override
    public String getIVarName() {
        return this.id + "_" + atomicInteger + sidePostfix;
    }

    @Override
    public String getStateVarName() {
        return this.id + "_state";
    }

    @Override
    public String getDesactivateCurrentLimitsVarName() {
        return this.id + "_desactivate_currentLimits";
    }

    @Override
    public String getStateValueVarName() {
        return this.id + "_state_value";
    }
}
