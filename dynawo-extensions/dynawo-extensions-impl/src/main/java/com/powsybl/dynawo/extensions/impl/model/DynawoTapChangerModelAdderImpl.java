/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModel;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModelAdder;
import com.powsybl.iidm.network.Load;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoTapChangerModelAdderImpl extends AbstractDynawoAutomationSystemModelAdder<Load, DynawoTapChangerModel, DynawoTapChangerModelAdderImpl>
        implements DynawoTapChangerModelAdder {

    private TransformerSide side = null;

    public DynawoTapChangerModelAdderImpl(Load load) {
        super(load);
    }

    @Override
    public DynawoTapChangerModelAdder setSide(TransformerSide side) {
        this.side = side;
        return self();
    }

    @Override
    protected DynawoTapChangerModel createExtension(Load extendable) {
        return new DynawoTapChangerModelImpl(extendable, modelName, parameterSetId, dynamicModelId, side);
    }

    @Override
    protected DynawoTapChangerModelAdderImpl self() {
        return this;
    }
}
