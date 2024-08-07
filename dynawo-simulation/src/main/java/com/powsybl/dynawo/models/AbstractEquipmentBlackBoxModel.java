/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.xml.MacroStaticReference;
import com.powsybl.iidm.network.Identifiable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño {@literal <zamarrenolm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEquipmentBlackBoxModel<T extends Identifiable<?>> extends AbstractBlackBoxModel implements EquipmentBlackBoxModel {

    protected final T equipment;

    protected AbstractEquipmentBlackBoxModel(String dynamicModelId, String parameterSetId, T equipment, String lib) {
        super(dynamicModelId, parameterSetId, lib);
        this.equipment = Objects.requireNonNull(equipment);
    }

    @Override
    public String getStaticId() {
        return equipment.getId();
    }

    @Override
    public T getEquipment() {
        return equipment;
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        write(writer, getParFile(context));
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        boolean hasVarMapping = !getVarsMapping().isEmpty();
        if (hasVarMapping) {
            writer.writeStartElement(DYN_URI, "blackBoxModel");
        } else {
            writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        }
        writeDynamicAttributes(writer, parFileName);
        writer.writeAttribute("staticId", getStaticId());
        if (hasVarMapping) {
            MacroStaticReference.writeMacroStaticRef(writer, getLib());
            writer.writeEndElement();
        }
    }
}
