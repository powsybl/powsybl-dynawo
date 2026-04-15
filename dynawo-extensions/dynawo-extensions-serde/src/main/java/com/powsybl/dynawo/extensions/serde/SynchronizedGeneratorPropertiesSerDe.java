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
import com.powsybl.dynawo.extensions.api.generator.RpclType;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorProperties;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorPropertiesAdder;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class SynchronizedGeneratorPropertiesSerDe extends AbstractExtensionSerDe<Generator, SynchronizedGeneratorProperties> {

    public SynchronizedGeneratorPropertiesSerDe() {
        super(SynchronizedGeneratorProperties.NAME, "network", SynchronizedGeneratorProperties.class, "synchronizedGeneratorProperties.xsd",
                "http://www.powsybl.org/schema/iidm/ext/synchronized_generator_properties/1_0", "sdgp");
    }

    @Override
    public void write(SynchronizedGeneratorProperties synchronizedGeneratorProperties, SerializerContext context) {
        context.getWriter().writeStringAttribute("type", synchronizedGeneratorProperties.getType());
        context.getWriter().writeEnumAttribute("rpcl", synchronizedGeneratorProperties.getRpcl());
    }

    @Override
    public SynchronizedGeneratorProperties read(Generator generator, DeserializerContext context) {
        String type = context.getReader().readStringAttribute("type");
        RpclType rpcl2 = context.getReader().readEnumAttribute("rpcl", RpclType.class);
        context.getReader().readEndNode();
        return generator.newExtension(SynchronizedGeneratorPropertiesAdder.class)
                .withType(type)
                .withRpcl2(rpcl2 != RpclType.NONE)
                .add();
    }
}
