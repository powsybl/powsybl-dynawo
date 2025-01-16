/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation.supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariationBuilder;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadsVariationJsonDeserializer extends StdDeserializer<List<LoadsVariation>> {

    private final transient Supplier<LoadsVariationBuilder> builderConstructor;

    public LoadsVariationJsonDeserializer(Supplier<LoadsVariationBuilder> builderConstructor) {
        super(List.class);
        this.builderConstructor = builderConstructor;
    }

    @Override
    public List<LoadsVariation> deserialize(JsonParser parser, DeserializationContext context) {
        List<LoadsVariationBuilder> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("variations")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, this::parseLoadsVariationBuilder);
                return true;
            }
            return false;
        });
        return modelConfigList.stream()
                .map(LoadsVariationBuilder::build)
                .filter(Objects::nonNull)
                .toList();
    }

    private LoadsVariationBuilder parseLoadsVariationBuilder(JsonParser parser) {
        LoadsVariationBuilder loadsVariationBuilder = builderConstructor.get();
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "loadsIds" -> loadsVariationBuilder.loads(JsonUtil.parseStringArray(parser));
                case "variationValue" -> {
                    parser.nextToken();
                    loadsVariationBuilder.variationValue(parser.getDoubleValue());
                }
                default -> handled = false;
            }
            return handled;
        });
        return loadsVariationBuilder;
    }
}
