/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.Objects;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractBlackBoxModel extends AbstractDynawoDynamicModel {

    public AbstractBlackBoxModel(String id, String staticId, String parameterSetId) {
        super(id);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    @Override
    public DynamicModelType getType() {
        return DynamicModelType.BLACK_BOX_MODEL;
    }

    public abstract String getLib();

    public String getStaticId() {
        return staticId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    private final String staticId;
    private final String parameterSetId;
}
