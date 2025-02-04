/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.AbstractContextBuilder;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;
import com.powsybl.dynawo.algorithms.ContingencyEventModelsFactory;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.*;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;

import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class SecurityAnalysisContext extends DynawoSimulationContext {

    private final List<Contingency> contingencies;
    private final List<ContingencyEventModels> contingencyEventModels;

    public static class Builder extends AbstractContextBuilder<Builder> {

        private final List<Contingency> contingencies;
        private DynamicSecurityAnalysisParameters securityParameters;
        private double contingenciesStartTime;

        public Builder(Network network, List<BlackBoxModel> dynamicModels, List<Contingency> contingencies) {
            super(network, dynamicModels);
            this.contingencies = contingencies;
        }

        public Builder dynamicSecurityAnalysisParameters(DynamicSecurityAnalysisParameters parameters) {
            this.securityParameters = Objects.requireNonNull(parameters);
            return self();
        }

        @Override
        protected void setup() {
            if (securityParameters == null) {
                securityParameters = DynamicSecurityAnalysisParameters.load();
            }
            simulationParameters = securityParameters.getDynamicSimulationParameters();
            contingenciesStartTime = securityParameters.getDynamicContingenciesParameters().getContingenciesStartTime();
            super.setup();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public SecurityAnalysisContext build() {
            setup();
            return new SecurityAnalysisContext(this);
        }
    }

    private SecurityAnalysisContext(Builder builder) {
        super(builder);
        this.contingencies = builder.contingencies;
        this.contingencyEventModels = ContingencyEventModelsFactory.createFrom(contingencies, this,
                macroConnectionsAdder, builder.contingenciesStartTime, getReportNode());
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }

    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }

    @Override
    public boolean withCurveVariables() {
        return false;
    }

    @Override
    public boolean withFsvVariables() {
        return false;
    }

    @Override
    public boolean withConstraints() {
        return true;
    }
}
