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
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorProperties;
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorPropertiesAdder;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class SynchronousGeneratorPropertiesSerDe extends AbstractExtensionSerDe<Generator, SynchronousGeneratorProperties> {

    public SynchronousGeneratorPropertiesSerDe() {
        super(SynchronousGeneratorProperties.NAME, "network", SynchronousGeneratorProperties.class, "synchronousGeneratorProperties.xsd",
                "http://www.powsybl.org/schema/iidm/ext/synchronous_generator_properties/1_0", "sgp");
    }

    @Override
    public void write(SynchronousGeneratorProperties synchronousGeneratorProperties, SerializerContext context) {
        // Keep attribute names consistent between write and read
        context.getWriter().writeStringAttribute("numberOfWindings", synchronousGeneratorProperties.getNumberOfWindings().toString());
        context.getWriter().writeStringAttribute("governor", synchronousGeneratorProperties.getGovernor());
        context.getWriter().writeStringAttribute("voltageRegulator", synchronousGeneratorProperties.getVoltageRegulator());
        context.getWriter().writeStringAttribute("pss", synchronousGeneratorProperties.getPss());
        context.getWriter().writeBooleanAttribute("auxiliaries", synchronousGeneratorProperties.isAuxiliaries());
        context.getWriter().writeBooleanAttribute("internalTransformer", synchronousGeneratorProperties.isInternalTransformer());
        context.getWriter().writeEnumAttribute("rpcl", synchronousGeneratorProperties.getRpcl());
        context.getWriter().writeStringAttribute("uva", synchronousGeneratorProperties.getUva().toString());
        context.getWriter().writeBooleanAttribute("aggregated", synchronousGeneratorProperties.isAggregated());
        context.getWriter().writeBooleanAttribute("qlim", synchronousGeneratorProperties.isQlim());
    }

    @Override
    public SynchronousGeneratorProperties read(Generator generator, DeserializerContext context) {
        SynchronousGeneratorProperties.Windings numberOfWindings = SynchronousGeneratorProperties.Windings.valueOf(context.getReader().readStringAttribute("numberOfWindings"));
        String governor = context.getReader().readStringAttribute("governor");
        String voltageRegulator = context.getReader().readStringAttribute("voltageRegulator");
        String pss = context.getReader().readStringAttribute("pss");
        boolean auxiliaries = context.getReader().readBooleanAttribute("auxiliaries");
        boolean internalTransformer = context.getReader().readBooleanAttribute("internalTransformer");
        RpclType rpclType = context.getReader().readEnumAttribute("rpcl", RpclType.class);
        SynchronousGeneratorProperties.Uva uva = SynchronousGeneratorProperties.Uva.valueOf(context.getReader().readStringAttribute("uva"));
        boolean aggregated = context.getReader().readBooleanAttribute("aggregated");
        boolean qlim = context.getReader().readBooleanAttribute("qlim");
        context.getReader().readEndNode();

        return generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
                .withNumberOfWindings(numberOfWindings)
                .withGovernor(governor)
                .withVoltageRegulator(voltageRegulator)
                .withPss(pss)
                .withAuxiliaries(auxiliaries)
                .withInternalTransformer(internalTransformer)
                .withRpcl(rpclType)
                .withUva(uva)
                .withAggregated(aggregated)
                .withQlim(qlim)
                .add();
    }
}
