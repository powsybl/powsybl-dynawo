/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractHvdcBuilder<R extends AbstractEquipmentModelBuilder<HvdcLine, R>> extends AbstractEquipmentModelBuilder<HvdcLine, R> {

    protected static final TwoSides DEFAULT_DANGLING_SIDE = TwoSides.ONE;

    protected TwoSides danglingSide;
    protected final HvdcVarNameHandler varNameHandler;

    protected AbstractHvdcBuilder(Network network, ModelConfig modelConfig, IdentifiableType identifiableType,
                                  ReportNode reportNode, HvdcVarNameHandler varNameHandler) {
        super(network, modelConfig, identifiableType, reportNode);
        this.varNameHandler = varNameHandler;
    }

    protected AbstractHvdcBuilder(Network network, ModelConfig modelConfig, String equipmentType, ReportNode parentReportNode,
                                  HvdcVarNameHandler varNameHandler) {
        super(network, modelConfig, equipmentType, parentReportNode);
        this.varNameHandler = varNameHandler;
    }

    public R dangling(TwoSides danglingSide) {
        this.danglingSide = danglingSide;
        return self();
    }

    @Override
    protected HvdcLine findEquipment(String staticId) {
        return network.getHvdcLine(staticId);
    }

    @Override
    protected void checkData() {
        super.checkData();
        boolean isDangling = modelConfig.isDangling();
        if (isDangling && danglingSide != null) {
            BuilderReports.reportFieldOptionNotImplemented(reportNode, "dangling", DEFAULT_DANGLING_SIDE.toString());
        } else if (!isDangling && danglingSide != null) {
            BuilderReports.reportFieldSetWithWrongEquipment(reportNode, "dangling", modelConfig.lib());
            isInstantiable = false;
        }
    }

    @Override
    public BaseHvdc build() {
        if (isInstantiable()) {
            if (modelConfig.isDangling()) {
                return new HvdcDangling(getEquipment(), parameterSetId, modelConfig, varNameHandler, danglingSide);
            } else {
                return new BaseHvdc(getEquipment(), parameterSetId, modelConfig, varNameHandler);
            }
        }
        return null;
    }
}
