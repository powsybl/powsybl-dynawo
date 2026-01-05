/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.nodefaults;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.algorithms.NodeFaultEventData;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationReports;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Objects;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class NodeFaultsBuilder {
    private final Network network;
    private final ReportNode reportNode;
    private boolean isInstantiable = true;

    private final BuilderEquipment<Generator> generator;
    private double faultRPu = Double.NaN;
    private double faultXPu = Double.NaN;

    public NodeFaultsBuilder(Network network, ReportNode reportNode) {
        this.network = Objects.requireNonNull(network);
        this.reportNode = Objects.requireNonNull(reportNode);
        this.generator = new BuilderEquipment<>(IdentifiableType.GENERATOR.toString(), reportNode);
    }

    protected void checkData() {
        isInstantiable = generator.checkEquipmentData();
        if (Double.isNaN(faultRPu)) {
            BuilderReports.reportFieldNotSet(reportNode, "fault_rPu");
            isInstantiable = false;
        }
        if (Double.isNaN(faultXPu)) {
            BuilderReports.reportFieldNotSet(reportNode, "fault_xPu");
            isInstantiable = false;
        }
    }

    private Generator getConnectedGenerator(String generatorId) {
        Generator gen = network.getGenerator(generatorId);
        return gen != null && gen.getTerminal().isConnected() ? gen : null;
    }

    public NodeFaultsBuilder generatorId(String generatorId) {
        generator.addEquipment(generatorId, this::getConnectedGenerator);
        return this;
    }

    public NodeFaultsBuilder faultRPu(double rPu) {
        this.faultRPu = rPu;
        return this;
    }

    public NodeFaultsBuilder faultXPu(double xPu) {
        this.faultXPu = xPu;
        return this;
    }

    private boolean isInstantiable() {
        checkData();
        if (!isInstantiable) {
            CriticalTimeCalculationReports.reportNodeFaultsInstantiationFailure(reportNode);
        }
        return isInstantiable;
    }

    private String getBusStaticId() {
        return generator.getEquipment().getTerminal().getBusBreakerView().getBus().getId();
    }

    public NodeFaultEventData build() {
        return isInstantiable() ? new NodeFaultEventData.Builder()
                .setStaticId(getBusStaticId())
                .setFaultXPu(faultXPu)
                .setFaultRPu(faultRPu)
                .build() : null;
    }
}
