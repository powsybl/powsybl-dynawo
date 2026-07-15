/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.info;

import com.powsybl.commons.extensions.Extension;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.impl.extensions.AbstractIidmExtensionAdder;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynawoModelInfoAdder<I extends Identifiable<I>, E extends Extension<I>, A extends AbstractDynawoModelInfoAdder<I, E, A>>
        extends AbstractIidmExtensionAdder<I, E> {

    protected String modelName;
    protected String parameterSetId;

    public AbstractDynawoModelInfoAdder(I identifiable) {
        super(identifiable);
    }

    public A setModelName(String modelName) {
        this.modelName = modelName;
        return self();
    }

    public A setParameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    protected abstract A self();
}
