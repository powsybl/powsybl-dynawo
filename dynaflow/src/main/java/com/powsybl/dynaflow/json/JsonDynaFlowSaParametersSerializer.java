/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
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
import com.powsybl.dynaflow.DynaFlowSecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisParameters;

import java.io.IOException;

/**
 * Represents {@link DynaFlowSecurityAnalysisParameters} as a Json extension of {@link SecurityAnalysisParameters}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionJsonSerializer.class)
public class JsonDynaFlowSaParametersSerializer implements
        ExtensionJsonSerializer<SecurityAnalysisParameters, DynaFlowSecurityAnalysisParameters> {

    @Override
    public String getCategoryName() {
        return "security-analysis-parameters";
    }

    @Override
    public Class<? super DynaFlowSecurityAnalysisParameters> getExtensionClass() {
        return DynaFlowSecurityAnalysisParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynaFlowSecurityAnalysisParameters";
    }

    /**
     * Specifies serialization for our extension: ignore name et extendable
     */
    private interface SerializationSpec {

        @JsonIgnore
        String getName();

        @JsonIgnore
        SecurityAnalysisParameters getExtendable();
    }

    private static ObjectMapper createMapper() {
        return JsonUtil.createObjectMapper()
                .addMixIn(DynaFlowSecurityAnalysisParameters.class, SerializationSpec.class)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Override
    public void serialize(DynaFlowSecurityAnalysisParameters jsonDynaFlowParameters, JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        createMapper().writeValue(jsonGenerator, jsonDynaFlowParameters);
    }

    @Override
    public DynaFlowSecurityAnalysisParameters deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return createMapper().readValue(parser, DynaFlowSecurityAnalysisParameters.class);
    }

    @Override
    public DynaFlowSecurityAnalysisParameters deserializeAndUpdate(JsonParser parser, DeserializationContext context,
                                                   DynaFlowSecurityAnalysisParameters toUpdateParameters) throws IOException {
        ObjectMapper objectMapper = createMapper();
        ObjectReader objectReader = objectMapper.readerForUpdating(toUpdateParameters);
        return objectReader.readValue(parser, DynaFlowSecurityAnalysisParameters.class);
    }
}
