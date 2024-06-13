/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.suppliers.events.EventModelConfig;
import com.powsybl.dynawaltz.suppliers.events.EventModelConfigsJsonDeserializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class SupplierJsonDeserializerTest {

    @Test
    void testPathException() {
        Path path = Path.of("wrongURI");
        SupplierJsonDeserializer<EventModelConfig> deserializer = new SupplierJsonDeserializer<>(new EventModelConfigsJsonDeserializer());
        Exception e = assertThrows(PowsyblException.class, () -> deserializer.deserialize(path));
        assertEquals("JSON input cannot be read", e.getMessage());
    }

    @Test
    void testInputStreamException() throws IOException {
        try (InputStream is = InputStream.nullInputStream()) {
            SupplierJsonDeserializer<EventModelConfig> deserializer = new SupplierJsonDeserializer<>(new EventModelConfigsJsonDeserializer());
            Exception e = assertThrows(PowsyblException.class, () -> deserializer.deserialize(is));
            assertEquals("JSON input cannot be read", e.getMessage());
        }
    }
}
