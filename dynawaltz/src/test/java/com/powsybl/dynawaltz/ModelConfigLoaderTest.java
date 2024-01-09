package com.powsybl.dynawaltz;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.dynawaltz.builders.DynamicModelCategory;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsSingleton;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelConfigLoaderTest {

    @Test
    void loadConfig() {
        List<DynamicModelCategory> modelCategories = ModelConfigsSingleton.getInstance().getDynamicModelCategories();
        modelCategories.forEach(mc -> {
            if (mc == null) {
                System.out.println("!!!");
            } else {
                mc.modelConfigs().forEach(c -> {
                    if (c == null) {
                        System.out.println(mc);
                    } else if (c.getLib() == null) {
                        System.out.println(c);
                    }
                });
            }
        });
    }

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
