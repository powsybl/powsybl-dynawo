/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.base.MoreObjects;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.loadflow.LoadFlowParameters;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowParameters extends AbstractExtension<LoadFlowParameters> {

    private static final String CHOSEN_OUTPUT_STRING_DELIMITER = ",";

    private static final String SVC_REGULATION_ON = "svcRegulationOn";
    private static final String SHUNT_REGULATION_ON = "shuntRegulationOn";
    private static final String AUTOMATIC_SLACK_BUS_ON = "automaticSlackBusOn";
    private static final String DSO_VOLTAGE_LEVEL = "dsoVoltageLevel";

    private static final String CHOSEN_OUTPUTS = "chosenOutputs";

    private static final String VSC_AS_GENERATORS = "vscAsGenerators";

    private static final String LCC_AS_LOADS = "lccAsLoads";

    private static final String TIME_STEP = "timeStep";

    private Boolean svcRegulationOn = null;
    private Boolean shuntRegulationOn = null;
    private Boolean automaticSlackBusOn = null;
    private Double dsoVoltageLevel = null;
    private List<String> chosenOutputs = null;
    private Boolean vscAsGenerators = null;
    private Boolean lccAsLoads = null;
    private Double timeStep = null;

    public Boolean getSvcRegulationOn() {
        return svcRegulationOn;
    }

    public DynaFlowParameters setSvcRegulationOn(boolean svcRegulationOn) {
        this.svcRegulationOn = svcRegulationOn;
        return this;
    }

    public Boolean getShuntRegulationOn() {
        return shuntRegulationOn;
    }

    public DynaFlowParameters setShuntRegulationOn(boolean shuntRegulationOn) {
        this.shuntRegulationOn = shuntRegulationOn;
        return this;
    }

    public Boolean getAutomaticSlackBusOn() {
        return automaticSlackBusOn;
    }

    public DynaFlowParameters setAutomaticSlackBusOn(boolean automaticSlackBusOn) {
        this.automaticSlackBusOn = automaticSlackBusOn;
        return this;
    }

    public Double getDsoVoltageLevel() {
        return dsoVoltageLevel;
    }

    public DynaFlowParameters setDsoVoltageLevel(double dsoVoltageLevel) {
        this.dsoVoltageLevel = dsoVoltageLevel;
        return this;
    }

    public List<String> getChosenOutputs() {
        return chosenOutputs;
    }

    public DynaFlowParameters setChosenOutputs(List<String> chosenOutputs) {
        this.chosenOutputs = chosenOutputs;
        return this;
    }

    public Boolean getVscAsGenerators() {
        return vscAsGenerators;
    }

    public DynaFlowParameters setVscAsGenerators(boolean vscAsGenerators) {
        this.vscAsGenerators = vscAsGenerators;
        return this;
    }

    public Boolean getLccAsLoads() {
        return lccAsLoads;
    }

    public DynaFlowParameters setLccAsLoads(boolean lccAsLoads) {
        this.lccAsLoads = lccAsLoads;
        return this;
    }

    public Double getTimeStep() {
        return timeStep;
    }

    public DynaFlowParameters setTimeStep(double timeStep) {
        this.timeStep = timeStep;
        return this;
    }

    @Override
    public String getName() {
        return "DynaFlowParameters";
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("").omitNullValues()
                .add(SVC_REGULATION_ON, svcRegulationOn)
                .add(SHUNT_REGULATION_ON, shuntRegulationOn)
                .add(AUTOMATIC_SLACK_BUS_ON, automaticSlackBusOn)
                .add(DSO_VOLTAGE_LEVEL, dsoVoltageLevel)
                .add(CHOSEN_OUTPUTS, chosenOutputs)
                .add(VSC_AS_GENERATORS, vscAsGenerators)
                .add(LCC_AS_LOADS, lccAsLoads)
                .add(TIME_STEP, timeStep)
                .toString();
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
        if (config.hasProperty(SVC_REGULATION_ON)) {
            parameters.setSvcRegulationOn(config.getBooleanProperty(SVC_REGULATION_ON));
        }
        if (config.hasProperty(SHUNT_REGULATION_ON)) {
            parameters.setShuntRegulationOn(config.getBooleanProperty(SHUNT_REGULATION_ON));
        }
        if (config.hasProperty(AUTOMATIC_SLACK_BUS_ON)) {
            parameters.setAutomaticSlackBusOn(config.getBooleanProperty(AUTOMATIC_SLACK_BUS_ON));
        }
        if (config.hasProperty(DSO_VOLTAGE_LEVEL)) {
            parameters.setDsoVoltageLevel(config.getDoubleProperty(DSO_VOLTAGE_LEVEL));
        }
        if (config.hasProperty(CHOSEN_OUTPUTS)) {
            parameters.setChosenOutputs(config.getStringListProperty(CHOSEN_OUTPUTS));
        }
        if (config.hasProperty(VSC_AS_GENERATORS)) {
            parameters.setVscAsGenerators(config.getBooleanProperty(VSC_AS_GENERATORS));
        }
        if (config.hasProperty(LCC_AS_LOADS)) {
            parameters.setLccAsLoads(config.getBooleanProperty(LCC_AS_LOADS));
        }
        if (config.hasProperty(TIME_STEP)) {
            parameters.setTimeStep(config.getDoubleProperty(TIME_STEP));
        }
    }

    public void update(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(SVC_REGULATION_ON)).ifPresent(prop -> setSvcRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(SHUNT_REGULATION_ON)).ifPresent(prop -> setShuntRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(AUTOMATIC_SLACK_BUS_ON)).ifPresent(prop -> setAutomaticSlackBusOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(DSO_VOLTAGE_LEVEL)).ifPresent(prop -> setDsoVoltageLevel(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(CHOSEN_OUTPUTS)).ifPresent(prop ->
                setChosenOutputs(Stream.of(prop.split(CHOSEN_OUTPUT_STRING_DELIMITER)).map(String::trim).collect(Collectors.toList())));
        Optional.ofNullable(properties.get(VSC_AS_GENERATORS)).ifPresent(prop -> setVscAsGenerators(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(LCC_AS_LOADS)).ifPresent(prop -> setLccAsLoads(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(TIME_STEP)).ifPresent(prop -> setTimeStep(Double.parseDouble(prop)));
    }

    public static DynaFlowParameters load(Map<String, String> properties) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        parameters.update(properties);
        return parameters;
    }

    public static List<String> getSpecificParametersNames() {
        return Arrays.asList(
                SVC_REGULATION_ON, SHUNT_REGULATION_ON, AUTOMATIC_SLACK_BUS_ON, DSO_VOLTAGE_LEVEL,
                CHOSEN_OUTPUTS, VSC_AS_GENERATORS, LCC_AS_LOADS, TIME_STEP
        );
    }

}
