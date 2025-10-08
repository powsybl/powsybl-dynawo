/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.auto.service.AutoService;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters.ExtensionSerializer;
import com.powsybl.dynawo.DynawoSimulationParameters;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(ExtensionSerializer.class)
public class DynawoSimulationParametersSerializer implements JsonDynamicSimulationParameters.ExtensionSerializer<DynawoSimulationParameters> {

    @Override
    public String getCategoryName() {
        return "dynamic-simulation-parameters";
    }

    @Override
    public Class<? super DynawoSimulationParameters> getExtensionClass() {
        return DynawoSimulationParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynawoSimulationParameters";
    }

    /**
     * Specifies serialization for our extension: ignore name et extendable
     */
    private interface SerializationSpec {

        @JsonIgnore
        String getName();

        @JsonIgnore
        DynamicSimulationParameters getExtendable();
    }

    private static ObjectMapper createMapper() {
        return JsonUtil.createObjectMapper()
                .addMixIn(DynawoSimulationParameters.class, SerializationSpec.class)
                .registerModule(new Jdk8Module());
    }

    @Override
    public void serialize(DynawoSimulationParameters dynawoParameters, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {
        createMapper().writeValue(generator, dynawoParameters);
    }

    @Override
    public DynawoSimulationParameters deserialize(JsonParser parser, DeserializationContext arg1)
            throws IOException {
        return createMapper().readValue(parser, DynawoSimulationParameters.class);
    }

    @Override
    public DynawoSimulationParameters deserializeAndUpdate(JsonParser parser, DeserializationContext context,
                                                           DynawoSimulationParameters toUpdateParameters) throws IOException {
        ObjectMapper objectMapper = createMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating(toUpdateParameters);
        return objectReader.readValue(parser, DynawoSimulationParameters.class);
    }
}
