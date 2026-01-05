/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.results;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.criticaltimecalculation.results.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class XmlResultParserTest {
    @Test
    void test() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/result.xml")));
        CriticalTimeCalculationResults results = new CriticalTimeCalculationResults(new XmlCriticalTimeCalculationResultsParser().parse(xml));

        assertThat(results.criticalTimeCalculationResults()).containsExactly(
                new CriticalTimeCalculationResult("MyFirstScenario", RESULT_FOUND, 1),
                new CriticalTimeCalculationResult("MySecondScenario", CT_BELOW_MIN_BOUND),
                new CriticalTimeCalculationResult("MyThirdScenario", CT_ABOVE_MAX_BOUND));
    }

    @Test
    void parseFromPath() throws URISyntaxException {
        List<CriticalTimeCalculationResult> result = new ArrayList<>();
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/result.xml")).toURI());
        new XmlCriticalTimeCalculationResultsParser().parse(path, result::add);
        assertEquals(3, result.size());
    }

    @Test
    void testInconsistentFile() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/wrongResult.xml")));
        List<CriticalTimeCalculationResult> result = new XmlCriticalTimeCalculationResultsParser().parse(xml);
        assertEquals(1, result.size());
    }
}
