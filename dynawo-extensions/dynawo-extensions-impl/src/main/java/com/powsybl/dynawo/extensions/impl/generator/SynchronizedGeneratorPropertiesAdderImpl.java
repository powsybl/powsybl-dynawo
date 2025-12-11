/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator;

import com.powsybl.commons.extensions.AbstractExtensionAdder;
import com.powsybl.dynawo.extensions.api.generator.RpclType;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorProperties;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorPropertiesAdder;
import com.powsybl.iidm.network.Generator;

import java.util.Objects;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class SynchronizedGeneratorPropertiesAdderImpl extends AbstractExtensionAdder<Generator, SynchronizedGeneratorProperties> implements SynchronizedGeneratorPropertiesAdder {

    private String type;

    private RpclType rpcl = RpclType.NONE;

    public SynchronizedGeneratorPropertiesAdderImpl(Generator generator) {
        super(generator);
    }

    @Override
    protected SynchronizedGeneratorProperties createExtension(Generator extendable) {

        Objects.requireNonNull(type, "type");
        return new SynchronizedGeneratorPropertiesImpl(extendable, type, rpcl);
    }

    @Override
    public SynchronizedGeneratorPropertiesAdderImpl withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public SynchronizedGeneratorPropertiesAdderImpl withRpcl2(boolean isRpcl2) {
        this.rpcl = isRpcl2 ? RpclType.RPCL2 : RpclType.NONE;
        return this;
    }
}
