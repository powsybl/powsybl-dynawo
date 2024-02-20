/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.dynawologs;

import com.powsybl.commons.reporter.TypedValue;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class CsvLogParserTest {

    @Test
    void testLog() throws URISyntaxException {

        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/dynawo.log")).toURI());
        List<LogEntry> logs = new CsvLogParser().parse(path);

        assertEquals(6, logs.size());
        assertEquals("DYNAWO VERSION  :     1.4.1", logs.get(0).message());
        assertEquals(TypedValue.INFO_SEVERITY, logs.get(0).severity());
        assertEquals("time iter num   order (k)      time step (h)", logs.get(1).message());
        assertEquals(TypedValue.INFO_SEVERITY, logs.get(1).severity());
        assertEquals("0.000 0           0              0.010", logs.get(2).message());
        assertEquals(TypedValue.INFO_SEVERITY, logs.get(2).severity());
        assertEquals("call of SolverReInit i.e. a new symbolic and numerical factorization will be performed", logs.get(3).message());
        assertEquals(TypedValue.DEBUG_SEVERITY, logs.get(3).severity());
        assertEquals("five consecutive steps have been taken that satisfy a scaled step length test", logs.get(4).message());
        assertEquals(TypedValue.WARN_SEVERITY, logs.get(4).severity());
        assertEquals("KINSOL fails to solve the problem ( DYNSolverKINAlgRestoration.cpp:394 )", logs.get(5).message());
        assertEquals(TypedValue.ERROR_SEVERITY, logs.get(5).severity());
    }
}
