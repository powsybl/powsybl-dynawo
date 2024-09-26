/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.events;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.builders.EventModelInfo;
import com.powsybl.dynawo.builders.VersionBound;
import com.powsybl.dynawo.models.AbstractBlackBoxModel;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Identifiable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEvent extends AbstractBlackBoxModel implements EventModel {

    private final EventModelInfo eventModelInfo;
    private final Identifiable<? extends Identifiable<?>> equipment;
    private final double startTime;

    protected AbstractEvent(String eventId, Identifiable<?> equipment, EventModelInfo eventModelInfo, double startTime) {
        super(eventId);
        this.equipment = equipment;
        this.startTime = startTime;
        this.eventModelInfo = eventModelInfo;
    }

    public Identifiable<?> getEquipment() {
        return equipment;
    }

    @Override
    public String getLib() {
        return eventModelInfo.name();
    }

    @Override
    public VersionBound getVersionBound() {
        return eventModelInfo.version();
    }

    @Override
    public final List<VarMapping> getVarsMapping() {
        // No static-dynamic mapping as purely dynamic
        return Collections.emptyList();
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writeDynamicAttributes(writer, parFileName);
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public String getParFile(DynawoSimulationContext context) {
        return context.getSimulationParFile();
    }

    @Override
    public void createDynamicModelParameters(DynawoSimulationContext context, Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        createEventSpecificParameters(paramSet);
        parametersAdder.accept(paramSet);
    }

    protected abstract void createEventSpecificParameters(ParametersSet paramSet);
}
