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
import com.powsybl.dynawaltz.models.MacroConnectAttribute;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Identifiable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        List<MacroConnectAttribute> attributesConnectTo = new ArrayList<>(super.getMacroConnectToAttributes());
        attributesConnectTo.add(MacroConnectAttribute.of("name2", getStaticId().orElse(null)));
        return attributesConnectTo;
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        // Buses with a dynamical model can only connect to equipment with a dynamic model
        String staticId = getStaticId().orElse(null);
        Bus bus = context.getNetwork().getBusBreakerView().getBus(staticId);
        if (bus == null) {
            throw new PowsyblException("Bus static id unknown: " + staticId);
        }
        checkLinkedDynamicModels(bus.getGeneratorStream(), "generator", context);
        checkLinkedDynamicModels(bus.getLineStream(), "line", context);
        checkLinkedDynamicModels(bus.getLoadStream(), "load", context);
    }

    private <T extends Identifiable<T>> void checkLinkedDynamicModels(Stream<T> stream, String equipmentName, DynaWaltzContext context) {
        stream.map(Identifiable::getId)
                .filter(context::isWithoutBlackBoxDynamicModel)
                .findAny()
                .ifPresent(id -> {
                    throw new PowsyblException(String.format("The %s %s linked to the standard bus %s does not possess a dynamic model",
                            equipmentName, id, getStaticId().orElse(null)));
                });
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
    public Optional<String> getSwitchOffSignalVarName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getNumCCVarName() {
        return Optional.empty();
    }
}
