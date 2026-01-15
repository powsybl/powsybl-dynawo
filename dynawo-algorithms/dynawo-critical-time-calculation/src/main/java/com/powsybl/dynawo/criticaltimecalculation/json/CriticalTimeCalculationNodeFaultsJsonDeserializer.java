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
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationNodeFaultsJsonDeserializer extends StdDeserializer<List<NodeFaultEventData>> {

    private final transient Supplier<NodeFaultEventData.Builder> builderConstructor;

    public CriticalTimeCalculationNodeFaultsJsonDeserializer(Supplier<NodeFaultEventData.Builder> builderConstructor) {
        super(List.class);
        this.builderConstructor = builderConstructor;
    }

    @Override
    public List<NodeFaultEventData> deserialize(JsonParser parser, DeserializationContext context) {
        List<NodeFaultEventData.Builder> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("nodeFaults")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, this::parseNodeFaultsBuilder);
                return true;
            }
            return false;
        });
        return modelConfigList.stream()
                .map(NodeFaultEventData.Builder::build)
                .filter(Objects::nonNull)
                .toList();
    }

    private NodeFaultEventData.Builder parseNodeFaultsBuilder(JsonParser parser) {
        NodeFaultEventData.Builder builder = builderConstructor.get();
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "generatorId" -> {
                    parser.nextToken();
                    builder.setStaticId(parser.getValueAsString());
                }
                case "fault_rPu" -> {
                    parser.nextToken();
                    builder.setFaultRPu(parser.getValueAsDouble());
                }
                case "fault_xPu" -> {
                    parser.nextToken();
                    builder.setFaultXPu(parser.getValueAsDouble());
                }

                default -> handled = false;
            }
            return handled;
        });
        return builder;
    }
}
