/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.extensions.AbstractExtensionAdder;
import com.powsybl.dynawo.extensions.api.generator.RpclType;
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorProperties;
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorPropertiesAdder;
import com.powsybl.iidm.network.Generator;

import java.util.Objects;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class SynchronousGeneratorPropertiesAdderImpl extends AbstractExtensionAdder<Generator, SynchronousGeneratorProperties> implements SynchronousGeneratorPropertiesAdder {
    private SynchronousGeneratorProperties.Windings numberOfWindings;

    private String governor;

    private String voltageRegulator;

    private String pss;

    private boolean auxiliaries;

    private boolean internalTransformer;

    private RpclType rpcl = RpclType.NONE;

    private SynchronousGeneratorProperties.Uva uva = SynchronousGeneratorProperties.Uva.LOCAL;

    private boolean aggregated;

    private boolean qlim;

    public SynchronousGeneratorPropertiesAdderImpl(Generator generator) {
        super(generator);
    }

    @Override
    protected SynchronousGeneratorProperties createExtension(Generator extendable) {

        Objects.requireNonNull(numberOfWindings, "numberOfWindings");
        if (governor == null ^ voltageRegulator == null) {
            throw new PowsyblException("Governor and Voltage regulator must be both instanced");
        }

        return new SynchronousGeneratorPropertiesImpl(extendable,
                numberOfWindings,
                governor,
                voltageRegulator,
                pss,
                auxiliaries,
                internalTransformer,
                rpcl,
                uva,
                aggregated,
                qlim);
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withNumberOfWindings(SynchronousGeneratorProperties.Windings numberOfWindings) {
        this.numberOfWindings = numberOfWindings;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withGovernor(String governor) {
        this.governor = governor;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withVoltageRegulator(String voltageRegulator) {
        this.voltageRegulator = voltageRegulator;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withPss(String pss) {
        this.pss = pss;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withAuxiliaries(boolean auxiliaries) {
        this.auxiliaries = auxiliaries;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withInternalTransformer(boolean internalTransformer) {
        this.internalTransformer = internalTransformer;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withRpcl(RpclType rpcl) {
        Objects.requireNonNull(rpcl, "rcpl");
        this.rpcl = rpcl;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withUva(SynchronousGeneratorProperties.Uva uva) {
        Objects.requireNonNull(uva, "uva");
        this.uva = uva;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withAggregated(boolean aggregated) {
        this.aggregated = aggregated;
        return this;
    }

    @Override
    public SynchronousGeneratorPropertiesAdderImpl withQlim(boolean qlim) {
        this.qlim = qlim;
        return this;
    }
}
