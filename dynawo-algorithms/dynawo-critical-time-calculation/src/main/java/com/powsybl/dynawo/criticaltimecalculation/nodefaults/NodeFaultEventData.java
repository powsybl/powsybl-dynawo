/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.nodefaults;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.iidm.network.*;

import java.util.Objects;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class NodeFaultEventData {

    public static class Builder {

        private final Network network;
        private final ReportNode reportNode;

        private final BuilderEquipment<Bus> bus;
        private String staticId = null;
        private double faultStartTime = 1;
        private double faultStopTime = 2;
        private double faultRPu = Double.NaN;
        private double faultXPu = Double.NaN;

        public Builder(Network network, ReportNode reportNode) {
            this.network = Objects.requireNonNull(network);
            this.reportNode = Objects.requireNonNull(reportNode);
            this.bus = new BuilderEquipment<>(IdentifiableType.BUS.toString(), this.reportNode);

        }

        public Builder(Network network) {
            this.network = Objects.requireNonNull(network);
            this.reportNode = ReportNode.NO_OP;
            this.bus = new BuilderEquipment<>(IdentifiableType.BUS.toString(), this.reportNode);

        }

        public Builder() {
            this.network = null;
            this.reportNode = ReportNode.NO_OP;
            this.bus = new BuilderEquipment<>(IdentifiableType.BUS.toString(), this.reportNode);

        }

        /**
         * Set static ID for the critical time
         */
        public Builder setStaticId(String staticId) {
            bus.addEquipment(staticId, this::getBus);
            this.staticId = bus.getEquipment() != null ? bus.getEquipment().getId() : null;
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
            this.faultRPu = rPu;
            return this;
        }

        /**
         * Set node fault xPu
         */
        public Builder setFaultXPu(double xPu) {
            this.faultXPu = xPu;
            return this;
        }

        private Bus getBus(String staticId) {
            Bus bus = network.getBusBreakerView().getBus(staticId);
            if (bus != null) {
                return bus;
            }

            Generator generator = network.getGenerator(staticId);
            return generator != null
                    ? generator.getTerminal().getBusBreakerView().getBus()
                    : null;
        }

        public NodeFaultEventData build() {
            if (bus.getEquipment() == null) {
                throw new IllegalStateException("Static Id '%s' was not found.".formatted(staticId));
            }
            if (faultRPu < 0) {
                throw new IllegalStateException("rPu (%.2f) should be zero or positive".formatted(faultRPu));
            }
            if (faultXPu < 0) {
                throw new IllegalStateException("xPu (%.2f) should be zero or positive".formatted(faultXPu));
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
    public static NodeFaultEventData.Builder builder(Network network, ReportNode reportNode) {
        return new NodeFaultEventData.Builder(network, reportNode);
    }

    public static NodeFaultEventData.Builder builder(Network network) {
        return new NodeFaultEventData.Builder(network);
    }

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
        this.rPu = builder.faultRPu;
        this.xPu = builder.faultXPu;
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
