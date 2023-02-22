/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.iidm.network.Bus;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class StandardBus extends AbstractBlackBoxModel implements BusModel {

    public StandardBus(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "Bus";
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("staticId", getStaticId().orElse(null));
        writer.writeEndElement();
    }

    @Override
    public List<Pair<String, String>> getMacroConnectToAttributes() {
        List<Pair<String, String>> attributesConnectTo = new ArrayList<>(super.getMacroConnectToAttributes());
        attributesConnectTo.add(Pair.of("name2", getStaticId().orElse(null)));
        return attributesConnectTo;
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (!(connected instanceof GeneratorModel)) {
            throw new PowsyblException("StandardBusModel can only connect to GeneratorModel");
        }
        GeneratorModel connectedGeneratorModel = (GeneratorModel) connected;
        return Arrays.asList(
                new VarConnection(getTerminalVarName(), connectedGeneratorModel.getTerminalVarName()),
                new VarConnection(getSwitchOffSignalVarName(), connectedGeneratorModel.getSwitchOffSignalNodeVarName())
        );
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) {
        Bus bus = context.getNetwork().getBusBreakerView().getBus(getStaticId().orElse(null));
        if (bus == null) {
            throw new PowsyblException("Bus static id unknown: " + getStaticId());
        }
        return bus.getGeneratorStream().map(g -> context.getDynamicModelOrThrows(g.getId())).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return getLib();
    }

    @Override
    public String getTerminalVarName() {
        return "bus_terminal";
    }

    @Override
    public String getSwitchOffSignalVarName() {
        return "bus_switchOff";
    }

    @Override
    public String getNumCCVarName() {
        return "@NAME@_numcc";
    }
}
