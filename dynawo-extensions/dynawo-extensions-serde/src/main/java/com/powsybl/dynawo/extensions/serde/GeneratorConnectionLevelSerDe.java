/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.serde;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.AbstractExtensionSerDe;
import com.powsybl.commons.extensions.ExtensionSerDe;
import com.powsybl.commons.io.DeserializerContext;
import com.powsybl.commons.io.SerializerContext;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevel;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevelAdder;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class GeneratorConnectionLevelSerDe extends AbstractExtensionSerDe<Generator, GeneratorConnectionLevel> {

    public GeneratorConnectionLevelSerDe() {
        super(GeneratorConnectionLevel.NAME, "network", GeneratorConnectionLevel.class, "generator_connection_level_1_0.xsd",
                "http://www.powsybl.org/schema/iidm/ext/generator_connection_level/1_0", "gcl");
    }

    @Override
    public void write(GeneratorConnectionLevel generatorConnectionLevel, SerializerContext context) {
        context.getWriter().writeEnumAttribute("level", generatorConnectionLevel.getLevel());
    }

    @Override
    public GeneratorConnectionLevel read(Generator generator, DeserializerContext context) {
        GeneratorConnectionLevel.GeneratorConnectionLevelType level = context.getReader().readEnumAttribute("level", GeneratorConnectionLevel.GeneratorConnectionLevelType.class);
        context.getReader().readEndNode();
        return generator.newExtension(GeneratorConnectionLevelAdder.class)
                .withLevel(level)
                .add();
    }

}
