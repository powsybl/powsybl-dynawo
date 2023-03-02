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
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Identifiable;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
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
    protected void writeDynamicAttributes(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
    }

    @Override
    public List<Pair<String, String>> getMacroConnectToAttributes() {
        List<Pair<String, String>> attributesConnectTo = new ArrayList<>(super.getMacroConnectToAttributes());
        attributesConnectTo.add(Pair.of("name2", getStaticId().orElse(null)));
        return attributesConnectTo;
    }

    public List<VarConnection> getVarConnectionsWithGenerator(GeneratorModel connected) {
        return Arrays.asList(
                new VarConnection(getTerminalVarName(), connected.getTerminalVarName()),
                new VarConnection(getSwitchOffSignalVarName(), connected.getSwitchOffSignalNodeVarName())
        );
    }

    /**
     * Creates connections only with generators without a dynamic model
     */
    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        String staticId = getStaticId().orElse(null);
        Bus bus = context.getNetwork().getBusBreakerView().getBus(staticId);
        if (bus == null) {
            throw new PowsyblException("Bus static id unknown: " + staticId);
        }
        List<String> staticIds = bus.getGeneratorStream().map(Identifiable::getId).filter(context::isWithoutBlackBoxDynamicModel).collect(Collectors.toList());
        if (!staticIds.isEmpty()) {
            createMacroConnectionsWithIndex1(staticIds, GeneratorModel.class, false, this::getVarConnectionsWithGenerator, context);
        }
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
