/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractGeneratorBuilder<R extends AbstractEquipmentModelBuilder<Generator, R>> extends AbstractEquipmentModelBuilder<Generator, R> {

    protected AbstractGeneratorBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, IdentifiableType.GENERATOR, parentReportNode);
    }

    @Override
    protected Generator findEquipment(String staticId) {
        return network.getGenerator(staticId);
    }

    protected boolean isGeneratorCustom() {
        return !modelConfig.varPrefix().isEmpty() || !modelConfig.varMapping().isEmpty();
    }
}
