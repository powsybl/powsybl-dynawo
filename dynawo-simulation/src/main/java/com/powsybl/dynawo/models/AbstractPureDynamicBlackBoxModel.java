/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.DynawoSimulationContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * Superclass for automation system and event black box models (model without IIDM static id)
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractPureDynamicBlackBoxModel extends AbstractBlackBoxModel {

    protected AbstractPureDynamicBlackBoxModel(String dynamicModelId, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, lib);
    }

    protected AbstractPureDynamicBlackBoxModel(String dynamicModelId, String lib) {
        super(dynamicModelId, dynamicModelId, lib);
    }

    @Override
    public final List<VarMapping> getVarsMapping() {
        // No static-dynamic mapping as purely dynamic
        return Collections.emptyList();
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        write(writer, getParFile(context));
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writeDynamicAttributes(writer, parFileName);
    }
}
