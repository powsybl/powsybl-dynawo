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
import com.powsybl.dynawaltz.models.buses.ConnectionPoint;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractGenerator extends AbstractEquipmentBlackBoxModel<Generator> implements GeneratorModel {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping("generator_state", "state"));

    protected AbstractGenerator(String dynamicModelId, Generator generator, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, generator, lib);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createTerminalMacroConnections(equipment, this::getVarConnectionsWith, context);
    }

    private List<VarConnection> getVarConnectionsWith(ConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(getSwitchOffSignalNodeVarName(), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    public String getTerminalVarName() {
        return "generator_terminal";
    }

    public String getSwitchOffSignalNodeVarName() {
        return "generator_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "generator_switchOffSignal2";
    }

    public String getSwitchOffSignalAutomatonVarName() {
        return "generator_switchOffSignal3";
    }

    public String getRunningVarName() {
        return "generator_running";
    }

    public String getQStatorPuVarName() {
        return "generator_QStatorPu";
    }

    @Override
    public String getUPuVarName() {
        return "generator_UPu";
    }
}
