/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.IdentifiableType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractEventModel extends AbstractPureDynamicBlackBoxModel implements EventModel {

    private static final String DISCONNECT_PREFIX = "Disconnect_";
    protected static final String CONNECT_PREFIX = "Connect_";
    private final String equipmentStaticId;
    private final double startTime;

    protected AbstractEventModel(String dynamicModelId, String equipmentStaticId, double startTime) {
        super(dynamicModelId, dynamicModelId);
        this.equipmentStaticId = equipmentStaticId;
        this.startTime = startTime;
    }

    //TODO handle connect
    protected static String generateEventId(String equipmentStaticId, boolean disconnect) {
        return disconnect ? DISCONNECT_PREFIX + equipmentStaticId : CONNECT_PREFIX + equipmentStaticId;
    }

    public String getEquipmentStaticId() {
        return equipmentStaticId;
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "set");
        writer.writeAttribute("id", getParameterSetId());
        ParametersXml.writeParameter(writer, DOUBLE, "event_tEvent", Double.toString(getStartTime()));
        writeEventSpecificParameters(writer, context);
        writer.writeEndElement();
    }

    protected abstract void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException;
}
