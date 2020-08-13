/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

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
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public String getDynamicModelId() {
        return dynamicModelId;
    }

    public abstract String getLib();

    public String getParameterSetId() {
        return parameterSetId;
    }

    public abstract void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException;

    private final String dynamicModelId;
    private final String parameterSetId;
}
