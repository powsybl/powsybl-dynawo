/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers.curves;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawaltz.curves.DynawoCurvesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CurvesJsonDeserializer extends StdDeserializer<List<DynawoCurvesBuilder>> {

    private final transient Supplier<DynawoCurvesBuilder> builderConstructor;

    public CurvesJsonDeserializer(Supplier<DynawoCurvesBuilder> builderConstructor) {
        super(List.class);
        this.builderConstructor = builderConstructor;
    }

    public CurvesJsonDeserializer() {
        this(DynawoCurvesBuilder::new);
    }

    @Override
    public List<DynawoCurvesBuilder> deserialize(JsonParser parser, DeserializationContext context) {
        List<DynawoCurvesBuilder> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("curves")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, this::parseCurvesBuilder);
                return true;
            }
            return false;
        });
        return modelConfigList;
    }

    private DynawoCurvesBuilder parseCurvesBuilder(JsonParser parser) {
        DynawoCurvesBuilder curvesBuilder = builderConstructor.get();
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "dynamicModelId" -> curvesBuilder.dynamicModelId(parser.nextTextValue());
                case "staticId" -> curvesBuilder.staticId(parser.nextTextValue());
                case "variable" -> curvesBuilder.variable(parser.nextTextValue());
                case "variables" -> curvesBuilder.variables(JsonUtil.parseStringArray(parser));
                default -> handled = false;
            }
            return handled;
        });
        return curvesBuilder;
    }
}
