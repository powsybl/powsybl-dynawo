/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.margincalculation.results;

import com.powsybl.dynawo.contingency.results.FailedCriterion;
import com.powsybl.dynawo.contingency.results.ScenarioResult;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.margincalculation.results.XmlMarginCalculationResultParser;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.contingency.results.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class XmlResultParserTest {

    @Test
    void test() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/result.xml")));
        List<LoadIncreaseResult> result = new XmlMarginCalculationResultParser().parse(xml);

        assertThat(result).containsExactly(new LoadIncreaseResult(100, CRITERIA_NON_RESPECTED, Collections.emptyList(),
                        List.of(new FailedCriterion("total load power = 295.381MW", 70.0))),
                new LoadIncreaseResult(50, CONVERGENCE,
                        List.of(new ScenarioResult("DisconnectLine", CONVERGENCE),
                                new ScenarioResult("DisconnectGroup", CRITERIA_NON_RESPECTED,
                                        List.of(new FailedCriterion("total load power = 276.374MW", 185.0))),
                                new ScenarioResult("DisconnectGenerator", DIVERGENCE)),
                        Collections.emptyList()),
                new LoadIncreaseResult(60, DIVERGENCE),
                new LoadIncreaseResult(70, EXECUTION_PROBLEM));
    }

    @Test
    void parseFromPath() throws URISyntaxException {
        List<LoadIncreaseResult> result = new ArrayList<>();
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/result.xml")).toURI());
        new XmlMarginCalculationResultParser().parse(path, result::add);
        assertEquals(4, result.size());
    }

    @Test
    void testInconsistentFile() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/wrongResult.xml")));
        List<LoadIncreaseResult> result = new XmlMarginCalculationResultParser().parse(xml);
        assertEquals(1, result.size());
    }
}
