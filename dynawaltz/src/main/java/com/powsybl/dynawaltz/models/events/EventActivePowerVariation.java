/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.EnumSet;
import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.BOOL;
import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class EventActivePowerVariation extends AbstractEventModel {

    private static final EnumSet<IdentifiableType> CONNECTABLE_EQUIPMENTS = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD);
    private static final String EVENT_PREFIX = "Step_";
    private static final String DYNAMIC_MODEL_LIB = "Step";
    private static final String DEFAULT_MODEL_LIB = "EventSetPointReal";

    private final double deltaP;

    public EventActivePowerVariation(Load equipment, double startTime, double deltaP) {
        super(equipment, startTime, EVENT_PREFIX);
        this.deltaP = deltaP;
    }

    public EventActivePowerVariation(Generator equipment, double startTime, double deltaP) {
        super(equipment, startTime, EVENT_PREFIX);
        this.deltaP = deltaP;
    }

    public static boolean isConnectable(IdentifiableType type) {
        return CONNECTABLE_EQUIPMENTS.contains(type);
    }

    @Override
    public String getLib() {
        throw new PowsyblException("The associated library depends on context");
    }

    @Override
    public String getName() {
        return EventActivePowerVariation.class.getSimpleName();
    }

    private List<VarConnection> getVarConnectionsWithDefaultControllableEquipment(ControllableEquipment connected) {
        return List.of(new VarConnection("event_state1", connected.getDeltaPVarName()));
    }

    private List<VarConnection> getVarConnectionsWithControllableEquipment(ControllableEquipment connected) {
        return List.of(new VarConnection("step_step_value", connected.getDeltaPVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getEquipment(),
                ControllableEquipment.class,
                context.isWithoutBlackBoxDynamicModel(getEquipment()) ? this::getVarConnectionsWithDefaultControllableEquipment : this::getVarConnectionsWithControllableEquipment,
                context);
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        super.writeParameters(writer, context);
        if (getEquipment().getType() == IdentifiableType.LOAD) {
            context.getDynaWaltzParameters().getNetworkParameters().addParameter(getEquipment().getId() + "_isControllable", BOOL, Boolean.toString(true));
        }
    }

    @Override
    protected void writeEventSpecificParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        if (context.isWithoutBlackBoxDynamicModel(getEquipment())) {
            ParametersXml.writeParameter(writer, DOUBLE, "event_tEvent", Double.toString(getStartTime()));
            ParametersXml.writeParameter(writer, DOUBLE, "event_stateEvent1", Double.toString(deltaP));
        } else {
            ParametersXml.writeParameter(writer, DOUBLE, "step_Value0", Double.toString(0));
            ParametersXml.writeParameter(writer, DOUBLE, "step_tStep", Double.toString(getStartTime()));
            ParametersXml.writeParameter(writer, DOUBLE, "step_Height", Double.toString(deltaP));
        }
    }

    @Override
    protected void writeDynamicAttributes(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", context.isWithoutBlackBoxDynamicModel(getEquipment()) ? DEFAULT_MODEL_LIB : DYNAMIC_MODEL_LIB);
        writer.writeAttribute("parFile", getParFile(context));
        writer.writeAttribute("parId", getParameterSetId());
    }
}
