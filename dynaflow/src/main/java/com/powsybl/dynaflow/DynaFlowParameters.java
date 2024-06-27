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
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.parameters.ParameterScope;
import com.powsybl.commons.parameters.ParameterType;
import com.powsybl.dynaflow.DynaFlowConstants.OutputTypes;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.dynaflow.DynaFlowConstants.ActivePowerCompensation;
import com.powsybl.dynaflow.DynaFlowConstants.StartingPointMode;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;

/**
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
public class DynaFlowParameters extends AbstractExtension<LoadFlowParameters> {

    /**
     * Inner class dedicated to Security Analysis (SA) namespace
     */
    public static class Sa {
        private static final String SECURITY_ANALYSIS = "sa"; //Security analysis
        private static final double DEFAULT_TIME_OF_EVENT = 10d;
        protected static final String TIME_OF_EVENT = "timeOfEvent";

        private Double timeOfEvent = DEFAULT_TIME_OF_EVENT;

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
    private static final String PRECISION = "precision";
    private static final String CHOSEN_OUTPUTS = "chosenOutputs";
    private static final String TIME_STEP = "timeStep";
    private static final String STARTING_POINT_MODE = "startingPointMode";
    private static final String MERGE_LOADS = "mergeLoads";

    // Default values
    private static final boolean DEFAULT_SVC_REGULATION_ON = true;
    private static final boolean DEFAULT_SHUNT_REGULATION_ON = true;
    private static final boolean DEFAULT_AUTOMATIC_SLACK_BUS_ON = true;
    private static final double DEFAULT_DSO_VOLTAGE_LEVEL = 45d;
    private static final ActivePowerCompensation DEFAULT_ACTIVE_POWER_COMPENSATION = ActivePowerCompensation.PMAX;
    private static final double DEFAULT_START_TIME = 0d;
    private static final double DEFAULT_STOP_TIME = 100d;
    private static final double DEFAULT_PRECISION = Double.NaN;
    private static final EnumSet<OutputTypes> DEFAULT_CHOSEN_OUTPUTS = EnumSet.of(OutputTypes.TIMELINE);
    private static final double DEFAULT_TIME_STEP = 10d;
    private static final StartingPointMode DEFAULT_STARTING_POINT_MODE = StartingPointMode.WARM;
    private static final boolean DEFAULT_MERGE_LOADS = true;

    private static <E extends Enum<E>> List<Object> getEnumPossibleValues(Class<E> enumClass) {
        return EnumSet.allOf(enumClass).stream().map(Enum::name).collect(Collectors.toList());
    }

    public static final List<Parameter> SPECIFIC_PARAMETERS = List.of(
            new Parameter(SVC_REGULATION_ON, ParameterType.BOOLEAN, "Static Var Compensator regulation on", DEFAULT_SVC_REGULATION_ON),
            new Parameter(SHUNT_REGULATION_ON, ParameterType.BOOLEAN, "Shunt compensator regulation on", DEFAULT_SHUNT_REGULATION_ON),
            new Parameter(AUTOMATIC_SLACK_BUS_ON, ParameterType.BOOLEAN, "Automatic slack bus selection on", DEFAULT_AUTOMATIC_SLACK_BUS_ON),
            new Parameter(DSO_VOLTAGE_LEVEL, ParameterType.DOUBLE, "DSO voltage level threshold", DEFAULT_DSO_VOLTAGE_LEVEL),
            new Parameter(ACTIVE_POWER_COMPENSATION, ParameterType.STRING, "Active power compensation mode", DEFAULT_ACTIVE_POWER_COMPENSATION.name(), getEnumPossibleValues(ActivePowerCompensation.class)),
            new Parameter(SETTING_PATH, ParameterType.STRING, "Setting file path", null, null, ParameterScope.TECHNICAL),
            new Parameter(ASSEMBLING_PATH, ParameterType.STRING, "Assembling file path", null, null, ParameterScope.TECHNICAL),
            new Parameter(START_TIME, ParameterType.DOUBLE, "Start time", DEFAULT_START_TIME),
            new Parameter(STOP_TIME, ParameterType.DOUBLE, "Stop time", DEFAULT_STOP_TIME),
            new Parameter(PRECISION, ParameterType.DOUBLE, "Precision", DEFAULT_PRECISION),
            new Parameter(Sa.TIME_OF_EVENT, ParameterType.DOUBLE, "Time of event", Sa.DEFAULT_TIME_OF_EVENT),
            new Parameter(CHOSEN_OUTPUTS, ParameterType.STRING_LIST, "Chosen outputs", DEFAULT_CHOSEN_OUTPUTS.stream().map(OutputTypes::name).toList(), getEnumPossibleValues(OutputTypes.class), ParameterScope.TECHNICAL),
            new Parameter(TIME_STEP, ParameterType.DOUBLE, "Time step", DEFAULT_TIME_STEP),
            new Parameter(STARTING_POINT_MODE, ParameterType.STRING, "Starting point mode", DEFAULT_STARTING_POINT_MODE.name(), getEnumPossibleValues(StartingPointMode.class)),
            new Parameter(MERGE_LOADS, ParameterType.BOOLEAN, "Merge loads connected to same bus", DEFAULT_MERGE_LOADS));

    private boolean svcRegulationOn = DEFAULT_SVC_REGULATION_ON;
    private boolean shuntRegulationOn = DEFAULT_SHUNT_REGULATION_ON;
    private boolean automaticSlackBusOn = DEFAULT_AUTOMATIC_SLACK_BUS_ON;
    private double dsoVoltageLevel = DEFAULT_DSO_VOLTAGE_LEVEL;
    private ActivePowerCompensation activePowerCompensation = DEFAULT_ACTIVE_POWER_COMPENSATION;
    private String settingPath = null;
    private String assemblingPath = null;
    private double startTime = DEFAULT_START_TIME;
    private double stopTime = DEFAULT_STOP_TIME;
    private Double precision = null;
    private Sa securityAnalysis = null;
    private EnumSet<OutputTypes> chosenOutputs = DEFAULT_CHOSEN_OUTPUTS;
    private double timeStep = DEFAULT_TIME_STEP;
    private StartingPointMode startingPointMode = DEFAULT_STARTING_POINT_MODE;
    private boolean mergeLoads = DEFAULT_MERGE_LOADS;

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

    public Set<OutputTypes> getChosenOutputs() {
        return chosenOutputs;
    }

    public DynaFlowParameters setChosenOutputs(Set<OutputTypes> chosenOutputs) {
        this.chosenOutputs = EnumSet.copyOf(chosenOutputs);
        return this;
    }

    public DynaFlowParameters addChosenOutput(OutputTypes chosenOutput) {
        this.chosenOutputs.add(chosenOutput);
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

    public boolean isMergeLoads() {
        return mergeLoads;
    }

    public DynaFlowParameters setMergeLoads(boolean mergeLoads) {
        this.mergeLoads = mergeLoads;
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
                .add(PRECISION, precision)
                .add(Sa.SECURITY_ANALYSIS, securityAnalysis)
                .add(CHOSEN_OUTPUTS, chosenOutputs)
                .add(TIME_STEP, timeStep)
                .add(STARTING_POINT_MODE, startingPointMode)
                .add(MERGE_LOADS, mergeLoads)
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
        config.getOptionalBooleanProperty(SVC_REGULATION_ON).ifPresent(parameters::setSvcRegulationOn);
        config.getOptionalBooleanProperty(SHUNT_REGULATION_ON).ifPresent(parameters::setShuntRegulationOn);
        config.getOptionalBooleanProperty(AUTOMATIC_SLACK_BUS_ON).ifPresent(parameters::setAutomaticSlackBusOn);
        config.getOptionalDoubleProperty(DSO_VOLTAGE_LEVEL).ifPresent(parameters::setDsoVoltageLevel);
        config.getOptionalEnumProperty(ACTIVE_POWER_COMPENSATION, ActivePowerCompensation.class).ifPresent(parameters::setActivePowerCompensation);
        config.getOptionalStringProperty(SETTING_PATH).ifPresent(parameters::setSettingPath);
        config.getOptionalStringProperty(ASSEMBLING_PATH).ifPresent(parameters::setAssemblingPath);
        config.getOptionalDoubleProperty(START_TIME).ifPresent(parameters::setStartTime);
        config.getOptionalDoubleProperty(STOP_TIME).ifPresent(parameters::setStopTime);
        config.getOptionalDoubleProperty(PRECISION).ifPresent(parameters::setPrecision);
        config.getOptionalDoubleProperty(Sa.TIME_OF_EVENT).ifPresent(parameters::setTimeOfEvent);
        config.getOptionalEnumSetProperty(CHOSEN_OUTPUTS, OutputTypes.class).ifPresent(parameters::setChosenOutputs);
        config.getOptionalDoubleProperty(TIME_STEP).ifPresent(parameters::setTimeStep);
        config.getOptionalStringProperty(STARTING_POINT_MODE).map(StartingPointMode::fromString).ifPresent(parameters::setStartingPointMode);
        config.getOptionalBooleanProperty(MERGE_LOADS).ifPresent(parameters::setMergeLoads);
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
        Optional.ofNullable(properties.get(PRECISION)).ifPresent(prop -> setPrecision(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(Sa.TIME_OF_EVENT)).ifPresent(prop -> {
            if (securityAnalysis == null) {
                securityAnalysis = new Sa();
            }
            securityAnalysis.setTimeOfEvent(Double.parseDouble(prop));
        });
        Optional.ofNullable(properties.get(CHOSEN_OUTPUTS)).ifPresent(prop ->
                setChosenOutputs(Stream.of(prop.split(CHOSEN_OUTPUT_STRING_DELIMITER)).map(o -> OutputTypes.valueOf(o.trim())).collect(Collectors.toSet())));
        Optional.ofNullable(properties.get(TIME_STEP)).ifPresent(prop -> setTimeStep(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(STARTING_POINT_MODE)).ifPresent(prop -> setStartingPointMode(StartingPointMode.fromString(prop)));
        Optional.ofNullable(properties.get(MERGE_LOADS)).ifPresent(prop -> setMergeLoads(Boolean.parseBoolean(prop)));
    }

    public Map<String, String> createMapFromParameters() {
        Map<String, String> parameters = new HashMap<>();
        addNotNullEntry(SVC_REGULATION_ON, svcRegulationOn, parameters::put);
        addNotNullEntry(SHUNT_REGULATION_ON, shuntRegulationOn, parameters::put);
        addNotNullEntry(AUTOMATIC_SLACK_BUS_ON, automaticSlackBusOn, parameters::put);
        addNotNullEntry(DSO_VOLTAGE_LEVEL, dsoVoltageLevel, parameters::put);
        if (activePowerCompensation != null) {
            parameters.put(ACTIVE_POWER_COMPENSATION, activePowerCompensation.name());
        }
        addNotNullEntry(SETTING_PATH, settingPath, parameters::put);
        addNotNullEntry(ASSEMBLING_PATH, assemblingPath, parameters::put);
        addNotNullEntry(START_TIME, startTime, parameters::put);
        addNotNullEntry(STOP_TIME, stopTime, parameters::put);
        addNotNullEntry(PRECISION, precision, parameters::put);
        addNotNullEntry(Sa.TIME_OF_EVENT, getTimeOfEvent(), parameters::put);
        if (!chosenOutputs.isEmpty()) {
            parameters.put(CHOSEN_OUTPUTS, String.join(CHOSEN_OUTPUT_STRING_DELIMITER, chosenOutputs.stream().map(OutputTypes::name).toList()));
        }
        addNotNullEntry(TIME_STEP, timeStep, parameters::put);
        addNotNullEntry(STARTING_POINT_MODE, startingPointMode, parameters::put);
        addNotNullEntry(MERGE_LOADS, mergeLoads, parameters::put);
        return parameters;
    }

    private void addNotNullEntry(String key, Object value, BiConsumer<String, String> adder) {
        if (value != null) {
            adder.accept(key, Objects.toString(value));
        }
    }

    public static DynaFlowParameters load(Map<String, String> properties) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        parameters.update(properties);
        return parameters;
    }
}
