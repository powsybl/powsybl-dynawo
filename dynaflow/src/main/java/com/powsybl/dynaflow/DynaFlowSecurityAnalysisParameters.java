/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.google.common.base.MoreObjects;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.security.SecurityAnalysisParameters;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynaFlowSecurityAnalysisParameters extends AbstractExtension<SecurityAnalysisParameters> {

    public static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-security-analysis-default-parameters";

    private static final double DEFAULT_CONTINGENCIES_START_TIME = 10d;
    private static final String CONTINGENCIES_START_TIME = "contingenciesStartTime";
    public static final List<String> SPECIFIC_PARAMETER_NAMES = List.of(CONTINGENCIES_START_TIME);

    private Double contingenciesStartTime = DEFAULT_CONTINGENCIES_START_TIME;

    public Double getContingenciesStartTime() {
        return contingenciesStartTime;
    }

    public DynaFlowSecurityAnalysisParameters setContingenciesStartTime(Double contingenciesStartTime) {
        this.contingenciesStartTime = contingenciesStartTime;
        return this;
    }

    @Override
    public String getName() {
        return "DynaFlowSecurityAnalysisParameters";
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("").omitNullValues()
                .add(CONTINGENCIES_START_TIME, contingenciesStartTime).toString();
    }

    public static DynaFlowSecurityAnalysisParameters load(PlatformConfig platformConfig) {
        Objects.requireNonNull(platformConfig);
        DynaFlowSecurityAnalysisParameters parameters = new DynaFlowSecurityAnalysisParameters();
        platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS)
                .ifPresent(config -> load(parameters, config));
        return parameters;
    }

    public static DynaFlowSecurityAnalysisParameters load(ModuleConfig config) {
        DynaFlowSecurityAnalysisParameters parameters = new DynaFlowSecurityAnalysisParameters();
        if (config != null) {
            load(parameters, config);
        }
        return parameters;
    }

    public static DynaFlowSecurityAnalysisParameters load(Map<String, String> properties) {
        DynaFlowSecurityAnalysisParameters parameters = new DynaFlowSecurityAnalysisParameters();
        parameters.update(properties);
        return parameters;
    }

    private static void load(DynaFlowSecurityAnalysisParameters parameters, ModuleConfig config) {
        config.getOptionalDoubleProperty(CONTINGENCIES_START_TIME).ifPresent(parameters::setContingenciesStartTime);
    }

    public void update(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(CONTINGENCIES_START_TIME)).ifPresent(prop -> setContingenciesStartTime(Double.parseDouble(prop)));
    }
}
