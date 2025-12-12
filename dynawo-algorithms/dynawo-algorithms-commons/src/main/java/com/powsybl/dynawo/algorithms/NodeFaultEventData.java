/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class NodeFaultEventData {

    public static final String DEFAULT_STATIC_ID = null;
    public static final double DEFAULT_FAULT_START_TIME = 1;
    public static final double DEFAULT_FAULT_STOP_TIME = 2;
    public static final double DEFAULT_FAULT_RPU = 0.001;
    public static final double DEFAULT_FAULT_XPU = 0.001;

    public static class Builder {

        private String staticId = DEFAULT_STATIC_ID;
        private double faultStartTime = DEFAULT_FAULT_START_TIME;
        private double faultStopTime = DEFAULT_FAULT_STOP_TIME;
        private double faultRPuValue = DEFAULT_FAULT_RPU;
        private double faultXPuValue = DEFAULT_FAULT_XPU;

        /**
         * Set static ID for the critical time
         */
        public Builder setStaticId(String staticId) {
            this.staticId = staticId;
            return this;
        }

        /**
         * Set node fault start time, must be greater than 0
         */
        public Builder setFaultStartTime(double startTime) {
            this.faultStartTime = startTime;
            return this;
        }

        /**
         * Set node fault stop time, must be after {@link #faultStartTime}
         */
        public Builder setFaultStopTime(double stopTime) {
            this.faultStopTime = stopTime;
            return this;
        }

        /**
         * Set node fault rPu
         */
        public Builder setFaultRPu(double rPu) {
            this.faultRPuValue = rPu;
            return this;
        }

        /**
         * Set node fault xPu
         */
        public Builder setFaultXPu(double xPu) {
            this.faultXPuValue = xPu;
            return this;
        }

        public NodeFaultEventData build() {
            if (faultRPuValue < 0) {
                throw new IllegalStateException("rPu (%.2f) should be zero or positive".formatted(faultRPuValue));
            }
            if (faultXPuValue < 0) {
                throw new IllegalStateException("xPu (%.2f) should be zero or positive".formatted(faultXPuValue));
            }
            if (faultStartTime < 0) {
                throw new IllegalStateException("Start time (%.2f) should be zero or positive".formatted(faultStartTime));
            }
            if (faultStopTime <= faultStartTime - 1) {
                throw new IllegalStateException("Stop time (%.2f) should be greater than start time (%.2f)".formatted(faultStopTime, faultStartTime));
            }
            return new NodeFaultEventData(this);
        }
    }

    /**
     * Creates a builder for CriticalTimeCalculationNodeFaults with default values
     */
    public static NodeFaultEventData.Builder builder() {
        return new NodeFaultEventData.Builder();
    }

    private final String staticId;
    private final double startTime;
    private final double faultTime;
    private final double rPu;
    private final double xPu;

    private NodeFaultEventData(NodeFaultEventData.Builder builder) {
        this.staticId = builder.staticId;
        this.startTime = builder.faultStartTime;
        this.faultTime = builder.faultStopTime;
        this.rPu = builder.faultRPuValue;
        this.xPu = builder.faultXPuValue;
    }

    public String getStaticId() {
        return staticId;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFaultTime() {
        return faultTime;
    }

    public double getRPu() {
        return rPu;
    }

    public double getXPu() {
        return xPu;
    }

}
