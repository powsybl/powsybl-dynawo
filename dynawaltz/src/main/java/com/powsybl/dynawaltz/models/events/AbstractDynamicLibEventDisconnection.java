/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Identifiable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractDynamicLibEventDisconnection extends AbstractEventModel {

    private static final String EVENT_PREFIX = "Disconnect_";
    private static final String DYNAMIC_MODEL_LIB = "EventSetPointBoolean";
    private static final String DEFAULT_MODEL_LIB = "EventConnectedStatus";
    protected static final String DISCONNECTION_VAR_CONNECT = "event_state1";

    private final boolean disconnect;

    protected AbstractDynamicLibEventDisconnection(Identifiable<?> equipment, double startTime, boolean disconnect) {
        super(equipment, startTime, EVENT_PREFIX);
        this.disconnect = disconnect;
    }

    @Override
    public String getLib() {
        throw new PowsyblException("The associated library depends on context");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void writeDynamicAttributes(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", context.isWithoutBlackBoxDynamicModel(getEquipment()) ? DEFAULT_MODEL_LIB : DYNAMIC_MODEL_LIB);
        writer.writeAttribute("parFile", getParFile(context));
        writer.writeAttribute("parId", getParameterSetId());
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        ParametersXml.writeParameter(writer, ParameterType.DOUBLE, "event_tEvent", Double.toString(getStartTime()));
        ParametersXml.writeParameter(writer, ParameterType.BOOL, "event_stateEvent1", Boolean.toString(disconnect));
    }
}
