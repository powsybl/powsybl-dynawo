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

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.simulator.DynawoParametersDatabase;
import com.powsybl.dynawo.xml.DynawoXmlContext;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractBlackBoxModel implements DynamicModel {

    public static class Parameters {

        public static Parameters load(DynawoParametersDatabase parametersDatabase, String parameterSetId) {
            return null;
        }

    }

    public AbstractBlackBoxModel(String dynamicModelId, String staticId, String parameterSetId, Parameters parameters) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
        this.parameters = parameters;
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

    public Parameters getParameters() {
        return parameters;
    }

    public abstract void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException;

    public void writeParameters(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        //Empty method to be redefined by specific models
    }

    private final String dynamicModelId;
    private final String staticId;
    private final String parameterSetId;
    private final Parameters parameters;
}
