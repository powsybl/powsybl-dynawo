package com.powsybl.dynawo.suppliers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SupplierJsonDeserializer<T> {

    private final StdDeserializer<List<T>> deserializer;

    public SupplierJsonDeserializer(StdDeserializer<List<T>> deserializer) {
        this.deserializer = deserializer;
    }

    public List<T> deserialize(Path path) {
        try {
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            return setupObjectMapper().readValue(reader, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new PowsyblException("JSON input cannot be read", e);
        }
    }

    public List<T> deserialize(InputStream is) {
        try {
            return setupObjectMapper().readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new PowsyblException("JSON input cannot be read", e);
        }
    }

    private ObjectMapper setupObjectMapper() {
        return new ObjectMapper().registerModule(new SimpleModule().addDeserializer(List.class, deserializer));
    }
}
