/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.events;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawo.xml.DynawoXmlContext;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractBlackBoxEventModel implements EventModel {

    public AbstractBlackBoxEventModel(String dynamicModelId, String staticId, String parameterSetId) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public String getDynamicModelId() {
        return dynamicModelId;
    }

    public abstract String getLib();

    public String getStaticId() {
        return staticId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    public abstract void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException;

    protected void writeEventBlackBoxModel(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    private final String dynamicModelId;
    private final String staticId;
    private final String parameterSetId;
}
