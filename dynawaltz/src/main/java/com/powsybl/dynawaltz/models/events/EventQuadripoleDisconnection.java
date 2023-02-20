/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Branch;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.BOOL;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class EventQuadripoleDisconnection extends AbstractEventModel {

    private final String lineStaticId;
    private final boolean disconnectOrigin;
    private final boolean disconnectExtremity;

    public EventQuadripoleDisconnection(String eventModelId, String staticId, double startTime,
                                        boolean disconnectOrigin, boolean disconnectExtremity) {
        super(eventModelId, startTime);
        this.lineStaticId = staticId;
        this.disconnectOrigin = disconnectOrigin;
        this.disconnectExtremity = disconnectExtremity;
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (!(connected instanceof LineModel)) {
            throw new PowsyblException("EventQuadripoleDisconnection can only connect to LineModel");
        }
        return List.of(new VarConnection("event_state1_value", ((LineModel) connected).getStateValueVarName()));
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) {
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModel(lineStaticId);
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultLineModel(lineStaticId, Branch.Side.ONE));
        }
        return List.of(connectedBbm);
    }

    public String getLineStaticId() {
        return lineStaticId;
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, BOOL, "event_disconnectOrigin", Boolean.toString(disconnectOrigin));
        ParametersXml.writeParameter(writer, BOOL, "event_disconnectExtremity", Boolean.toString(disconnectExtremity));
    }
}
