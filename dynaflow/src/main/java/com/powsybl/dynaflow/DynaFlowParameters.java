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
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.parameters.ParameterScope;
import com.powsybl.commons.parameters.ParameterType;
import com.powsybl.dynaflow.DynaFlowConstants.OutputTypes;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.dynaflow.DynaFlowConstants.ActivePowerCompensation;
import com.powsybl.dynaflow.DynaFlowConstants.StartingPointMode;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawo.commons.ParametersUtils.*;

/**
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
public class DynaFlowParameters extends AbstractExtension<LoadFlowParameters> {

    public static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-default-parameters";

    private static final Logger LOGGER = LoggerFactory.getLogger(DynaFlowParameters.class);
    private static final String SVC_REGULATION_ON = "svcRegulationOn";
    private static final String SHUNT_REGULATION_ON = "shuntRegulationOn";
    private static final String AUTOMATIC_SLACK_BUS_ON = "automaticSlackBusOn";
    private static final String DSO_VOLTAGE_LEVEL = "dsoVoltageLevel";
    private static final String TFO_VOLTAGE_LEVEL = "tfoVoltageLevel";
    private static final String ACTIVE_POWER_COMPENSATION = "activePowerCompensation";
    private static final String SETTING_PATH = "settingPath";
    private static final String ASSEMBLING_PATH = "assemblingPath";
    private static final String START_TIME = "startTime";
    private static final String STOP_TIME = "stopTime";
    private static final String PRECISION_NAME = "precision";
    private static final String CHOSEN_OUTPUTS = "chosenOutputs";
    private static final String TIME_STEP = "timeStep";
    private static final String STARTING_POINT_MODE = "startingPointMode";
    private static final String MERGE_LOADS = "mergeLoads";

    // Default values
    private static final boolean DEFAULT_SVC_REGULATION_ON = true;
    private static final boolean DEFAULT_SHUNT_REGULATION_ON = true;
    private static final boolean DEFAULT_AUTOMATIC_SLACK_BUS_ON = true;
    private static final double DEFAULT_DSO_VOLTAGE_LEVEL = 45d;
    private static final double DEFAULT_TFO_VOLTAGE_LEVEL = 100d;
    private static final ActivePowerCompensation DEFAULT_ACTIVE_POWER_COMPENSATION = ActivePowerCompensation.PMAX;
    private static final double DEFAULT_START_TIME = 0d;
    private static final double DEFAULT_STOP_TIME = 100d;
    private static final double DEFAULT_PRECISION = Double.NaN;
    private static final EnumSet<OutputTypes> DEFAULT_CHOSEN_OUTPUTS = EnumSet.of(OutputTypes.TIMELINE);
    private static final double DEFAULT_TIME_STEP = 10d;
    private static final StartingPointMode DEFAULT_STARTING_POINT_MODE = StartingPointMode.WARM;
    private static final boolean DEFAULT_MERGE_LOADS = true;

    public static final List<Parameter> SPECIFIC_PARAMETERS = List.of(
            new Parameter(SVC_REGULATION_ON, ParameterType.BOOLEAN, "Static Var Compensator regulation on", DEFAULT_SVC_REGULATION_ON),
            new Parameter(SHUNT_REGULATION_ON, ParameterType.BOOLEAN, "Shunt compensator regulation on", DEFAULT_SHUNT_REGULATION_ON),
            new Parameter(AUTOMATIC_SLACK_BUS_ON, ParameterType.BOOLEAN, "Automatic slack bus selection on", DEFAULT_AUTOMATIC_SLACK_BUS_ON),
            new Parameter(DSO_VOLTAGE_LEVEL, ParameterType.DOUBLE, "DSO voltage level threshold", DEFAULT_DSO_VOLTAGE_LEVEL),
            new Parameter(TFO_VOLTAGE_LEVEL, ParameterType.DOUBLE, "Transformers voltage level threshold", DEFAULT_TFO_VOLTAGE_LEVEL),
            new Parameter(ACTIVE_POWER_COMPENSATION, ParameterType.STRING, "Active power compensation mode", DEFAULT_ACTIVE_POWER_COMPENSATION.name(), getEnumPossibleValues(ActivePowerCompensation.class)),
            new Parameter(SETTING_PATH, ParameterType.STRING, "Setting file path", null, null, ParameterScope.TECHNICAL),
            new Parameter(ASSEMBLING_PATH, ParameterType.STRING, "Assembling file path", null, null, ParameterScope.TECHNICAL),
            new Parameter(START_TIME, ParameterType.DOUBLE, "Start time", DEFAULT_START_TIME),
            new Parameter(STOP_TIME, ParameterType.DOUBLE, "Stop time", DEFAULT_STOP_TIME),
            new Parameter(PRECISION_NAME, ParameterType.DOUBLE, "Precision", DEFAULT_PRECISION),
            new Parameter(CHOSEN_OUTPUTS, ParameterType.STRING_LIST, "Chosen outputs", DEFAULT_CHOSEN_OUTPUTS.stream().map(OutputTypes::name).toList(), getEnumPossibleValues(OutputTypes.class), ParameterScope.TECHNICAL),
            new Parameter(TIME_STEP, ParameterType.DOUBLE, "Time step", DEFAULT_TIME_STEP),
            new Parameter(STARTING_POINT_MODE, ParameterType.STRING, "Starting point mode", DEFAULT_STARTING_POINT_MODE.name(), getEnumPossibleValues(StartingPointMode.class)),
            new Parameter(MERGE_LOADS, ParameterType.BOOLEAN, "Merge loads connected to same bus", DEFAULT_MERGE_LOADS));

    private boolean svcRegulationOn = DEFAULT_SVC_REGULATION_ON;
    private boolean shuntRegulationOn = DEFAULT_SHUNT_REGULATION_ON;
    private boolean automaticSlackBusOn = DEFAULT_AUTOMATIC_SLACK_BUS_ON;
    private double dsoVoltageLevel = DEFAULT_DSO_VOLTAGE_LEVEL;
    private double tfoVoltageLevel = DEFAULT_TFO_VOLTAGE_LEVEL;
    private ActivePowerCompensation activePowerCompensation = DEFAULT_ACTIVE_POWER_COMPENSATION;
    private String settingPath = null;
    private String assemblingPath = null;
    private double startTime = DEFAULT_START_TIME;
    private double stopTime = DEFAULT_STOP_TIME;
    private Double precision = null;
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

    public Double getTfoVoltageLevel() {
        return tfoVoltageLevel;
    }

    public DynaFlowParameters setTfoVoltageLevel(double tfoVoltageLevel) {
        this.tfoVoltageLevel = tfoVoltageLevel;
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
                .add(TFO_VOLTAGE_LEVEL, tfoVoltageLevel)
                .add(ACTIVE_POWER_COMPENSATION, activePowerCompensation)
                .add(SETTING_PATH, settingPath)
                .add(ASSEMBLING_PATH, assemblingPath)
                .add(START_TIME, startTime)
                .add(STOP_TIME, stopTime)
                .add(PRECISION_NAME, precision)
                .add(CHOSEN_OUTPUTS, chosenOutputs)
                .add(TIME_STEP, timeStep)
                .add(STARTING_POINT_MODE, startingPointMode)
                .add(MERGE_LOADS, mergeLoads)
                .toString();
    }

    public static DynaFlowParameters load(PlatformConfig platformConfig) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        parameters.update(platformConfig);
        return parameters;
    }

    public void update(PlatformConfig platformConfig) {
        Objects.requireNonNull(platformConfig);
        platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS)
                .ifPresent(config -> load(this, config));
    }

    public static DynaFlowParameters load(ModuleConfig config) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        if (config != null) {
            load(parameters, config);
        }
        return parameters;
    }

    public static DynaFlowParameters load(Map<String, String> properties) {
        DynaFlowParameters parameters = new DynaFlowParameters();
        parameters.update(properties);
        return parameters;
    }

    private static void load(DynaFlowParameters parameters, ModuleConfig config) {
        config.getOptionalBooleanProperty(SVC_REGULATION_ON).ifPresent(parameters::setSvcRegulationOn);
        config.getOptionalBooleanProperty(SHUNT_REGULATION_ON).ifPresent(parameters::setShuntRegulationOn);
        config.getOptionalBooleanProperty(AUTOMATIC_SLACK_BUS_ON).ifPresent(parameters::setAutomaticSlackBusOn);
        config.getOptionalDoubleProperty(DSO_VOLTAGE_LEVEL).ifPresent(parameters::setDsoVoltageLevel);
        config.getOptionalDoubleProperty(TFO_VOLTAGE_LEVEL).ifPresent(parameters::setTfoVoltageLevel);
        config.getOptionalEnumProperty(ACTIVE_POWER_COMPENSATION, ActivePowerCompensation.class).ifPresent(parameters::setActivePowerCompensation);
        config.getOptionalStringProperty(SETTING_PATH).ifPresent(parameters::setSettingPath);
        config.getOptionalStringProperty(ASSEMBLING_PATH).ifPresent(parameters::setAssemblingPath);
        config.getOptionalDoubleProperty(START_TIME).ifPresent(parameters::setStartTime);
        config.getOptionalDoubleProperty(STOP_TIME).ifPresent(parameters::setStopTime);
        config.getOptionalDoubleProperty(PRECISION_NAME).ifPresent(parameters::setPrecision);
        config.getOptionalEnumSetProperty(CHOSEN_OUTPUTS, OutputTypes.class).ifPresent(parameters::setChosenOutputs);
        config.getOptionalDoubleProperty(TIME_STEP).ifPresent(parameters::setTimeStep);
        config.getOptionalStringProperty(STARTING_POINT_MODE).map(StartingPointMode::fromString).ifPresent(parameters::setStartingPointMode);
        config.getOptionalBooleanProperty(MERGE_LOADS).ifPresent(parameters::setMergeLoads);
    }

    public static void log(LoadFlowParameters parameters, DynaFlowParameters parametersExt) {
        if (LOGGER.isInfoEnabled()) {
            AsciiTable at = new AsciiTable();
            at.addRule();
            at.addRow("Name", "Value");
            at.addRule();
            for (var e : parameters.toMap().entrySet()) {
                at.addRow(e.getKey(), e.getValue());
            }
            for (var e : parametersExt.createMapFromParameters().entrySet()) {
                at.addRow(e.getKey(), Objects.toString(e.getValue(), ""));
            }
            at.addRule();
            at.getRenderer().setCWC(new CWC_LongestWord());
            at.setPaddingLeftRight(1, 1);
            LOGGER.info("Parameters:\n{}", at.render());
        }
    }

    public void update(Map<String, String> properties) {
        Objects.requireNonNull(properties);
        Optional.ofNullable(properties.get(SVC_REGULATION_ON)).ifPresent(prop -> setSvcRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(SHUNT_REGULATION_ON)).ifPresent(prop -> setShuntRegulationOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(AUTOMATIC_SLACK_BUS_ON)).ifPresent(prop -> setAutomaticSlackBusOn(Boolean.parseBoolean(prop)));
        Optional.ofNullable(properties.get(DSO_VOLTAGE_LEVEL)).ifPresent(prop -> setDsoVoltageLevel(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(TFO_VOLTAGE_LEVEL)).ifPresent(prop -> setTfoVoltageLevel(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(ACTIVE_POWER_COMPENSATION)).ifPresent(prop -> setActivePowerCompensation(ActivePowerCompensation.valueOf(prop)));
        Optional.ofNullable(properties.get(SETTING_PATH)).ifPresent(this::setSettingPath);
        Optional.ofNullable(properties.get(ASSEMBLING_PATH)).ifPresent(this::setAssemblingPath);
        Optional.ofNullable(properties.get(START_TIME)).ifPresent(prop -> setStartTime(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(STOP_TIME)).ifPresent(prop -> setStopTime(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(PRECISION_NAME)).ifPresent(prop -> setPrecision(Double.parseDouble(prop)));
        Optional.ofNullable(properties.get(CHOSEN_OUTPUTS)).ifPresent(prop ->
                setChosenOutputs(Stream.of(prop.split(PROPERTY_LIST_DELIMITER)).map(o -> OutputTypes.valueOf(o.trim())).collect(Collectors.toSet())));
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
        addNotNullEntry(TFO_VOLTAGE_LEVEL, tfoVoltageLevel, parameters::put);
        if (activePowerCompensation != null) {
            parameters.put(ACTIVE_POWER_COMPENSATION, activePowerCompensation.name());
        }
        addNotNullEntry(SETTING_PATH, settingPath, parameters::put);
        addNotNullEntry(ASSEMBLING_PATH, assemblingPath, parameters::put);
        addNotNullEntry(START_TIME, startTime, parameters::put);
        addNotNullEntry(STOP_TIME, stopTime, parameters::put);
        addNotNullEntry(PRECISION_NAME, precision, parameters::put);
        if (!chosenOutputs.isEmpty()) {
            parameters.put(CHOSEN_OUTPUTS, String.join(PROPERTY_LIST_DELIMITER, chosenOutputs.stream().map(OutputTypes::name).toList()));
        }
        addNotNullEntry(TIME_STEP, timeStep, parameters::put);
        addNotNullEntry(STARTING_POINT_MODE, startingPointMode, parameters::put);
        addNotNullEntry(MERGE_LOADS, mergeLoads, parameters::put);
        return parameters;
    }
}
