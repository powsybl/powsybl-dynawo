/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.outputvariables;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.outputvariables.DynawoOutputVariablesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class OutputVariablesJsonDeserializer extends StdDeserializer<List<OutputVariable>> {

    private final transient Supplier<DynawoOutputVariablesBuilder> builderConstructor;

    public OutputVariablesJsonDeserializer(Supplier<DynawoOutputVariablesBuilder> builderConstructor) {
        super(List.class);
        this.builderConstructor = builderConstructor;
    }

    public OutputVariablesJsonDeserializer() {
        this(DynawoOutputVariablesBuilder::new);
    }

    @Override
    public List<OutputVariable> deserialize(JsonParser parser, DeserializationContext context) {
        List<DynawoOutputVariablesBuilder> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("curves")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, p -> parseOutputVariablesBuilder(p, OutputVariable.OutputType.CURVE));
                return true;
            } else if (name.equals("fsv")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, p -> parseOutputVariablesBuilder(p, OutputVariable.OutputType.FINAL_STATE));
                return true;
            }
            return false;
        });
        return modelConfigList.stream()
                .flatMap(b -> b.build().stream())
                .filter(Objects::nonNull)
                .toList();
    }

    private DynawoOutputVariablesBuilder parseOutputVariablesBuilder(JsonParser parser, OutputVariable.OutputType outputType) {
        DynawoOutputVariablesBuilder variablesBuilder = builderConstructor.get();
        variablesBuilder.outputType(outputType);
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "dynamicModelId" -> variablesBuilder.dynamicModelId(parser.nextTextValue());
                case "staticId" -> variablesBuilder.staticId(parser.nextTextValue());
                case "variable" -> variablesBuilder.variable(parser.nextTextValue());
                case "variables" -> variablesBuilder.variables(JsonUtil.parseStringArray(parser));
                default -> handled = false;
            }
            return handled;
        });
        return variablesBuilder;
    }
}
