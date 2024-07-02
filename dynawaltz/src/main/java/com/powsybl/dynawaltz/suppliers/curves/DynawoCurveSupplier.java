/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.curves;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.curves.DynawoCurvesBuilder;
import com.powsybl.dynawaltz.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Instantiates an {@link com.powsybl.dynawaltz.curves.DynawoCurve} list from JSON
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoCurveSupplier implements CurvesSupplier {

    private final Function<CurvesJsonDeserializer, List<DynawoCurvesBuilder>> deserializerFunction;

    public static DynawoCurveSupplier load(InputStream is) {
        return new DynawoCurveSupplier(d -> new SupplierJsonDeserializer<>(d).deserialize(is));
    }

    public static DynawoCurveSupplier load(Path path) {
        return new DynawoCurveSupplier(d -> new SupplierJsonDeserializer<>(d).deserialize(path));
    }

    private DynawoCurveSupplier(Function<CurvesJsonDeserializer, List<DynawoCurvesBuilder>> deserializerFunction) {
        this.deserializerFunction = deserializerFunction;
    }

    @Override
    public List<Curve> get(Network network, ReportNode reportNode) {
        CurvesJsonDeserializer deserializer = new CurvesJsonDeserializer(() -> new DynawoCurvesBuilder(reportNode));
        return deserializerFunction.apply(deserializer).stream()
                .flatMap(b -> b.build().stream())
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public String getName() {
        return DynaWaltzProvider.NAME;
    }
}
