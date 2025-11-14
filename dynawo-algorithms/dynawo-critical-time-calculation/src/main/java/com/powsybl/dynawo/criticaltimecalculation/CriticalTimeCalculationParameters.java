/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoSimulationParameters;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Optional;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class CriticalTimeCalculationParameters {
    public static final double DEFAULT_START_TIME = 0;
    public static final double DEFAULT_STOP_TIME = 200;
    public static final double DEFAULT_MIN_VALUE = 0;
    public static final double DEFAULT_MAX_VALUE = 5;
    public static String DEFAULT_ELEMENT_ID = "FAULT";
    public static String DEFAULT_PAR_NAME = "fault_tEnd";
    public static final double DEFAULT_ACCURACY = 0.001;
    public static final Mode DEFAULT_MODE = Mode.SIMPLE;

    public enum Mode {
        SIMPLE,
        COMPLEX
    }
    public static class Builder {

        private double startTime = DEFAULT_START_TIME;
        private double stopTime = DEFAULT_STOP_TIME;
        private String elementId = DEFAULT_ELEMENT_ID;
        private String parName = DEFAULT_PAR_NAME;
        private double minValue = DEFAULT_MIN_VALUE;
        private double maxValue = DEFAULT_MAX_VALUE;
        private double accuracy = DEFAULT_ACCURACY;
        private Mode mode = DEFAULT_MODE;

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
         * Set minimum value for the critical time
         */
        public Builder setMinValue(double minValue) {
            this.minValue = minValue;
            return this;
        }

        /**
         * Set maximum value for the critical time
         */
        public Builder setMaxValue(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        /**
         * Set element ID for the critical time
         */
        public Builder setElementId(String elementId) {
            this.elementId = elementId;
            return this;
        }

        /**
         * Set par Name for the critical time
         */
        public Builder setParName(String parName) {
            this.parName = parName;
            return this;
        }

        public Builder setMode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder setAccuracy(double accuracy) {
            this.accuracy = accuracy;
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

        public CriticalTimeCalculationParameters build() {
            if (startTime < 0) {
                throw new IllegalStateException("Start time (%.2f) should be zero or positive".formatted(startTime));
            }
            if (stopTime <= startTime) {
                throw new IllegalStateException("Stop time (%.2f) should be greater than start time (%.2f)".formatted(stopTime, startTime));
            }
            if (minValue > maxValue - 2 * accuracy) {
                throw new IllegalStateException("Gap between minValue (%.2f) and maxValue (%.2f) must be at least two times the accuracy with min < max".formatted(minValue, maxValue));
            }
            if (accuracy < 0) {
                throw new IllegalStateException("Accuracy should be a number above 0 (found : (%.2f))".formatted(accuracy));
            }
            return new CriticalTimeCalculationParameters(this);
        }
    }

    /**
     * Creates a builder for CriticalTimeCalculationParameters with default values
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Loads parameters from the default platform configuration.
     */
    public static CriticalTimeCalculationParameters load() {
        return load(PlatformConfig.defaultConfig());
    }

    /**
     * Loads parameters from a provided platform configuration.
     */
    public static CriticalTimeCalculationParameters load(PlatformConfig platformConfig) {
        return load(platformConfig, FileSystems.getDefault());
    }

    public static CriticalTimeCalculationParameters load(PlatformConfig platformConfig, FileSystem fileSystem) {
        Builder builder = builder();
        Optional<ModuleConfig> config = platformConfig.getOptionalModuleConfig("critical-time-calculation-default-parameters");
        config.ifPresent(c -> {
            c.getOptionalDoubleProperty("startTime").ifPresent(builder::setStartTime);
            c.getOptionalDoubleProperty("stopTime").ifPresent(builder::setStopTime);
            c.getOptionalStringProperty("elementId").ifPresent(builder::setElementId);
            c.getOptionalStringProperty("parName").ifPresent(builder::setParName);
            c.getOptionalStringProperty("debugDir").ifPresent(builder::setDebugDir);
            c.getOptionalDoubleProperty("minValue").ifPresent(builder::setMinValue);
            c.getOptionalDoubleProperty("maxValue").ifPresent(builder::setMaxValue);
            c.getOptionalEnumProperty("mode", Mode.class).ifPresent(builder::setMode);
            c.getOptionalIntProperty("accuracy").ifPresent(builder::setAccuracy);
            c.getOptionalStringProperty("debugDir").ifPresent(builder::setDebugDir);
        });
        builder.setDynawoParameters(DynawoSimulationParameters.load(platformConfig, fileSystem));
        return builder.build();
    }

    private final double startTime;
    private final double stopTime;
    private final String elementId;
    private final String parName;
    private final double minValue;
    private final double maxValue;
    private final Mode mode;
    private final double accuracy;
    private final DynawoSimulationParameters dynawoParameters;
    private final String debugDir;

    private CriticalTimeCalculationParameters(CriticalTimeCalculationParameters.Builder builder) {
        this.startTime = builder.startTime;
        this.stopTime = builder.stopTime;
        this.elementId = builder.elementId;
        this.parName = builder.parName;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.mode = builder.mode;
        this.accuracy = builder.accuracy;
        this.dynawoParameters = builder.dynawoParameters;
        this.debugDir = builder.debugDir;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getStopTime() {
        return stopTime;
    }

    public String getElementId() {
        return elementId;
    }

    public String getParName() {
        return parName;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public Mode getMode() {
        return mode;
    }

    public double getAccuracy() {
        return accuracy;
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
