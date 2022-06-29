/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.events;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractBlackBoxEventModel implements EventModel, BlackBoxEventModel {

    private final String eventModelId;
    private final String staticId;
    private final String parameterSetId;

    public AbstractBlackBoxEventModel(String eventModelId, String staticId, String parameterSetId) {
        this.eventModelId = Objects.requireNonNull(eventModelId);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    @Override
    public String getEventModelId() {
        return eventModelId;
    }

    @Override
    public String getStaticId() {
        return staticId;
    }

    @Override
    public String getParameterSetId() {
        return parameterSetId;
    }

    @Override
    public String getDynamicModelId() {
        return null;
    }

    @Override
    public List<Pair<String, String>> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getEventModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzXmlContext xmlContext) {
        // No parameters for events
    }
}
