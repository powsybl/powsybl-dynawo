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
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterIModel;
import com.powsybl.dynawo.extensions.impl.model.DynawoPhaseShifterIModelAdderImpl;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionAdderProvider.class)
public class DynawoPhaseShifterIModelAdderImplProvider<I extends Identifiable<I>> implements
        ExtensionAdderProvider<TwoWindingsTransformer, DynawoPhaseShifterIModel, DynawoPhaseShifterIModelAdderImpl> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public String getExtensionName() {
        return DynawoPhaseShifterIModel.NAME;
    }

    @Override
    public Class<DynawoPhaseShifterIModelAdderImpl> getAdderClass() {
        return DynawoPhaseShifterIModelAdderImpl.class;
    }

    @Override
    public DynawoPhaseShifterIModelAdderImpl newAdder(TwoWindingsTransformer extendable) {
        return new DynawoPhaseShifterIModelAdderImpl(extendable);
    }
}
