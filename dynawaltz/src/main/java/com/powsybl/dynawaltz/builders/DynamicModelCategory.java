/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicModelCategory(String category, DynamicModelBuilderUtils.ModelBuilderConstructor builderConstructor, List<ModelConfig> modelConfigs) {
    //TODO keep everything private and return builder directly ?
    //TODO pacakge rivate cstr
    //TODO add enum category to model tag and the corresponding enum in builders check the correspondance in constr
}
