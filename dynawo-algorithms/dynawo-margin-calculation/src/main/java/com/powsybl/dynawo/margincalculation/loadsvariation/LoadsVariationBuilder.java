/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderEquipmentsList;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.margincalculation.MarginCalculationReports;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadsVariationBuilder {

    private final Network network;
    private final ReportNode reportNode;
    private boolean isInstantiable = true;

    private final BuilderEquipmentsList<Load> loads;
    private double variationValue = Double.NaN;

    public LoadsVariationBuilder(Network network, ReportNode reportNode) {
        this.network = Objects.requireNonNull(network);
        this.reportNode = Objects.requireNonNull(reportNode);
        this.loads = new BuilderEquipmentsList<>(IdentifiableType.LOAD, "loads");
    }

    public LoadsVariationBuilder loads(String... loadIds) {
        loads.addEquipments(loadIds, this::getConnectedLoad);
        return this;
    }

    public LoadsVariationBuilder loads(Collection<String> loadIds) {
        loads.addEquipments(loadIds, this::getConnectedLoad);
        return this;
    }

    private Load getConnectedLoad(String loadId) {
        Load load = network.getLoad(loadId);
        return load != null && load.getTerminal().isConnected() ? load : null;
    }

    public LoadsVariationBuilder variationValue(double variationValue) {
        this.variationValue = variationValue;
        return this;
    }

    protected void checkData() {
        isInstantiable = loads.checkEquipmentData(reportNode);
        if (Double.isNaN(variationValue)) {
            BuilderReports.reportFieldNotSet(reportNode, "variationValue");
            isInstantiable = false;
        }
    }

    private boolean isInstantiable() {
        checkData();
        if (!isInstantiable) {
            MarginCalculationReports.reportLoadVariationInstantiationFailure(reportNode);
        }
        return isInstantiable;
    }

    public LoadsVariation build() {
        return isInstantiable() ? new LoadsVariation(loads.getEquipments(), variationValue) : null;
    }
}
