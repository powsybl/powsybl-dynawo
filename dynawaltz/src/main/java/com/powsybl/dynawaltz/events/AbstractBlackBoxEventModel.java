/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.events;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractBlackBoxEventModel implements EventModel {

    public AbstractBlackBoxEventModel(String eventModelId, String staticId, String parameterSetId) {
        this.eventModelId = Objects.requireNonNull(eventModelId);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public String getEventModelId() {
        return eventModelId;
    }

    public abstract String getLib();

    public String getStaticId() {
        return staticId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    public abstract void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException;

    protected void writeEventBlackBoxModel(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getEventModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    private final String eventModelId;
    private final String staticId;
    private final String parameterSetId;
}
