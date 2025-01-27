/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;
import com.powsybl.dynawo.algorithms.ContingencyEventModelsFactory;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.*;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;

import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SecurityAnalysisContext extends DynawoSimulationContext {

    private final List<Contingency> contingencies;
    private final List<ContingencyEventModels> contingencyEventModels;

    public SecurityAnalysisContext(Network network, String workingVariantId,
                                   List<BlackBoxModel> dynamicModels,
                                   DynamicSecurityAnalysisParameters parameters,
                                   DynawoSimulationParameters dynawoSimulationParameters,
                                   List<Contingency> contingencies) {
        this(network, workingVariantId, dynamicModels, parameters, dynawoSimulationParameters, contingencies, DynawoConstants.VERSION_MIN, ReportNode.NO_OP);
    }

    public SecurityAnalysisContext(Network network, String workingVariantId,
                                   List<BlackBoxModel> dynamicModels,
                                   DynamicSecurityAnalysisParameters parameters,
                                   DynawoSimulationParameters dynawoSimulationParameters,
                                   List<Contingency> contingencies,
                                   DynawoVersion currentVersion,
                                   ReportNode reportNode) {
        super(network, workingVariantId, dynamicModels, List.of(), Collections.emptyList(),
                parameters.getDynamicSimulationParameters(), dynawoSimulationParameters, currentVersion, reportNode);
        double contingenciesStartTime = parameters.getDynamicContingenciesParameters().getContingenciesStartTime();
        this.contingencies = contingencies;
        this.contingencyEventModels = ContingencyEventModelsFactory.createFrom(contingencies, this, macroConnectionsAdder, contingenciesStartTime, reportNode);
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
