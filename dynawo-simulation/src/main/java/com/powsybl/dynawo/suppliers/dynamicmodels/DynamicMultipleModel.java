/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.powsybl.iidm.network.IdentifiableType;

import java.util.Objects;

/**
 * Dynamic multiple models configuration deserialized by {@link DynamicModelConfigsJsonDeserializer}
 * used to configure dynamic model builder in {@link DynawoModelsSupplier}
 * @param modelResolverName name of the {@link MultipleModelResolver} used to resolve equipment identifiers
 * @param identifiableType type of the equipment for which the dynamic model is configured
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicMultipleModel(String modelResolverName, IdentifiableType identifiableType) {

    public DynamicMultipleModel(String modelResolverName, IdentifiableType identifiableType) {
        this.modelResolverName = Objects.requireNonNull(modelResolverName);
        this.identifiableType = Objects.requireNonNull(identifiableType);
    }
}
