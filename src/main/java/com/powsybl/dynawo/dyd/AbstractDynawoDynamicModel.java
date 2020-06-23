/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.xml.DynamicModelsXml.DydXmlWriterContext;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractDynawoDynamicModel implements DynamicModel {

    public AbstractDynawoDynamicModel(String id, String staticId, String parameterSetId) {
        this.id = Objects.requireNonNull(id);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public String getId() {
        return id;
    }

    public abstract String getLib();

    public String getStaticId() {
        return staticId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    public List<String> getMacroStaticRefs() {
        return Collections.emptyList();
    }

    public List<MacroConnect> getMacroConnects() {
        return Collections.emptyList();
    }

    public abstract void write(XMLStreamWriter writer, DydXmlWriterContext dydXmlWriterContext) throws XMLStreamException;

    private final String id;
    private final String staticId;
    private final String parameterSetId;
}
