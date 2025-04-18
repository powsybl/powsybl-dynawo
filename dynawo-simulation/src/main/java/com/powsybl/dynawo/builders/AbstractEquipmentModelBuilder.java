/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEquipmentModelBuilder<T extends Identifiable<T>, R extends AbstractEquipmentModelBuilder<T, R>> extends AbstractDynamicModelBuilder implements EquipmentModelBuilder<T, R> {

    protected String parameterSetId;
    protected final ModelConfig modelConfig;
    protected final BuilderEquipment<T> builderEquipment;
    protected final Predicate<T> hasSameNetwork = eq -> Objects.equals(network, eq.getNetwork());

    protected AbstractEquipmentModelBuilder(Network network, ModelConfig modelConfig, IdentifiableType equipmentType, ReportNode parentReportNode) {
        super(network, parentReportNode);
        this.modelConfig = Objects.requireNonNull(modelConfig);
        this.builderEquipment = new BuilderEquipment<>(equipmentType.toString(), reportNode);
    }

    protected AbstractEquipmentModelBuilder(Network network, ModelConfig modelConfig, String equipmentType, ReportNode parentReportNode) {
        super(network, parentReportNode);
        this.modelConfig = modelConfig;
        this.builderEquipment = new BuilderEquipment<>(equipmentType, reportNode);
    }

    @Override
    public R staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment);
        return self();
    }

    @Override
    public R equipment(T equipment) {
        builderEquipment.addEquipment(equipment, hasSameNetwork);
        return self();
    }

    @Override
    public R parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    @Override
    protected void checkData() {
        isInstantiable = builderEquipment.checkEquipmentData();
        if (parameterSetId == null) {
            BuilderReports.reportFieldNotSet(reportNode, "parameterSetId");
            isInstantiable = false;
        }
    }

    protected abstract T findEquipment(String staticId);

    public T getEquipment() {
        return builderEquipment.getEquipment();
    }

    @Override
    public String getModelName() {
        return modelConfig.name();
    }

    @Override
    public String getModelId() {
        return builderEquipment.getStaticId();
    }

    protected abstract R self();
}
