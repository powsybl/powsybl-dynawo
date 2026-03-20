/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.commons.NetworkExporter;
import com.powsybl.iidm.network.BusbarSection;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Switch;
import com.powsybl.iidm.network.VoltageLevel;

import java.util.List;
import java.util.function.Consumer;

/**
 * Find voltage level associated with a switch or bus bar section output variable
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class OutputVariableVoltageLevelFinder implements NetworkExporter.VoltageLevelFinder {

    private final List<OutputVariable> curves;
    private final List<OutputVariable> fsv;

    public OutputVariableVoltageLevelFinder(List<OutputVariable> curves, List<OutputVariable> fsv) {
        this.curves = curves;
        this.fsv = fsv;
    }

    @Override
    public void findVoltageLevels(Network network, Consumer<VoltageLevel> voltageLevelConsumer) {
        findVoltageLevels(network, voltageLevelConsumer, curves);
        findVoltageLevels(network, voltageLevelConsumer, fsv);
    }

    private void findVoltageLevels(Network network, Consumer<VoltageLevel> voltageLevelConsumer, List<OutputVariable> outputVariables) {
        for (OutputVariable outputVariable : outputVariables) {
            BusbarSection bbs = network.getBusbarSection(outputVariable.getModelId());
            if (bbs != null) {
                voltageLevelConsumer.accept(bbs.getTerminal().getVoltageLevel());
                continue;
            }
            Switch sw = network.getSwitch(outputVariable.getModelId());
            if (sw != null) {
                voltageLevelConsumer.accept(sw.getVoltageLevel());
            }
        }
    }
}
