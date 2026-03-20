/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.contingency;

import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElementType;
import com.powsybl.dynawo.commons.NetworkExporter;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VoltageLevel;

import java.util.List;
import java.util.function.Consumer;

/**
 * Find voltage level associated with a switch or bus bar section contingency element
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ContingencyVoltageLevelFinder implements NetworkExporter.VoltageLevelFinder {

    private final List<Contingency> contingencies;

    public ContingencyVoltageLevelFinder(List<Contingency> contingencies) {
        this.contingencies = contingencies;
    }

    @Override
    public void findVoltageLevels(Network network, Consumer<VoltageLevel> voltageLevelConsumer) {
        contingencies.stream()
                .flatMap(c -> c.getElements().stream())
                .forEach(ce -> {
                    ContingencyElementType type = ce.getType();
                    if (ContingencyElementType.BUSBAR_SECTION == type) {
                        voltageLevelConsumer.accept(network.getBusbarSection(ce.getId()).getTerminal().getVoltageLevel());
                    } else if (ContingencyElementType.SWITCH == type) {
                        voltageLevelConsumer.accept(network.getSwitch(ce.getId()).getVoltageLevel());
                    }
                });
    }
}
