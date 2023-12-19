/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.dynamicsimulation.DynamicModel;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface EquipmentModelBuilder<R extends EquipmentModelBuilder<R>> extends ModelBuilder<DynamicModel> {

    //TODO quid de l'utilisation de cette interface
    R staticId(String staticId);

    R dynamicModelId(String dynamicModelId);

    R parameterSetId(String parameterSetId);
}
