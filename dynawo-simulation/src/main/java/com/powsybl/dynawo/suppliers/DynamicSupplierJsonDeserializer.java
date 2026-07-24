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

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO generalize ?
public class DynamicSupplierJsonDeserializer<T> {

    private final StdDeserializer<T> deserializer;
    private final Class<T> type;

    public DynamicSupplierJsonDeserializer(Class<T> type, StdDeserializer<T> deserializer) {
        this.deserializer = deserializer;
        this.type = type;
    }

    public T deserialize(Path path) {
        try {
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            return setupObjectMapper().readValue(reader, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new PowsyblException("JSON input cannot be read", e);
        }
    }

    public T deserialize(InputStream is) {
        try {
            return setupObjectMapper().readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new PowsyblException("JSON input cannot be read", e);
        }
    }

    private ObjectMapper setupObjectMapper() {
        return new ObjectMapper().registerModule(new SimpleModule().addDeserializer(type, deserializer));
    }
}
