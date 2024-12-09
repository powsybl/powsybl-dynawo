/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.results;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynaflow.results.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class XmlAggregatedResultParserTest {

    @Test
    void test() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/aggregated_results.xml")));
        List<ScenarioResult> result = new XmlScenarioResultParser().parse(xml);

        assertThat(result).containsExactly(
                        new ScenarioResult("DisconnectLine", CONVERGENCE),
                        new ScenarioResult("DisconnectGenerator", DIVERGENCE),
                        new ScenarioResult("DisconnectGroup", CRITERIA_NON_RESPECTED,
                                List.of(new FailedCriterion("total load power = 276.374MW", 185.0))),
                        new ScenarioResult("DisconnectGenerator2", EXECUTION_PROBLEM));
    }

    @Test
    void parseFromPath() throws URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/aggregated_results.xml")).toURI());
        List<ScenarioResult> result = new XmlScenarioResultParser().parse(path);
        assertEquals(4, result.size());
    }

    @Test
    void testInconsistentFile() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/aggregated_results_faulty.xml")));
        List<ScenarioResult> result = new XmlScenarioResultParser().parse(xml);
        assertEquals(0, result.size());
    }
}
