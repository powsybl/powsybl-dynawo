/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.xml.ConstraintsReader;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import com.powsybl.security.LimitViolation;
import com.powsybl.security.json.SecurityAnalysisJsonModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
class ConstraintsReaderTest {

    @Test
    void test() throws IOException {
        Network network = FourSubstationsNodeBreakerFactory.create();
        List<LimitViolation> violations = ConstraintsReader.read(network, getClass().getResourceAsStream("/constraints_sample.xml"));
        assertEquals(3, violations.size());

        Writer stringWriter = new StringWriter();
        ObjectMapper mapper = JsonUtil.createObjectMapper().registerModule(new SecurityAnalysisJsonModule());
        mapper.writerWithDefaultPrettyPrinter().writeValue(stringWriter, violations);

        assertTxtEquals(Objects.requireNonNull(getClass().getResourceAsStream("/limitViolations.json")), stringWriter.toString());
    }
}
