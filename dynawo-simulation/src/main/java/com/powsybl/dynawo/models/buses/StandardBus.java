/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.iidm.network.Bus;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StandardBus extends AbstractBus {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("bus_UPu", "v"),
            new VarMapping("bus_UPhase", "angle"));

    protected StandardBus(Bus bus, String parameterSetId, ModelConfig modelConfig) {
        super(bus, parameterSetId, modelConfig);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
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

    @Override
    public boolean needMandatoryDynamicModels() {
        return true;
    }
}
