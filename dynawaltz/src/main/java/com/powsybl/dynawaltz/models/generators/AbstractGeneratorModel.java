/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractGeneratorModel extends AbstractBlackBoxModel implements GeneratorModel {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("generator_PGenPu", "p"),
            new VarMapping("generator_QGenPu", "q"),
            new VarMapping("generator_state", "state"));

    private final String terminalVarName;
    private final String switchOffSignalNodeVarName;
    private final String switchOffSignalEventVarName;
    private final String switchOffSignalAutomatonVarName;
    private final String runningVarName;

    protected AbstractGeneratorModel(String dynamicModelId, String staticId, String parameterSetId,
                                  String terminalVarName, String switchOffSignalNodeVarName,
                                  String switchOffSignalEventVarName, String switchOffSignalAutomatonVarName,
                                  String runningVarName) {
        super(dynamicModelId, Objects.requireNonNull(staticId), parameterSetId);
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
        String staticId = getStaticId().orElse(null); // cannot be empty as checked in constructor
        Generator generator = context.getNetwork().getGenerator(staticId);
        if (generator == null) {
            throw new PowsyblException("Generator static id unknown: " + staticId);
        }
        createMacroConnections(BusUtils.getConnectableBusStaticId(generator), BusModel.class, this::getVarConnectionsWithBus, context);
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        VarConnection terminalsConnection = new VarConnection(getTerminalVarName(), connected.getTerminalVarName());
        if (connected instanceof StandardBus) {
            return List.of(terminalsConnection);
        } else {
            VarConnection sosConnection = new VarConnection(getSwitchOffSignalNodeVarName(), connected.getSwitchOffSignalVarName());
            return List.of(terminalsConnection, sosConnection);
        }
    }

    @Override
    public String getTerminalVarName() {
        return terminalVarName;
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return switchOffSignalNodeVarName;
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return switchOffSignalEventVarName;
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return switchOffSignalAutomatonVarName;
    }

    @Override
    public String getRunningVarName() {
        return runningVarName;
    }
}
