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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;
import static com.powsybl.dynaflow.DynaFlowConstants.OutputTypes;

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

    public static final boolean DEFAULT_SVC_REGULATION_ON = false;
    public static final boolean DEFAULT_SHUNT_REGULATION_ON = false;
    public static final boolean DEFAULT_AUTOMATIC_SLACK_BUS_ON = false;
    public static final double DEFAULT_DSO_VOLTAGE_LEVEL = 45.0;

    public static final boolean DEFAULT_VSC_AS_GENERATORS = true;
    public static final boolean DEFAULT_LCC_AS_LOADS = true;

    public static final double DEFAULT_TIME_STEP = 2.6;

    public static final List<String> DEFAULT_CHOSEN_OUTPUT = Collections.singletonList(OutputTypes.STEADYSTATE.name());

    private boolean svcRegulationOn = DEFAULT_SVC_REGULATION_ON;
    private boolean shuntRegulationOn = DEFAULT_SHUNT_REGULATION_ON;
    private boolean automaticSlackBusOn = DEFAULT_AUTOMATIC_SLACK_BUS_ON;
    private double dsoVoltageLevel = DEFAULT_DSO_VOLTAGE_LEVEL;

    private List<String> chosenOutputs = DEFAULT_CHOSEN_OUTPUT;

    private boolean vscAsGenerators = DEFAULT_VSC_AS_GENERATORS;

    private boolean lccAsLoads = DEFAULT_LCC_AS_LOADS;

    private double timeStep = DEFAULT_TIME_STEP;

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

    public List<String> getChosenOutputs() {
        return chosenOutputs;
    }

    public DynaFlowParameters setChosenOutputs(List<String> chosenOutputs) {
        this.chosenOutputs = chosenOutputs;
        return this;
    }

    public boolean getVscAsGenerators() {
        return vscAsGenerators;
    }

    public DynaFlowParameters setVscAsGenerators(boolean vscAsGenerators) {
        this.vscAsGenerators = vscAsGenerators;
        return this;
    }

    public boolean getLccAsLoads() {
        return lccAsLoads;
    }

    public DynaFlowParameters setLccAsLoads(boolean lccAsLoads) {
        this.lccAsLoads = lccAsLoads;
        return this;
    }

    public double getTimeStep() {
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
        ImmutableMap.Builder<String, Object> immutableMapBuilder = ImmutableMap.builder();
        immutableMapBuilder
                .put(SVC_REGULATION_ON, svcRegulationOn)
                .put(SHUNT_REGULATION_ON, shuntRegulationOn)
                .put(AUTOMATIC_SLACK_BUS_ON, automaticSlackBusOn)
                .put(DSO_VOLTAGE_LEVEL, dsoVoltageLevel)
                .put(CHOSEN_OUTPUTS, chosenOutputs)
                .put(VSC_AS_GENERATORS, vscAsGenerators)
                .put(LCC_AS_LOADS, lccAsLoads)
                .put(TIME_STEP, timeStep);

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
                .setDsoVoltageLevel(config.getDoubleProperty(DSO_VOLTAGE_LEVEL, DEFAULT_DSO_VOLTAGE_LEVEL))
                .setChosenOutputs(config.getStringListProperty(CHOSEN_OUTPUTS, DEFAULT_CHOSEN_OUTPUT))
                .setVscAsGenerators(config.getBooleanProperty(VSC_AS_GENERATORS, DEFAULT_VSC_AS_GENERATORS))
                .setLccAsLoads(config.getBooleanProperty(LCC_AS_LOADS, DEFAULT_LCC_AS_LOADS))
                .setTimeStep(config.getDoubleProperty(TIME_STEP, DEFAULT_TIME_STEP));
    }

    public void loading(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(SVC_REGULATION_ON)).ifPresent(prop -> setSvcRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(SHUNT_REGULATION_ON)).ifPresent(prop -> setShuntRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(AUTOMATIC_SLACK_BUS_ON)).ifPresent(prop -> setAutomaticSlackBusOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(DSO_VOLTAGE_LEVEL)).ifPresent(prop -> setDsoVoltageLevel(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(CHOSEN_OUTPUTS)).ifPresent(prop ->
                setChosenOutputs(Stream.of(prop.replaceAll("^\\[|\\]$", "").split(CHOSEN_OUTPUT_STRING_DELIMITER)).map(String::trim).collect(Collectors.toList())));
        Optional.ofNullable(properties.get(VSC_AS_GENERATORS)).ifPresent(prop -> setVscAsGenerators(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(LCC_AS_LOADS)).ifPresent(prop -> setLccAsLoads(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(TIME_STEP)).ifPresent(prop -> setTimeStep(Double.parseDouble(prop)));
    }

    public static DynaFlowParameters load(Map<String, String> properties) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        parameters.loading(properties);
        return parameters;
    }

    public static List<String> getSpecificParametersNames() {
        return Arrays.asList(
                SVC_REGULATION_ON, SHUNT_REGULATION_ON, AUTOMATIC_SLACK_BUS_ON, DSO_VOLTAGE_LEVEL,
                CHOSEN_OUTPUTS, VSC_AS_GENERATORS, LCC_AS_LOADS, TIME_STEP
        );
    }

}
