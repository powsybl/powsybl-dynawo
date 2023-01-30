/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.test.AbstractConverterTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DynawoVersionTest extends AbstractConverterTest {

    private static final DynawoVersion DV_MIN = new DynawoVersion(1, 3, 0);

    @Test
    public void testDynawoVersionCreation() {
        assertEquals("1.3.4", new DynawoVersion(1, 3, 4).toString());
        assertEquals("1.10.4", DynawoVersion.createFromString("1.10.4").toString());
        assertEquals("1.10.4", DynawoVersion.createFromString("1:10:4", ":").toString());

        // Errors
        assertThrows(PowsyblException.class, DynawoVersion::new);
        assertThrows(PowsyblException.class, () -> new DynawoVersion(1, -4));
        assertThrows(PowsyblException.class, () -> DynawoVersion.createFromString("1.-2"));
        assertThrows(PowsyblException.class, () -> DynawoVersion.createFromString("1:2"));
        assertThrows(PowsyblException.class, () -> DynawoVersion.createFromString("12.a"));
    }

    @Test
    public void testVersionEqual() {
        DynawoVersion dv1 = DynawoVersion.createFromString("1.3.0");
        DynawoVersion dv2 = DynawoVersion.createFromString("1.3");
        assertEquals(0, DV_MIN.compareTo(dv1));
        assertEquals(0, DV_MIN.compareTo(dv2));
    }

    @Test
    public void testVersionAboveMin() {
        DynawoVersion dv1 = DynawoVersion.createFromString("1.3.1");
        DynawoVersion dv2 = DynawoVersion.createFromString("2.1");
        DynawoVersion dv3 = DynawoVersion.createFromString("1.3.0.1");
        assertEquals(-1, DV_MIN.compareTo(dv1));
        assertEquals(-1, DV_MIN.compareTo(dv2));
        assertEquals(-1, DV_MIN.compareTo(dv3));
    }

    @Test
    public void testVersionBelowMin() {
        DynawoVersion dv1 = DynawoVersion.createFromString("1.2.0");
        DynawoVersion dv2 = DynawoVersion.createFromString("1");
        assertEquals(1, DV_MIN.compareTo(dv1));
        assertEquals(1, DV_MIN.compareTo(dv2));
        assertEquals(1, DV_MIN.compareTo(null));
    }
}
