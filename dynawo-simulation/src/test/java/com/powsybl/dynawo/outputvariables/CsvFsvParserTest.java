/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.outputvariables;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class CsvFsvParserTest {

    @Test
    void testFsvParsing() throws URISyntaxException {

        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/fsv.csv")).toURI());
        List<FsvEntry> timeline = new CsvFsvParser(';').parse(path);
        assertThat(timeline).containsExactly(
                new FsvEntry("NETWORK", "_BUS____1_TN_Upu_value", 1.060012),
                new FsvEntry("GEN____1_SM", "generator_omegaPu", 1.0),
                new FsvEntry("_LOAD___2_EC", "load_PPu", 0.216995));
    }

    @Test
    void testInconsistentEntry() throws URISyntaxException {
        CsvFsvParser parser = new CsvFsvParser(';');
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/fsvWrongEntry.csv")).toURI());
        List<FsvEntry> timeline = parser.parse(path);
        assertThat(timeline).isEmpty();
    }
}
