/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.auto.service.AutoService;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters.ExtensionSerializer;
import com.powsybl.dynawaltz.DynaWaltzParameters;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(ExtensionSerializer.class)
public class DynaWaltzSimulationParametersSerializer implements JsonDynamicSimulationParameters.ExtensionSerializer<DynaWaltzParameters> {

    @Override
    public String getCategoryName() {
        return "dynamic-simulation-parameters";
    }

    @Override
    public Class<? super DynaWaltzParameters> getExtensionClass() {
        return DynaWaltzParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynaWaltzParameters";
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
                .addMixIn(DynaWaltzParameters.class, SerializationSpec.class);
    }

    @Override
    public void serialize(DynaWaltzParameters dynawoParameters, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {
        createMapper().writeValue(generator, dynawoParameters);
    }

    @Override
    public DynaWaltzParameters deserialize(JsonParser parser, DeserializationContext arg1)
            throws IOException {
        return createMapper().readValue(parser, DynaWaltzParameters.class);
    }
}
