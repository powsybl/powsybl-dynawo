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
public abstract class AbstractEquipmentModelBuilder<T extends Identifiable<?>, R extends AbstractEquipmentModelBuilder<T, R>> extends AbstractDynamicModelBuilder implements EquipmentModelBuilder<R> {

    protected String dynamicModelId;
    protected String parameterSetId;
    protected final ModelConfig modelConfig;
    protected final DslEquipment<T> dslEquipment;

    protected AbstractEquipmentModelBuilder(Network network, ModelConfig modelConfig, IdentifiableType equipmentType, Reporter reporter) {
        super(network, reporter);
        this.modelConfig = modelConfig;
        this.dslEquipment = new DslEquipment<>(equipmentType);
    }

    public R staticId(String staticId) {
        dslEquipment.addEquipment(staticId, this::findEquipment);
        return self();
    }

    public R dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return self();
    }

    public R parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    @Override
    protected void checkData() {
        isInstantiable = dslEquipment.checkEquipmentData(reporter);
        if (parameterSetId == null) {
            Reporters.reportFieldNotSet(reporter, "parameterSetId");
            isInstantiable = false;
        }
        if (dynamicModelId == null) {
            Reporters.reportFieldReplacement(reporter, "dynamicModelId", "staticId", dslEquipment.hasStaticId() ? dslEquipment.getStaticId() : "(unknown staticId)");
            dynamicModelId = dslEquipment.getStaticId();
        }
    }

    protected abstract T findEquipment(String staticId);

    public T getEquipment() {
        return dslEquipment.getEquipment();
    }

    @Override
    public String getModelId() {
        return dynamicModelId != null ? dynamicModelId : "unknownDynamicId";
    }

    protected abstract R self();
}
