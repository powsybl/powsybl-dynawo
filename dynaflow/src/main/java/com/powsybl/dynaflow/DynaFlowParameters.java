/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.MoreObjects;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.dynaflow.DynaFlowConstants.ActivePowerCompensation;
import com.powsybl.dynaflow.DynaFlowConstants.StartingPointMode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowParameters extends AbstractExtension<LoadFlowParameters> {

    /**
     * Inner class dedicated to Security Analysis (SA) namespace
     */
    public static class Sa {
        private static final String SECURITY_ANALYSIS = "sa"; //Security analysis
        private static final String TIME_OF_EVENT = "timeOfEvent";

        private Double timeOfEvent = null;

        public Double getTimeOfEvent() {
            return timeOfEvent;
        }

        public void setTimeOfEvent(Double timeOfEvent) {
            this.timeOfEvent = timeOfEvent;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("").omitNullValues()
                    .add(TIME_OF_EVENT, timeOfEvent).toString();
        }

        public static void writeJson(JsonGenerator jsonGenerator, DynaFlowParameters dynaFlowParameters) throws IOException {
            if (dynaFlowParameters.getSa().isSerializable()) {
                jsonGenerator.writeObjectFieldStart("sa");
                jsonGenerator.writeNumberField("TimeOfEvent", dynaFlowParameters.getTimeOfEvent());
                jsonGenerator.writeEndObject();
            }
        }

        @JsonIgnore
        public boolean isSerializable() {
            return timeOfEvent != null;
        }
    }

    private static final String CHOSEN_OUTPUT_STRING_DELIMITER = ",";
    private static final String SVC_REGULATION_ON = "svcRegulationOn";
    private static final String SHUNT_REGULATION_ON = "shuntRegulationOn";
    private static final String AUTOMATIC_SLACK_BUS_ON = "automaticSlackBusOn";
    private static final String DSO_VOLTAGE_LEVEL = "dsoVoltageLevel";
    private static final String ACTIVE_POWER_COMPENSATION = "activePowerCompensation";
    private static final String SETTING_PATH = "settingPath";
    private static final String ASSEMBLING_PATH = "assemblingPath";
    private static final String START_TIME = "startTime";
    private static final String STOP_TIME = "stopTime";
    private static final String PRECISION_NAME = "precision";
    private static final String CHOSEN_OUTPUTS = "chosenOutputs";
    private static final String TIME_STEP = "timeStep";
    private static final String STARTING_POINT_MODE = "startingPointMode";

    private Boolean svcRegulationOn = null;
    private Boolean shuntRegulationOn = null;
    private Boolean automaticSlackBusOn = null;
    private Double dsoVoltageLevel = null;
    private ActivePowerCompensation activePowerCompensation = null;
    private String settingPath = null;
    private String assemblingPath = null;
    private Double startTime = null;
    private Double stopTime = null;
    private Double precision = null;
    private Sa securityAnalysis = null;
    private List<String> chosenOutputs = null;
    private Double timeStep = null;
    private StartingPointMode startingPointMode = null;

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

    public ActivePowerCompensation getActivePowerCompensation() {
        return activePowerCompensation;
    }

    public DynaFlowParameters setActivePowerCompensation(ActivePowerCompensation activePowerCompensation) {
        this.activePowerCompensation = activePowerCompensation;
        return this;
    }

    public String getSettingPath() {
        return settingPath;
    }

    public DynaFlowParameters setSettingPath(String settingPath) {
        this.settingPath = settingPath;
        return this;
    }

    public String getAssemblingPath() {
        return assemblingPath;
    }

    public DynaFlowParameters setAssemblingPath(String assemblingPath) {
        this.assemblingPath = assemblingPath;
        return this;
    }

    public Double getStartTime() {
        return startTime;
    }

    public DynaFlowParameters setStartTime(Double startTime) {
        this.startTime = startTime;
        return this;
    }

    public Double getStopTime() {
        return stopTime;
    }

    public DynaFlowParameters setStopTime(Double stopTime) {
        this.stopTime = stopTime;
        return this;
    }

    public Double getPrecision() {
        return precision;
    }

    public DynaFlowParameters setPrecision(Double precision) {
        this.precision = precision;
        return this;
    }

    @JsonIgnore
    public Double getTimeOfEvent() {
        return securityAnalysis == null ? null : securityAnalysis.getTimeOfEvent();
    }

    public DynaFlowParameters setTimeOfEvent(Double timeOfEvent) {
        if (this.securityAnalysis == null) {
            securityAnalysis = new Sa();
        }
        securityAnalysis.setTimeOfEvent(timeOfEvent);
        return this;
    }

    public List<String> getChosenOutputs() {
        return chosenOutputs;
    }

    public DynaFlowParameters setChosenOutputs(List<String> chosenOutputs) {
        this.chosenOutputs = chosenOutputs;
        return this;
    }

    public Double getTimeStep() {
        return timeStep;
    }

    public DynaFlowParameters setTimeStep(double timeStep) {
        this.timeStep = timeStep;
        return this;
    }

    public StartingPointMode getStartingPointMode() {
        return startingPointMode;
    }

    public DynaFlowParameters setStartingPointMode(StartingPointMode startingPointMode) {
        this.startingPointMode = startingPointMode;
        return this;
    }

    public Sa getSa() {
        return this.securityAnalysis;
    }

    public DynaFlowParameters setSa(Sa securityAnalysis) {
        this.securityAnalysis = securityAnalysis;
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
                .add(ACTIVE_POWER_COMPENSATION, activePowerCompensation)
                .add(SETTING_PATH, settingPath)
                .add(ASSEMBLING_PATH, assemblingPath)
                .add(START_TIME, startTime)
                .add(STOP_TIME, stopTime)
                .add(PRECISION_NAME, precision)
                .add(Sa.SECURITY_ANALYSIS, securityAnalysis)
                .add(CHOSEN_OUTPUTS, chosenOutputs)
                .add(TIME_STEP, timeStep)
                .add(STARTING_POINT_MODE, startingPointMode)
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
        if (config.hasProperty(ACTIVE_POWER_COMPENSATION)) {
            parameters.setActivePowerCompensation(config.getEnumProperty(ACTIVE_POWER_COMPENSATION, ActivePowerCompensation.class));
        }
        if (config.hasProperty(SETTING_PATH)) {
            parameters.setSettingPath(config.getStringProperty(SETTING_PATH));
        }
        if (config.hasProperty(ASSEMBLING_PATH)) {
            parameters.setAssemblingPath(config.getStringProperty(ASSEMBLING_PATH));
        }
        if (config.hasProperty(START_TIME)) {
            parameters.setStartTime(config.getDoubleProperty(START_TIME));
        }
        if (config.hasProperty(STOP_TIME)) {
            parameters.setStopTime(config.getDoubleProperty(STOP_TIME));
        }
        if (config.hasProperty(PRECISION_NAME)) {
            parameters.setPrecision(config.getDoubleProperty(PRECISION_NAME));
        }
        if (config.hasProperty(Sa.TIME_OF_EVENT)) {
            parameters.setTimeOfEvent(config.getDoubleProperty(Sa.TIME_OF_EVENT));
        }
        if (config.hasProperty(CHOSEN_OUTPUTS)) {
            parameters.setChosenOutputs(config.getStringListProperty(CHOSEN_OUTPUTS));
        }
        if (config.hasProperty(TIME_STEP)) {
            parameters.setTimeStep(config.getDoubleProperty(TIME_STEP));
        }
        if (config.hasProperty(STARTING_POINT_MODE)) {
            parameters.setStartingPointMode(StartingPointMode.fromString(config.getStringProperty(STARTING_POINT_MODE)));
        }
    }

    public void update(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(SVC_REGULATION_ON)).ifPresent(prop -> setSvcRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(SHUNT_REGULATION_ON)).ifPresent(prop -> setShuntRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(AUTOMATIC_SLACK_BUS_ON)).ifPresent(prop -> setAutomaticSlackBusOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(DSO_VOLTAGE_LEVEL)).ifPresent(prop -> setDsoVoltageLevel(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(ACTIVE_POWER_COMPENSATION)).ifPresent(prop -> setActivePowerCompensation(ActivePowerCompensation.valueOf(prop)));
        Optional.ofNullable(properties.get(SETTING_PATH)).ifPresent(this::setSettingPath);
        Optional.ofNullable(properties.get(ASSEMBLING_PATH)).ifPresent(this::setAssemblingPath);
        Optional.ofNullable(properties.get(START_TIME)).ifPresent(prop -> setStartTime(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(STOP_TIME)).ifPresent(prop -> setStopTime(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(PRECISION_NAME)).ifPresent(prop -> setPrecision(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(Sa.TIME_OF_EVENT)).ifPresent(prop -> {
            if (securityAnalysis == null) {
                securityAnalysis = new Sa();
            }
            securityAnalysis.setTimeOfEvent(Double.parseDouble(prop));
        });
        Optional.ofNullable(properties.get(CHOSEN_OUTPUTS)).ifPresent(prop ->
                setChosenOutputs(Stream.of(prop.split(CHOSEN_OUTPUT_STRING_DELIMITER)).map(String::trim).collect(Collectors.toList())));
        Optional.ofNullable(properties.get(TIME_STEP)).ifPresent(prop -> setTimeStep(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(STARTING_POINT_MODE)).ifPresent(prop -> setStartingPointMode(StartingPointMode.fromString(prop)));
    }

    public static DynaFlowParameters load(Map<String, String> properties) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        parameters.update(properties);
        return parameters;
    }

    public static List<String> getSpecificParametersNames() {
        return Arrays.asList(
                SVC_REGULATION_ON, SHUNT_REGULATION_ON, AUTOMATIC_SLACK_BUS_ON, DSO_VOLTAGE_LEVEL,
                ACTIVE_POWER_COMPENSATION, SETTING_PATH, ASSEMBLING_PATH, START_TIME, STOP_TIME, PRECISION_NAME,
                Sa.TIME_OF_EVENT, CHOSEN_OUTPUTS, TIME_STEP, STARTING_POINT_MODE
        );
    }

}
