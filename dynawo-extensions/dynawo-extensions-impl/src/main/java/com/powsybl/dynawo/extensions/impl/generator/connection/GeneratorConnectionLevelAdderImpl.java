/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator.connection;

import com.powsybl.commons.extensions.AbstractExtensionAdder;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevel;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevelAdder;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class GeneratorConnectionLevelAdderImpl extends AbstractExtensionAdder<Generator, GeneratorConnectionLevel> implements GeneratorConnectionLevelAdder {

    private GeneratorConnectionLevel.GeneratorConnectionLevelType level;

    public GeneratorConnectionLevelAdderImpl(Generator generator) {
        super(generator);
    }

    @Override
    protected GeneratorConnectionLevel createExtension(Generator extendable) {
        return new GeneratorConnectionLevelImpl(extendable, level);
    }

    @Override
    public GeneratorConnectionLevelAdderImpl withLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType level) {
        this.level = level;
        return this;
    }

}
