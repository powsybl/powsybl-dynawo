/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionJsonSerializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.loadflow.LoadFlowParameters;

import java.io.IOException;

/**
 * Represents {@link DynaFlowParameters} as a Json extension of {@link LoadFlowParameters}
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
@AutoService(ExtensionJsonSerializer.class)
public class JsonDynaFlowParametersSerializer implements ExtensionJsonSerializer<LoadFlowParameters, DynaFlowParameters> {

    @Override
    public String getCategoryName() {
        return "loadflow-parameters";
    }

    @Override
    public Class<? super DynaFlowParameters> getExtensionClass() {
        return DynaFlowParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynaFlowParameters";
    }

    /**
     * Specifies serialization for our extension: ignore name et extendable
     */
    private interface SerializationSpec {

        @JsonIgnore
        String getName();

        @JsonIgnore
        LoadFlowParameters getExtendable();
    }

    private static ObjectMapper createMapper() {
        return JsonUtil.createObjectMapper()
                .addMixIn(DynaFlowParameters.class, SerializationSpec.class)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void serialize(DynaFlowParameters jsonDynaFlowParameters, JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        createMapper().writeValue(jsonGenerator, jsonDynaFlowParameters);
    }

    @Override
    public DynaFlowParameters deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return createMapper().readValue(parser, DynaFlowParameters.class);
    }

    @Override
    public DynaFlowParameters deserializeAndUpdate(JsonParser parser, DeserializationContext context, DynaFlowParameters toUpdateParameters) throws IOException {
        ObjectMapper objectMapper = createMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating(toUpdateParameters);
        return objectReader.readValue(parser, DynaFlowParameters.class);
    }
}
