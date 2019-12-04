/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.crv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.powsybl.dynawo.crv.DynawoCurve;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoCurveTest {

    @Test
    public void test() {

        DynawoCurve curve = new DynawoCurve("model", "variable");

        assertEquals("model", curve.getModel());
        assertEquals("variable", curve.getVariable());
    }
}
