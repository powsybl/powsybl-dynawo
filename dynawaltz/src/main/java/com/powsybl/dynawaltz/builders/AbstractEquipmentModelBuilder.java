/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEquipmentModelBuilder<T extends Identifiable<?>, R extends AbstractEquipmentModelBuilder<T, R>> extends AbstractDynamicModelBuilder implements EquipmentModelBuilder<T, R> {

    protected String dynamicModelId;
    protected String parameterSetId;
    protected final ModelConfig modelConfig;
    protected final BuilderEquipment<T> builderEquipment;

    protected AbstractEquipmentModelBuilder(Network network, ModelConfig modelConfig, IdentifiableType equipmentType, Reporter reporter) {
        super(network, reporter);
        this.modelConfig = modelConfig;
        this.builderEquipment = new BuilderEquipment<>(equipmentType);
    }

    @Override
    public R staticId(String staticId) {
        builderEquipment.addEquipment(staticId, this::findEquipment);
        return self();
    }

    @Override
    public R equipment(T equipment) {
        builderEquipment.addEquipment(equipment, this::checkEquipment);
        return self();
    }

    @Override
    public R dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return self();
    }

    @Override
    public R parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    @Override
    protected void checkData() {
        isInstantiable = builderEquipment.checkEquipmentData(reporter);
        if (parameterSetId == null) {
            Reporters.reportFieldNotSet(reporter, "parameterSetId");
            isInstantiable = false;
        }
        if (dynamicModelId == null) {
            Reporters.reportFieldReplacement(reporter, "dynamicModelId", "staticId", builderEquipment.hasStaticId() ? builderEquipment.getStaticId() : "(unknown staticId)");
            dynamicModelId = builderEquipment.getStaticId();
        }
    }

    protected abstract T findEquipment(String staticId);

    protected boolean checkEquipment(T equipment) {
        return network == equipment.getNetwork();
    }

    public T getEquipment() {
        return builderEquipment.getEquipment();
    }

    @Override
    public String getModelId() {
        return dynamicModelId != null ? dynamicModelId : "unknownDynamicId";
    }

    protected abstract R self();
}
