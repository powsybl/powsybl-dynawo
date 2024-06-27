/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.events;

import com.powsybl.dynawaltz.suppliers.Property;

import java.util.List;

/**
 * Event model configuration deserialized by {@link EventModelConfigsJsonDeserializer}
 * used to configure event model builder in {@link DynawoEventModelsSupplier}
 * @param model alias of the library used for this model
 * @param properties list of properties used by the builder
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record EventModelConfig(String model, List<Property> properties) {
}
