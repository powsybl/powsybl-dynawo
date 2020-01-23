/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.results.CurvesCsv;
import com.powsybl.dynawo.results.DynawoResults;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoResultsTest {

    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void resultException() {
        exception.expect(PowsyblException.class);
        CurvesCsv.parse(getClass().getResourceAsStream("/nordic32/invalidCurves.csv"));
        exception.expectMessage("Bad CSV header, should be \ntime;...");
    }

    @Test
    public void emptyException() {
        exception.expect(PowsyblException.class);
        CurvesCsv.parse(getClass().getResourceAsStream("/nordic32/emptyCurves.csv"));
        exception.expectMessage("CSV header is missing");
    }

    @Test
    public void columnsException() {
        exception.expect(PowsyblException.class);
        CurvesCsv.parse(getClass().getResourceAsStream("/nordic32/inconsistentCurves.csv"));
        exception.expectMessage("Columns of line 217 are inconsistent with header");
    }

    @Test
    public void duplicateException() {
        exception.expect(PowsyblException.class);
        CurvesCsv.parse(getClass().getResourceAsStream("/nordic32/duplicateTitle.csv"));
        exception.expectMessage("Bad CSV header, there are duplicates in time series names time");
    }
}
