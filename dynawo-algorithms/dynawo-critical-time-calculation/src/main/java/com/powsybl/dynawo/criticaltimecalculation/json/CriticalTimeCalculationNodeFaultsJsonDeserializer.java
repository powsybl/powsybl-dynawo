/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawo.algorithms.NodeFaultEventData;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationNodeFaultsJsonDeserializer extends StdDeserializer<List<NodeFaultEventData>> {

    private final transient Supplier<NodeFaultsBuilder> builderConstructor;

    public CriticalTimeCalculationNodeFaultsJsonDeserializer(Supplier<NodeFaultsBuilder> builderConstructor) {
        super(List.class);
        this.builderConstructor = builderConstructor;
    }

    @Override
    public List<NodeFaultEventData> deserialize(JsonParser parser, DeserializationContext context) {
        List<NodeFaultsBuilder> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("nodeFaults")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, this::parseNodeFaultsBuilder);
                return true;
            }
            return false;
        });
        return modelConfigList.stream()
                .map(NodeFaultsBuilder::build)
                .filter(Objects::nonNull)
                .toList();
    }

    private NodeFaultsBuilder parseNodeFaultsBuilder(JsonParser parser) {
        NodeFaultsBuilder builder = builderConstructor.get();
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "generatorId" -> {
                    parser.nextToken();
                    builder.generatorId(parser.getValueAsString());
                }
                case "fault_rPu" -> {
                    parser.nextToken();
                    builder.faultRPu(parser.getValueAsDouble());
                }
                case "fault_xPu" -> {
                    parser.nextToken();
                    builder.faultXPu(parser.getValueAsDouble());
                }

                default -> handled = false;
            }
            return handled;
        });
        return builder;
    }
}
