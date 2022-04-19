/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.collect.ImmutableMap;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.loadflow.LoadFlowParameters;

import java.util.*;

import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowParameters extends AbstractExtension<LoadFlowParameters> {

    private static final String SVC_REGULATION_ON = "svcRegulationOn";
    private static final String SHUNT_REGULATION_ON = "shuntRegulationOn";
    private static final String AUTOMATIC_SLACK_BUS_ON = "automaticSlackBusOn";
    private static final String DSO_VOLTAGE_LEVEL = "dsoVoltageLevel";

    public static final boolean DEFAULT_SVC_REGULATION_ON = false;
    public static final boolean DEFAULT_SHUNT_REGULATION_ON = false;
    public static final boolean DEFAULT_AUTOMATIC_SLACK_BUS_ON = false;
    public static final double DEFAULT_DSO_VOLTAGE_LEVEL = 45.0;

    private boolean svcRegulationOn = DEFAULT_SVC_REGULATION_ON;
    private boolean shuntRegulationOn = DEFAULT_SHUNT_REGULATION_ON;
    private boolean automaticSlackBusOn = DEFAULT_AUTOMATIC_SLACK_BUS_ON;
    private double dsoVoltageLevel = DEFAULT_DSO_VOLTAGE_LEVEL;

    public boolean getSvcRegulationOn() {
        return svcRegulationOn;
    }

    public DynaFlowParameters setSvcRegulationOn(boolean svcRegulationOn) {
        this.svcRegulationOn = svcRegulationOn;
        return this;
    }

    public boolean getShuntRegulationOn() {
        return shuntRegulationOn;
    }

    public DynaFlowParameters setShuntRegulationOn(boolean shuntRegulationOn) {
        this.shuntRegulationOn = shuntRegulationOn;
        return this;
    }

    public boolean getAutomaticSlackBusOn() {
        return automaticSlackBusOn;
    }

    public DynaFlowParameters setAutomaticSlackBusOn(boolean automaticSlackBusOn) {
        this.automaticSlackBusOn = automaticSlackBusOn;
        return this;
    }

    public double getDsoVoltageLevel() {
        return dsoVoltageLevel;
    }

    public DynaFlowParameters setDsoVoltageLevel(double dsoVoltageLevel) {
        this.dsoVoltageLevel = dsoVoltageLevel;
        return this;
    }

    @Override
    public String getName() {
        return "DynaFlowParameters";
    }

    @Override
    public String toString() {
        ImmutableMap.Builder<String, Object> immutableMapBuilder = ImmutableMap.builder();
        immutableMapBuilder
                .put(SVC_REGULATION_ON, svcRegulationOn)
                .put(SHUNT_REGULATION_ON, shuntRegulationOn)
                .put(AUTOMATIC_SLACK_BUS_ON, automaticSlackBusOn)
                .put(DSO_VOLTAGE_LEVEL, dsoVoltageLevel);

        return immutableMapBuilder.build().toString();
    }

    public static DynaFlowParameters load(PlatformConfig platformConfig) {
        Objects.requireNonNull(platformConfig);
        DynaFlowParameters parameters = new DynaFlowParameters();

        platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS)
                .ifPresent(config -> load(parameters, config));

        return parameters;
    }

    public static DynaFlowParameters load(ModuleConfig config) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        if (config != null) {
            load(parameters, config);
        }
        return parameters;
    }

    private static void load(DynaFlowParameters parameters, ModuleConfig config) {
        parameters.setSvcRegulationOn(config.getBooleanProperty(SVC_REGULATION_ON, DEFAULT_SVC_REGULATION_ON))
                .setShuntRegulationOn(config.getBooleanProperty(SHUNT_REGULATION_ON, DEFAULT_SHUNT_REGULATION_ON))
                .setAutomaticSlackBusOn(config.getBooleanProperty(AUTOMATIC_SLACK_BUS_ON, DEFAULT_AUTOMATIC_SLACK_BUS_ON))
                .setDsoVoltageLevel(config.getDoubleProperty(DSO_VOLTAGE_LEVEL, DEFAULT_DSO_VOLTAGE_LEVEL));
    }

    public static DynaFlowParameters load(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        DynaFlowParameters parameters = new DynaFlowParameters();
        Optional.ofNullable(properties.get(SVC_REGULATION_ON)).ifPresent(prop -> parameters.setSvcRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(SHUNT_REGULATION_ON)).ifPresent(prop -> parameters.setShuntRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(AUTOMATIC_SLACK_BUS_ON)).ifPresent(prop -> parameters.setAutomaticSlackBusOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(DSO_VOLTAGE_LEVEL)).ifPresent(prop -> parameters.setDsoVoltageLevel(Double.parseDouble(prop)));
        return parameters;
    }

    public static List<String> getSpecificParametersNames() {
        return Arrays.asList(SVC_REGULATION_ON, SHUNT_REGULATION_ON, AUTOMATIC_SLACK_BUS_ON, DSO_VOLTAGE_LEVEL);
    }
}
