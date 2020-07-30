/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.powsybl.dynawo.dyd.LoadAlphaBeta.Parameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadAlphaBetaTest extends DynamicModelUtilTest {

    @Test
    public void test() {
        LoadAlphaBeta.Parameters parameters = (Parameters) LoadAlphaBeta.Parameters.load(parametersDatabase, "LoadAlphaBeta");
        assertEquals("1.5", parameters.getLoadAlpha());
        assertEquals("2.5", parameters.getLoadBeta());
    }
}
