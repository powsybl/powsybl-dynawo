/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.loadflow.LoadFlowParameters;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowParameters extends AbstractExtension<LoadFlowParameters> {

    private static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-default-parameters";

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
                .put("svcRegulationOn", svcRegulationOn)
                .put("shuntRegulationON", shuntRegulationOn)
                .put("automaticSlackBusON", automaticSlackBusOn)
                .put("dsoVoltageLevel", dsoVoltageLevel);

        return immutableMapBuilder.build().toString();
    }

    @AutoService(LoadFlowParameters.ConfigLoader.class)
    public static class DynaFlowConfigLoader implements LoadFlowParameters.ConfigLoader<DynaFlowParameters> {

        //Watch out for the name in the config.yml, no upper case at the beginning to match the one in the config.json
        //that can not have upper case also at the beginning
        @Override
        public DynaFlowParameters load(PlatformConfig platformConfig) {
            DynaFlowParameters parameters = new DynaFlowParameters();

            platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS)
                    .ifPresent(config -> parameters.setSvcRegulationOn(config.getBooleanProperty("svcRegulationOn", DEFAULT_SVC_REGULATION_ON))
                            .setShuntRegulationOn(config.getBooleanProperty("shuntRegulationOn", DEFAULT_SHUNT_REGULATION_ON))
                            .setAutomaticSlackBusOn(config.getBooleanProperty("automaticSlackBusOn", DEFAULT_AUTOMATIC_SLACK_BUS_ON))
                            .setDsoVoltageLevel(config.getDoubleProperty("dsoVoltageLevel", DEFAULT_DSO_VOLTAGE_LEVEL)));

            return parameters;
        }

        @Override
        public String getExtensionName() {
            return "DynaFlowParameters";
        }

        @Override
        public String getCategoryName() {
            return "loadflow-parameters";
        }

        @Override
        public Class<? super DynaFlowParameters> getExtensionClass() {
            return DynaFlowParameters.class;
        }
    }
}
