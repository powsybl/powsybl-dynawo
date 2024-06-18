/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.iidm.network.Network;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderConfig {

    @FunctionalInterface
    public interface ModelBuilderConstructor {
        ModelBuilder<DynamicModel> createBuilder(Network network, String lib, ReportNode reportNode);
    }

    private final String category;
    private final ModelBuilderConstructor builderConstructor;
    private final Supplier<Collection<String>> libsSupplier;

    public BuilderConfig(String category, ModelBuilderConstructor builderConstructor, Supplier<Collection<String>> libsSupplier) {
        this.category = category;
        this.builderConstructor = builderConstructor;
        this.libsSupplier = libsSupplier;
    }

    public String getCategory() {
        return category;
    }

    public ModelBuilderConstructor getBuilderConstructor() {
        return builderConstructor;
    }

    public Collection<String> getLibs() {
        return libsSupplier.get();
    }
}
