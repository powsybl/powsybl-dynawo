package com.powsybl.dynawaltz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.dynawaltz.builders.ModelConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelConfigLoaderTest {

    @Test
    void loadConfigTest() throws IOException {
        String json = """
                {
                "synchronousGenerators": [
                    {
                      "lib": "PhotovoltaicsWeccCurrentSource",
                      "internalModelPrefix": "WTG4A",
                      "properties": [
                        "Synchronized"
                      ]
                    },
                    {
                      "lib": "PhotovoltaicsWeccCurrentSource",
                      "properties": [
                        "Synchronized"
                      ]
                    },
                    {
                      "lib": "PhotovoltaicsWeccCurrentSource"
                    }
                ]
                }""";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<ModelConfig>> configs = objectMapper.readValue(json, new TypeReference<>() {

        });
        assertNotNull(configs);
    }
}
