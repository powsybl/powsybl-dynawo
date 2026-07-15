/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.info;

import com.powsybl.commons.extensions.Extension;
import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DynawoModelInfoExtension<I extends Identifiable<I>, T extends DynawoModelInfoExtension<I, T>> extends Extension<I> {

    /**
     * The dynamic model name used in the simulation
     */
    String getModelName();

    T setModelName(String modelName);

    /**
     * The parameter set id associated with this model
     */
    String getParameterSetId();

    T setParameterSetId(String parameterSetId);
}
