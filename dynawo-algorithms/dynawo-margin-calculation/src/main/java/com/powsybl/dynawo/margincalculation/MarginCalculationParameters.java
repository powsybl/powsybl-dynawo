/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoSimulationParameters;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MarginCalculationParameters {

    public static final double DEFAULT_START_TIME = 0;
    public static final double DEFAULT_STOP_TIME = 200;
    public static final double DEFAULT_MARGIN_CALCULATION_START_TIME = 100;
    public static final double DEFAULT_LOAD_INCREASE_START_TIME = 10;
    public static final double DEFAULT_LOAD_INCREASE_STOP_TIME = 50;
    public static final double DEFAULT_CONTINGENCIES_START_TIME = 120;
    public static final CalculationType DEFAULT_CALCULATION_TYPE = CalculationType.GLOBAL_MARGIN;
    public static final int DEFAULT_ACCURACY = 2;
    public static final LoadModelsRule DEFAULT_LOAD_MODELS_RULE = LoadModelsRule.ALL_LOADS;

    public enum CalculationType {
        GLOBAL_MARGIN,
        LOCAL_MARGIN
    }

    /**
     * Indicates how to handle load dynamic models in the first phase
     */
    public enum LoadModelsRule {
        /**
         * Remove every specific loads dynamic models
         */
        ALL_LOADS,
        /**
         * Remove dynamic models on loads affected by a load variation
         */
        TARGETED_LOADS
    }

    public static class Builder {

        private double startTime = DEFAULT_START_TIME;
        private double stopTime = DEFAULT_STOP_TIME;
        private double marginCalculationStartTime = DEFAULT_MARGIN_CALCULATION_START_TIME;
        private double loadIncreaseStartTime = DEFAULT_LOAD_INCREASE_START_TIME;
        private double loadIncreaseStopTime = DEFAULT_LOAD_INCREASE_STOP_TIME;
        private double contingenciesStartTime = DEFAULT_CONTINGENCIES_START_TIME;
        private CalculationType calculationType = DEFAULT_CALCULATION_TYPE;
        private int accuracy = DEFAULT_ACCURACY;
        private LoadModelsRule loadModelsRule = DEFAULT_LOAD_MODELS_RULE;
        private DynawoSimulationParameters dynawoParameters = new DynawoSimulationParameters();
        private String debugDir;

        /**
         * Set dynamic simulation start time, must be greater than 0
         */
        public Builder setStartTime(double startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * Set dynamic simulation stop time, must be after {@link #startTime}
         */
        public Builder setStopTime(double stopTime) {
            this.stopTime = stopTime;
            return this;
        }

        /**
         * Set margin calculation phase start time, must be between {@link #startTime} and {@link #stopTime}
         */
        public Builder setMarginCalculationStartTime(double marginCalculationStartTime) {
            this.marginCalculationStartTime = marginCalculationStartTime;
            return this;
        }

        /**
         * Set load increase start time, must be between {@link #startTime} and {@link #marginCalculationStartTime}
         */
        public Builder setLoadIncreaseStartTime(double loadIncreaseStartTime) {
            this.loadIncreaseStartTime = loadIncreaseStartTime;
            return this;
        }

        /**
         * Set load increase stop time, must be after {@link #loadIncreaseStartTime}
         */
        public Builder setLoadIncreaseStopTime(double loadIncreaseStopTime) {
            this.loadIncreaseStopTime = loadIncreaseStopTime;
            return this;
        }

        /**
         * Set contingencies start time, must be between {@link #marginCalculationStartTime} and {@link #stopTime}
         */
        public Builder setContingenciesStartTime(double contingenciesStartTime) {
            this.contingenciesStartTime = contingenciesStartTime;
            return this;
        }

        public Builder setCalculationType(CalculationType calculationType) {
            this.calculationType = calculationType;
            return this;
        }

        public Builder setAccuracy(int accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder setLoadModelsRule(LoadModelsRule loadModelsRule) {
            this.loadModelsRule = loadModelsRule;
            return this;
        }

        public Builder setDynawoParameters(DynawoSimulationParameters dynawoParameters) {
            this.dynawoParameters = dynawoParameters;
            return this;
        }

        public Builder setDebugDir(String debugDir) {
            this.debugDir = debugDir;
            return this;
        }

        public MarginCalculationParameters build() {
            if (startTime < 0) {
                throw new IllegalStateException("Start time (%.2f) should be zero or positive".formatted(startTime));
            }
            if (stopTime <= startTime) {
                throw new IllegalStateException("Stop time (%.2f) should be greater than start time (%.2f)".formatted(stopTime, startTime));
            }
            if (marginCalculationStartTime <= startTime || marginCalculationStartTime >= stopTime) {
                throw new IllegalStateException("Margin calculation start time (%.2f) should be between start (%.2f) and stop time (%.2f)"
                        .formatted(marginCalculationStartTime, startTime, stopTime));
            }
            if (contingenciesStartTime <= marginCalculationStartTime || contingenciesStartTime >= stopTime) {
                throw new IllegalStateException("Contingencies start time (%.2f) should be between margin calculation start time (%.2f) and stop time (%.2f)"
                        .formatted(contingenciesStartTime, marginCalculationStartTime, stopTime));
            }
            if (loadIncreaseStartTime <= startTime) {
                throw new IllegalStateException("Load increase start time (%.2f) should be greater than start time (%.2f)"
                        .formatted(loadIncreaseStartTime, startTime));
            }
            if (loadIncreaseStopTime <= loadIncreaseStartTime || loadIncreaseStopTime >= marginCalculationStartTime) {
                throw new IllegalStateException("Load increase stop time (%.2f) should be between load increase start time (%.2f) and margin calculation start time (%.2f)"
                        .formatted(loadIncreaseStopTime, loadIncreaseStartTime, marginCalculationStartTime));
            }
            return new MarginCalculationParameters(this);
        }
    }

    /**
     * Creates a builder for MarginCalculationParameters with default values
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Loads parameters from the default platform configuration.
     */
    public static MarginCalculationParameters load() {
        return load(PlatformConfig.defaultConfig());
    }

    /**
     * Loads parameters from a provided platform configuration.
     */
    public static MarginCalculationParameters load(PlatformConfig platformConfig) {
        return load(platformConfig, FileSystems.getDefault());
    }

    public static MarginCalculationParameters load(PlatformConfig platformConfig, FileSystem fileSystem) {
        Builder builder = builder();
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig("margin-calculation-default-parameters");
        config.ifPresent(c -> {
            c.getOptionalDoubleProperty("startTime").ifPresent(builder::setStartTime);
            c.getOptionalDoubleProperty("stopTime").ifPresent(builder::setStopTime);
            c.getOptionalDoubleProperty("margin-calculation-start-time").ifPresent(builder::setMarginCalculationStartTime);
            c.getOptionalDoubleProperty("loadIncrease.startTime").ifPresent(builder::setLoadIncreaseStartTime);
            c.getOptionalDoubleProperty("loadIncrease.stopTime").ifPresent(builder::setLoadIncreaseStopTime);
            c.getOptionalDoubleProperty("contingencies-start-time").ifPresent(builder::setContingenciesStartTime);
            c.getOptionalEnumProperty("calculation-type", CalculationType.class).ifPresent(builder::setCalculationType);
            c.getOptionalIntProperty("accuracy").ifPresent(builder::setAccuracy);
            c.getOptionalEnumProperty("load-models-rule", LoadModelsRule.class).ifPresent(builder::setLoadModelsRule);
            c.getOptionalStringProperty("debugDir").ifPresent(builder::setDebugDir);
        });
        builder.setDynawoParameters(DynawoSimulationParameters.load(platformConfig, fileSystem));
        return builder.build();
    }

    private final double startTime;
    private final double stopTime;
    private final double marginCalculationStartTime;
    private final double loadIncreaseStartTime;
    private final double loadIncreaseStopTime;
    private final double contingenciesStartTime;
    private final CalculationType calculationType;
    private final int accuracy;
    private final LoadModelsRule loadModelsRule;
    private final DynawoSimulationParameters dynawoParameters;
    private final String debugDir;

    private MarginCalculationParameters(MarginCalculationParameters.Builder builder) {
        this.startTime = builder.startTime;
        this.stopTime = builder.stopTime;
        this.marginCalculationStartTime = builder.marginCalculationStartTime;
        this.loadIncreaseStartTime = builder.loadIncreaseStartTime;
        this.loadIncreaseStopTime = builder.loadIncreaseStopTime;
        this.contingenciesStartTime = builder.contingenciesStartTime;
        this.calculationType = builder.calculationType;
        this.accuracy = builder.accuracy;
        this.loadModelsRule = builder.loadModelsRule;
        this.dynawoParameters = builder.dynawoParameters;
        this.debugDir = builder.debugDir;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getStopTime() {
        return stopTime;
    }

    public double getMarginCalculationStartTime() {
        return marginCalculationStartTime;
    }

    public double getLoadIncreaseStartTime() {
        return loadIncreaseStartTime;
    }

    public double getLoadIncreaseStopTime() {
        return loadIncreaseStopTime;
    }

    public double getContingenciesStartTime() {
        return contingenciesStartTime;
    }

    public CalculationType getCalculationType() {
        return calculationType;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public LoadModelsRule getLoadModelsRule() {
        return loadModelsRule;
    }

    public DynawoSimulationParameters getDynawoParameters() {
        return dynawoParameters;
    }

    /**
     * Get the directory where execution files will be dumped
     *
     * @return the debug directory
     */
    public String getDebugDir() {
        return debugDir;
    }
}
