/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.providers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionAdderProvider;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevel;
import com.powsybl.dynawo.extensions.impl.generator.connection.GeneratorConnectionLevelAdderImpl;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@AutoService(ExtensionAdderProvider.class)
public class GeneratorConnectionLevelAdderImplProvider implements
        ExtensionAdderProvider<Generator, GeneratorConnectionLevel, GeneratorConnectionLevelAdderImpl> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public String getExtensionName() {
        return GeneratorConnectionLevel.NAME;
    }

    @Override
    public Class<GeneratorConnectionLevelAdderImpl> getAdderClass() {
        return GeneratorConnectionLevelAdderImpl.class;
    }

    @Override
    public GeneratorConnectionLevelAdderImpl newAdder(Generator generator) {
        return new GeneratorConnectionLevelAdderImpl(generator);
    }
}
