package com.powsybl.dynawo.suppliers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynamicModelConfigs;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynamicModelConfigsJsonDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoSupplierJsonDeserializer {

    public DynamicModelConfigs deserialize(Path path) {
        try {
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            return setupObjectMapper().readValue(reader, DynamicModelConfigs.class);
        } catch (IOException e) {
            throw new PowsyblException("JSON input cannot be read", e);
        }
    }

    public DynamicModelConfigs deserialize(InputStream is) {
        try {
            return setupObjectMapper().readValue(is, DynamicModelConfigs.class);
        } catch (IOException e) {
            throw new PowsyblException("JSON input cannot be read", e);
        }
    }

    private ObjectMapper setupObjectMapper() {
        return new ObjectMapper().registerModule(new SimpleModule()
                .addDeserializer(DynamicModelConfigs.class,
                        new DynamicModelConfigsJsonDeserializer()));
    }
}
