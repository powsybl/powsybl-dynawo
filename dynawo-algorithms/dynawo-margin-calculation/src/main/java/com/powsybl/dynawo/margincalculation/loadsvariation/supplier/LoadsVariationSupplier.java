/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation.supplier;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariationBuilder;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface LoadsVariationSupplier {

    List<LoadsVariation> getLoadsVariations(Network network, ReportNode reportNode);

    default List<LoadsVariation> getLoadsVariations(Network network) {
        return getLoadsVariations(network, ReportNode.NO_OP);
    }

    static LoadsVariationSupplier getLoadsVariationSupplierForJson(Path loadVariationsPath) {
        return (n, r) -> new SupplierJsonDeserializer<>(
                new LoadsVariationJsonDeserializer(() -> new LoadsVariationBuilder(n, r)))
                .deserialize(loadVariationsPath);
    }

}
