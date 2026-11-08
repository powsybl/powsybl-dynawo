/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy;

import com.powsybl.dynawo.cgmesdy.parser.CgmesDyImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: parses {@code mini_DY.xml} from the test classpath using the
 * real triplestore (Jena) and verifies the populated {@link CgmesDyModel}.
 *
 * <p>Run with:
 * <pre>mvn test -pl powsybl-dynawo-cgmes-dy -Dtest=CgmesDyModelLoaderTest</pre>
 * </p>
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
class CgmesDyModelLoaderTest {

    private static CgmesDyModel model;

    @BeforeAll
    static void parse() throws Exception {
        // Load the test resource as a stream
        InputStream dyStream = CgmesDyModelLoaderTest.class
            .getResourceAsStream("/mini_DY.xml");
        assertNotNull(dyStream, "mini_DY.xml must be on the test classpath");

        CgmesDyImporter importer = new CgmesDyImporter();
        model = importer.importDy(dyStream, CgmesDyConstants.CIM16_NS);
    }

    // =========================================================================
    // Synchronous machine
    // =========================================================================

    @Test
    void syncMachineTimeConstantReactanceisParsed() {
        assertEquals(1, model.syncTimeConstReactanceList().size(),
            "Expected 1 SynchronousMachineTimeConstantReactance");
        var sm = model.syncTimeConstReactanceList().get(0);
        assertEquals("sync_machine_dyn_1", sm.id());
        assertEquals("sync_machine_1", sm.synchronousMachineId());
        assertEquals("subtransient", sm.modelType());
        assertEquals(1.81, sm.xDirectSync(), 1e-6);
        assertEquals(0.3, sm.xDirectTrans(), 1e-6);
        assertEquals(0.23, sm.xDirectSubtrans(), 1e-6);
        assertEquals(1.01, sm.tpdo(), 1e-6);
        assertEquals(0.15, sm.xl(), 1e-6);
    }

    @Test
    void syncMachineSimplifiedisParsed() {
        assertEquals(1, model.syncSimplifiedList().size(),
            "Expected 1 SynchronousMachineSimplified");
        var sm = model.syncSimplifiedList().get(0);
        assertEquals("sync_machine_dyn_2", sm.id());
        assertEquals("sync_machine_2", sm.synchronousMachineId());
    }

    // =========================================================================
    // Governor
    // =========================================================================

    @Test
    void govSteam0isParsed() {
        assertEquals(1, model.govSteam0List().size(), "Expected 1 GovSteam0");
        var g = model.govSteam0List().get(0);
        assertEquals("gov_steam0_1", g.id());
        assertEquals("sync_machine_dyn_1", g.synchronousMachineId());
        assertEquals(900.0, g.mwbase(), 1e-6);
        assertEquals(0.05, g.r(), 1e-6);
        assertEquals(0.5, g.t1(), 1e-6);
        assertEquals(1.0, g.vmax(), 1e-6);
        assertEquals(0.0, g.vmin(), 1e-6);
        assertEquals(3.0, g.t2(), 1e-6);
        assertEquals(10.0, g.t3(), 1e-6);
        assertEquals(0.0, g.dt(), 1e-6);
    }

    // =========================================================================
    // Exciters
    // =========================================================================

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEDC1AisParsed() {
        assertEquals(1, model.excIEEEDC1AList().size(), "Expected 1 ExcIEEEDC1A");
        var e = model.excIEEEDC1AList().get(0);
        assertEquals("exc_dc1a_1", e.id());
        assertEquals("sync_machine_dyn_1", e.synchronousMachineId());
        assertEquals(46.0, e.ka(), 1e-6);
        assertEquals(0.06, e.ta(), 1e-6);
        assertEquals(-0.17, e.ke(), 1e-6);
        assertEquals(0.46, e.te(), 1e-6);
        assertEquals(1.0, e.vrmax(), 1e-6);
        assertEquals(-0.9, e.vrmin(), 1e-6);
        assertEquals(3.1, e.efd1(), 1e-6);
        assertEquals(0.33, e.seefd1(), 1e-6);
        assertFalse(e.uelin());
        assertFalse(e.exclim());
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEAC1AisParsed() {
        assertEquals(1, model.excIEEEAC1AList().size(), "Expected 1 ExcIEEEAC1A");
        var e = model.excIEEEAC1AList().get(0);
        assertEquals("exc_ac1a_1", e.id());
        assertEquals("sync_machine_dyn_2", e.synchronousMachineId());
        assertEquals(400.0, e.ka(), 1e-6);
        assertEquals(0.02, e.ta(), 1e-6);
        assertEquals(0.2, e.kc(), 1e-6);
        assertEquals(1.0, e.ke(), 1e-6);
        assertEquals(4.18, e.e1(), 1e-6);
        assertEquals(0.1, e.se1(), 1e-6);
    }

    // =========================================================================
    // PSS
    // =========================================================================

    @Test
    void pssIEEE2BisParsed() {
        assertEquals(1, model.pssIEEE2BList().size(), "Expected 1 PssIEEE2B");
        var p = model.pssIEEE2BList().get(0);
        assertEquals("pss_ieee2b_1", p.id());
        assertEquals("exc_dc1a_1", p.excitationSystemId());
        assertEquals("rotorSpeed", p.inputSignal1Type());
        assertEquals("generatorElectricalPower", p.inputSignal2Type());
        assertEquals(1.0, p.ks1(), 1e-6);
        assertEquals(0.1, p.ks2(), 1e-6);
        assertEquals(10.0, p.tw1(), 1e-6);
        assertEquals(0.12, p.t1(), 1e-6);
        assertEquals(0.1, p.vstmax(), 1e-6);
        assertEquals(-0.1, p.vstmin(), 1e-6);
    }

    // =========================================================================
    // Protection (OEL)
    // =========================================================================

    @Test
    void overexcLimIEEEisParsed() {
        assertEquals(1, model.oelIEEEList().size(), "Expected 1 OverexcLimIEEE");
        var o = model.oelIEEEList().get(0);
        assertEquals("oel_ieee_1", o.id());
        assertEquals("exc_dc1a_1", o.excitationSystemId());
        assertEquals(1.5, o.ifdmax(), 1e-6);
        assertEquals(1.3, o.ifdlim(), 1e-6);
        assertEquals(1.2, o.itfpu(), 1e-6);
        assertEquals(10.0, o.kramp(), 1e-6);
    }

    // =========================================================================
    // Wind
    // =========================================================================

    @Test
    void windType4aisParsed() {
        assertEquals(1, model.windType4aList().size(), "Expected 1 WindGenTurbineType4aIEC");
        var w = model.windType4aList().get(0);
        assertEquals("wind_type4a_1", w.id());
        assertEquals("wind_plant_1", w.powerPlantId());
        assertEquals(0.9, w.dipmax(), 1e-6);
        assertEquals(0.9, w.diqmax(), 1e-6);
        assertEquals(0.01, w.tg(), 1e-6);
    }

    // =========================================================================
    // Empty list sanity checks
    // =========================================================================

    @Test
    void unrelatedModelsAreEmpty() {
        assertTrue(model.govHydro1List().isEmpty(), "No GovHydro1 expected");
        assertTrue(model.excIEEEDC4BList().isEmpty(), "No ExcIEEEDC4B expected");
        assertTrue(model.loadMotorList().isEmpty(), "No LoadMotor expected");
        assertTrue(model.csConverterList().isEmpty(), "No CsConverter expected");
//        assertTrue(model.userDefinedList().isEmpty(), "No UserDefined expected");
    }
}
