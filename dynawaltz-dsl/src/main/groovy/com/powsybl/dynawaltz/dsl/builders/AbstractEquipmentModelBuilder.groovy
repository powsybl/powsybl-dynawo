/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.builders

import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.dynawaltz.dsl.Reporters
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractEquipmentModelBuilder<T extends Identifiable> extends AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected String dynamicModelId
    protected String parameterSetId
    protected final EquipmentConfig equipmentConfig
    protected final DslEquipment<T> dslEquipment

    protected AbstractEquipmentModelBuilder(Network network, EquipmentConfig equipmentConfig, IdentifiableType equipmentType, Reporter reporter) {
        super(network, reporter)
        this.equipmentConfig = equipmentConfig
        this.dslEquipment = new DslEquipment<T>(equipmentType)
    }

    void staticId(String staticId) {
        dslEquipment.addEquipment(staticId, this::findEquipment)
    }

    void dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId
    }

    void parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId
    }

    @Override
    protected void checkData() {
        isInstantiable = dslEquipment.checkEquipmentData(reporter)
        if (!parameterSetId) {
            Reporters.reportFieldNotSet(reporter, "parameterSetId")
            isInstantiable = false
        }
        if (!dynamicModelId) {
            Reporters.reportFieldReplacement(reporter, "dynamicModelId", "staticId", dslEquipment.staticId ?: "(unknown staticId)")
            dynamicModelId = dslEquipment.staticId
        }
    }

    abstract protected T findEquipment(String staticId)

    T getEquipment() {
        dslEquipment.equipment
    }

    @Override
    abstract DynamicModel build()
}
