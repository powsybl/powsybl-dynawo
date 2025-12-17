/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.generator;

import com.powsybl.commons.extensions.ExtensionAdder;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public interface SynchronizedGeneratorPropertiesAdder extends ExtensionAdder<Generator, SynchronizedGeneratorProperties> {

    @Override
    default Class<SynchronizedGeneratorProperties> getExtensionClass() {
        return SynchronizedGeneratorProperties.class;
    }

    SynchronizedGeneratorPropertiesAdder withType(String type);

    SynchronizedGeneratorPropertiesAdder withRpcl2(boolean isRpcl2);
}
