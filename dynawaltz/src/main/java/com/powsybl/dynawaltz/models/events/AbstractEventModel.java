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
import com.powsybl.iidm.network.Identifiable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractEventModel extends AbstractPureDynamicBlackBoxModel implements EventModel {

    private static final String DISCONNECT_PREFIX = "Disconnect_";
    private final Identifiable<?> equipment;
    private final double startTime;

    protected AbstractEventModel(Identifiable<?> equipment, double startTime) {
        super(generateEventId(equipment.getId()));
        this.equipment = equipment;
        this.startTime = startTime;
    }

    protected static String generateEventId(String equipmentStaticId) {
        return DISCONNECT_PREFIX + equipmentStaticId;
    }

    public Identifiable<?> getEquipment() {
        return equipment;
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
