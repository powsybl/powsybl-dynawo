/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.events.DisconnectableEquipment;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractGeneratorModel extends AbstractEquipmentBlackBoxModel<Generator> implements GeneratorModel, DisconnectableEquipment {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping("generator_state", "state"));

    private final String terminalVarName;
    private final String switchOffSignalNodeVarName;
    private final String switchOffSignalEventVarName;
    private final String switchOffSignalAutomatonVarName;
    private final String runningVarName;

    protected AbstractGeneratorModel(String dynamicModelId, Generator generator, String parameterSetId,
                                     String terminalVarName, String switchOffSignalNodeVarName,
                                     String switchOffSignalEventVarName, String switchOffSignalAutomatonVarName,
                                     String runningVarName) {
        super(dynamicModelId, parameterSetId, generator);
        this.terminalVarName = terminalVarName;
        this.switchOffSignalNodeVarName = switchOffSignalNodeVarName;
        this.switchOffSignalEventVarName = switchOffSignalEventVarName;
        this.switchOffSignalAutomatonVarName = switchOffSignalAutomatonVarName;
        this.runningVarName = runningVarName;
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(BusUtils.getConnectableBusStaticId(equipment), BusModel.class, this::getVarConnectionsWithBus, context);
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(getSwitchOffSignalNodeVarName(), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    public String getTerminalVarName() {
        return terminalVarName;
    }

    public String getSwitchOffSignalNodeVarName() {
        return switchOffSignalNodeVarName;
    }

    public String getSwitchOffSignalEventVarName() {
        return switchOffSignalEventVarName;
    }

    public String getSwitchOffSignalAutomatonVarName() {
        return switchOffSignalAutomatonVarName;
    }

    public String getRunningVarName() {
        return runningVarName;
    }

    public String getQStatorPuVarName() {
        return "generator_QStatorPu";
    }

    @Override
    public String getUPuVarName() {
        return "generator_UPu";
    }

    @Override
    public String getDisconnectableVarName() {
        return getSwitchOffSignalEventVarName();
    }
}
