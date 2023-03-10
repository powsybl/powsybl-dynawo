/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import java.util.Collections;
import java.util.List;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public abstract class AbstractPureDynamicBlackBoxModel extends AbstractBlackBoxModel {

    protected AbstractPureDynamicBlackBoxModel(String dynamicModelId, String parameterSetId) {
        super(dynamicModelId, null, parameterSetId);
    }

    @Override
    public final List<VarMapping> getVarsMapping() {
        // No static-dynamic mapping as purely dynamic
        return Collections.emptyList();
    }
}
