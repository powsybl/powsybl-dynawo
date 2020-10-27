/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.auto.service.AutoService;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.DynaflowParameters;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.json.JsonLoadFlowParameters.ExtensionSerializer;

import java.io.IOException;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
@AutoService(ExtensionSerializer.class)
public class JsonDynaflowParametersSerializer implements ExtensionSerializer<DynaflowParameters> {

    @Override
    public String getCategoryName() {
        return "loadflow-parameters";
    }

    @Override
    public Class<? super DynaflowParameters> getExtensionClass() {
        return DynaflowParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynaflowParameters";
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
                .addMixIn(DynaflowParameters.class, SerializationSpec.class);
    }

    @Override
    public void serialize(DynaflowParameters jsonDynaflowParameters, JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        createMapper().writeValue(jsonGenerator, jsonDynaflowParameters);
    }

    @Override
    public DynaflowParameters deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return createMapper().readValue(parser, DynaflowParameters.class);
    }

    @Override
    public DynaflowParameters deserializeAndUpdate(JsonParser parser, DeserializationContext context, DynaflowParameters toUpdateParameters) throws IOException {
        ObjectMapper objectMapper = createMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating(toUpdateParameters);
        return objectReader.readValue(parser, DynaflowParameters.class);
    }
}
