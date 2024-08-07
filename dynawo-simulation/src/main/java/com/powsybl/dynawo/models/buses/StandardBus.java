/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.iidm.network.Bus;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardBus extends AbstractBus {

    protected StandardBus(String dynamicModelId, Bus bus, String parameterSetId, String lib) {
        super(dynamicModelId, bus, parameterSetId, lib);
    }

    @Override
    protected void writeDynamicAttributes(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
    }

    @Override
    public String getTerminalVarName() {
        return "bus_terminal";
    }
}
