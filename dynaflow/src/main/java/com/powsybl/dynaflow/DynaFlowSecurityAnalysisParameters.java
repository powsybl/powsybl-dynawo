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

import static com.powsybl.dynaflow.DynaFlowSecurityAnalysisProvider.MODULE_SPECIFIC_PARAMETERS;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynaFlowSecurityAnalysisParameters extends AbstractExtension<SecurityAnalysisParameters> {

    private static final double DEFAULT_TIME_OF_EVENT = 10d;
    private static final String TIME_OF_EVENT = "timeOfEvent";
    public static final List<String> SPECIFIC_PARAMETER_NAMES = List.of(TIME_OF_EVENT);

    private Double timeOfEvent = DEFAULT_TIME_OF_EVENT;

    public Double getTimeOfEvent() {
        return timeOfEvent;
    }

    public DynaFlowSecurityAnalysisParameters setTimeOfEvent(Double timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
        return this;
    }

    @Override
    public String getName() {
        return "DynaFlowSecurityAnalysisParameters";
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("").omitNullValues()
                .add(TIME_OF_EVENT, timeOfEvent).toString();
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
        config.getOptionalDoubleProperty(TIME_OF_EVENT).ifPresent(parameters::setTimeOfEvent);
    }

    public void update(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(TIME_OF_EVENT)).ifPresent(prop -> setTimeOfEvent(Double.parseDouble(prop)));
    }
}
