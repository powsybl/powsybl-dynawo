/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractHvdcBuilder<R extends AbstractEquipmentModelBuilder<HvdcLine, R>> extends AbstractEquipmentModelBuilder<HvdcLine, R> {

    protected TwoSides danglingSide;

    protected AbstractHvdcBuilder(Network network, ModelConfig modelConfig, String identifiableType, Reporter reporter) {
        super(network, modelConfig, identifiableType, reporter);
    }

    public R dangling(TwoSides danglingSide) {
        this.danglingSide = danglingSide;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        boolean isDangling = modelConfig.isDangling();
        if (isDangling && danglingSide == null) {
            Reporters.reportFieldNotSet(reporter, "dangling");
            isInstantiable = false;
        } else if (!isDangling && danglingSide != null) {
            Reporters.reportFieldSetWithWrongEquipment(reporter, "dangling", modelConfig.lib());
            isInstantiable = false;
        }
    }
}
