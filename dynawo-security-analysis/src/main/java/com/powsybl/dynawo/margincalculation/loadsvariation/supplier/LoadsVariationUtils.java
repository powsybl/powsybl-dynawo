/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation.supplier;

import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariationBuilder;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class LoadsVariationUtils {

    private LoadsVariationUtils() {
    }

    public static LoadsVariationSupplier createLoadsVariationSupplier(InputStream is) {
        return (n, r) -> new SupplierJsonDeserializer<>(new LoadsVariationJsonDeserializer(() -> new LoadsVariationBuilder(n, r))).deserialize(is);
    }

    public static LoadsVariationSupplier createLoadsVariationSupplier(Path path) {
        return (n, r) -> new SupplierJsonDeserializer<>(new LoadsVariationJsonDeserializer(() -> new LoadsVariationBuilder(n, r))).deserialize(path);
    }
}
