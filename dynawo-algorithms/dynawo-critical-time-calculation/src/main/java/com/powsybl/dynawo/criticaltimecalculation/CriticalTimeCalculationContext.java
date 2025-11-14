/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynawo.*;
import com.powsybl.dynawo.algorithms.NodeFaultEventModels;
import com.powsybl.dynawo.algorithms.NodeFaultEventData;
import com.powsybl.dynawo.algorithms.NodeFaultEventModelsFactory;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;

import java.util.*;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public final class CriticalTimeCalculationContext extends DynawoSimulationContext {
    private final CriticalTimeCalculationParameters criticalTimeCalculationParameters;
    private final List<NodeFaultEventModels> nodeFaultEventModels;

    public static class Builder extends AbstractContextBuilder<Builder> {

        private final List<List<NodeFaultEventData>> nodeFaultsList;
        private List<NodeFaultEventModels> nodeFaultEventModels;
        private CriticalTimeCalculationParameters parameters;
        private EventModelsSupplier eventModelsSupplier;

        public Builder(Network network, List<BlackBoxModel> dynamicModels, List<List<NodeFaultEventData>> nodeFaultsList) {
            super(network, dynamicModels);
            this.nodeFaultsList = nodeFaultsList;
        }

        public Builder criticalTimeCalculationParameters(CriticalTimeCalculationParameters parameters) {
            this.parameters = Objects.requireNonNull(parameters);
            return self();
        }

        @Override
        protected void setupData() {
            if (parameters == null) {
                parameters = CriticalTimeCalculationParameters.load();
            }
            super.setupData();
        }

        @Override
        protected void setupMacroConnections() {
            super.setupMacroConnections();
            setupNodeFaultEventModels();
        }

        @Override
        protected void setupSimulationTime() {
            this.simulationTime = new SimulationTime(parameters.getStartTime(), parameters.getStopTime());
        }

        private void setupNodeFaultEventModels() {
            this.nodeFaultEventModels = NodeFaultEventModelsFactory.createFrom(nodeFaultsList,
                    network, blackBoxModelSupplier,
                    simulationModels::hasMacroConnector, reportNode);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public CriticalTimeCalculationContext build() {
            setup();
            return new CriticalTimeCalculationContext(this);
        }
    }

    private CriticalTimeCalculationContext(Builder builder) {
        super(builder);
        this.criticalTimeCalculationParameters = builder.parameters;
        this.nodeFaultEventModels = builder.nodeFaultEventModels;
    }

    public CriticalTimeCalculationParameters getCriticalTimeCalculationParameters() {
        return criticalTimeCalculationParameters;
    }

    public List<NodeFaultEventModels> getNodeFaultEventModels() {
        return nodeFaultEventModels;
    }

}
