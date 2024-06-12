/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.curves;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.curves.DynawoCurvesBuilder;
import com.powsybl.iidm.network.Network;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Instantiates an {@link com.powsybl.dynawaltz.curves.DynawoCurve} list from JSON
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoCurveSupplier implements CurvesSupplier {

    private final Function<CurvesJsonDeserializer, List<DynawoCurvesBuilder>> deserializerFunction;

    public DynawoCurveSupplier(InputStream is) {
        deserializerFunction = d -> deserialize(is, d);
    }

    public DynawoCurveSupplier(Path path) {
        deserializerFunction = d -> deserialize(path, d);
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

    private static List<DynawoCurvesBuilder> deserialize(Path path, CurvesJsonDeserializer deserializer) {
        try {
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            return setupObjectMapper(deserializer).readValue(reader, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<DynawoCurvesBuilder> deserialize(InputStream is, CurvesJsonDeserializer deserializer) {
        try {
            return setupObjectMapper(deserializer).readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper setupObjectMapper(CurvesJsonDeserializer deserializer) {
        return new ObjectMapper().registerModule(new SimpleModule().addDeserializer(List.class, deserializer));
    }
}
