/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.providers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionAdderProvider;
import com.powsybl.dynawo.extensions.api.info.DynawoUnderVoltageModelInfo;
import com.powsybl.dynawo.extensions.impl.info.DynawoUnderVoltageModelInfoAdderImpl;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionAdderProvider.class)
public class DynawoUnderVoltageModelInfoAdderImplProvider<I extends Identifiable<I>> implements
        ExtensionAdderProvider<Generator, DynawoUnderVoltageModelInfo, DynawoUnderVoltageModelInfoAdderImpl> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public String getExtensionName() {
        return DynawoUnderVoltageModelInfo.NAME;
    }

    @Override
    public Class<DynawoUnderVoltageModelInfoAdderImpl> getAdderClass() {
        return DynawoUnderVoltageModelInfoAdderImpl.class;
    }

    @Override
    public DynawoUnderVoltageModelInfoAdderImpl newAdder(Generator extendable) {
        return new DynawoUnderVoltageModelInfoAdderImpl(extendable);
    }
}
