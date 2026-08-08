/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters;

import com.powsybl.dynawo.cgmesdy.CgmesDyConstants;
import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.dynawo.cgmesdy.exciters.ac.*;
import com.powsybl.dynawo.cgmesdy.exciters.dc.*;
import com.powsybl.dynawo.cgmesdy.exciters.st.*;
import com.powsybl.dynawo.cgmesdy.exciters.vendor.*;
import com.powsybl.dynawo.cgmesdy.parser.CgmesDyModelLoader;
import com.powsybl.triplestore.api.TripleStore;
import com.powsybl.triplestore.api.TripleStoreFactory;
import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all 55 exciter types (4 IEEE-DC + 8 IEEE-AC + 7 IEEE-ST + 36 vendor)
 * parsed from {@code exciters_pss_cim16.xml}.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@DisplayName("ExciterLoader – all 55 exciter types (CIM16)")
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
class ExciterLoaderTest {

    private static final String FIXTURE = "/com/powsybl/dynawo/cgmesdy/exciters_pss_cim16.xml";
    private static final double T = 1e-9;
    private static CgmesDyModel MODEL;

    @BeforeAll
    static void loadFixture() throws Exception {
        try (InputStream is = ExciterLoaderTest.class.getResourceAsStream(FIXTURE)) {
            assertNotNull(is, "Fixture not found: " + FIXTURE);
            TripleStore ts = TripleStoreFactory.create("rdf4j");
            ts.read(is, CgmesDyConstants.RDF_NS, "urn:test:exc");
            MODEL = new CgmesDyModelLoader(ts, CgmesDyConstants.CIM16_NS).load();
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private static <X> X one(java.util.List<X> list, String name) {
        assertEquals(1, list.size(), name + " count");
        return list.get(0);
    }

    /**
     * Assert that {@code synchronousMachineId} is non-blank and contains the expected
     * stub fragment. This verifies the SPARQL fix:
     * {@code ExcitationSystemDynamics.SynchronousMachineDynamics → ?synchronousMachineId}
     * (previously the variable was mistakenly mapped to {@code ?excitationSystemId}).
     */
    private static void hasSmId(String actual, String fragment) {
        assertFalse(actual.isBlank(),
            "synchronousMachineId must not be blank — check SPARQL ExcitationSystemDynamics.SynchronousMachineDynamics mapping");
        assertTrue(actual.contains(fragment),
            "synchronousMachineId should reference '" + fragment + "', got: " + actual);
    }

    private static void hasId(String id, String fragment) {
        assertTrue(id.contains(fragment), "ID should contain '" + fragment + "', got: " + id);
    }

    // =========================================================================
    // IEEE DC exciters
    // =========================================================================

    @Nested @DisplayName("ExcIEEEDC1A") class DC1A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEDC1AList().size()); }

        @Test void fields() {
            ExcIEEEDC1A e = one(MODEL.excIEEEDC1AList(), "ExcIEEEDC1A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            hasId(e.id(), "excieeedc1a");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(0.01, e.tr(), T);
            assertEquals(46.0, e.ka(), T);
            assertEquals(0.06, e.ta(), T);
            assertEquals(1.0, e.vrmax(), T);
            assertEquals(-0.9, e.vrmin(), T);
            assertEquals(0.05, e.ke(), T);
            assertEquals(0.46, e.te(), T);
            assertEquals(0.1, e.kf(), T);
            assertEquals(1.0, e.tf(), T);
            assertEquals(0.09, e.kc(), T);
            assertEquals(0.48, e.kd(), T);
            assertEquals(3.1, e.efd1(), T);
            assertEquals(0.33, e.seefd1(), T);
            assertEquals(2.3, e.efd2(), T);
            assertEquals(0.1, e.seefd2(), T);
            assertTrue(e.uelin());
            assertFalse(e.exclim());
        }

        @Test void constraint() {
            assertTrue(MODEL.excIEEEDC1AList().get(0).vrmax() >= MODEL.excIEEEDC1AList().get(0).vrmin()); }
    }

    @Nested @DisplayName("ExcIEEEDC2A") class DC2A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEDC2AList().size()); }

        @Test void fields() {
            ExcIEEEDC2A e = one(MODEL.excIEEEDC2AList(), "ExcIEEEDC2A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(50.0, e.ka(), T);
            assertEquals(4.95, e.vrmax(), T);
            assertEquals(-4.9, e.vrmin(), T);
            assertEquals(-0.05, e.ke(), T);
            assertFalse(e.uelin());
            assertTrue(e.exclim());
        }
    }

    @Nested @DisplayName("ExcIEEEDC3A") class DC3A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEDC3AList().size()); }

        @Test void fields() {
            ExcIEEEDC3A e = one(MODEL.excIEEEDC3AList(), "ExcIEEEDC3A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(0.05, e.trh(), T);
            assertEquals(1.0, e.kv(), T);
            assertEquals(1.0, e.vmax(), T);
            assertEquals(1.33, e.te(), T);
            assertEquals(3.375, e.efd1(), T);
            assertTrue(e.exclim());
        }
    }

    @Nested @DisplayName("ExcIEEEDC4B") class DC4B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEDC4BList().size()); }

        @Test void fields() {
            ExcIEEEDC4B e = one(MODEL.excIEEEDC4BList(), "ExcIEEEDC4B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(48.0, e.kp(), T);
            assertEquals(10.0, e.ki(), T);
            assertEquals(20.0, e.kd(), T);
            assertEquals(2.7, e.vrmax(), T);
            assertTrue(e.uelin());
            assertFalse(e.oelin());
        }
    }

    // =========================================================================
    // IEEE AC exciters
    // =========================================================================

    @Nested @DisplayName("ExcIEEEAC1A") class AC1A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC1AList().size()); }

        @Test void fields() {
            ExcIEEEAC1A e = one(MODEL.excIEEEAC1AList(), "ExcIEEEAC1A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(400.0, e.ka(), T);
            assertEquals(0.02, e.ta(), T);
            assertEquals(14.5, e.vamax(), T);
            assertEquals(-14.5, e.vamin(), T);
            assertEquals(0.8, e.te(), T);
            assertEquals(0.03, e.kf(), T);
            assertEquals(6.24, e.vfemax(), T);
            assertEquals(4.18, e.e1(), T);
            assertEquals(0.1, e.se1(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEAC2A") class AC2A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC2AList().size()); }

        @Test void fields() {
            ExcIEEEAC2A e = one(MODEL.excIEEEAC2AList(), "ExcIEEEAC2A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(25.0, e.kb(), T);
            assertEquals(105.0, e.vrmax(), T);
            assertEquals(4.4, e.vfemax(), T);
            assertEquals(1.0, e.kh(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEAC3A") class AC3A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC3AList().size()); }

        @Test void fields() {
            ExcIEEEAC3A e = one(MODEL.excIEEEAC3AList(), "ExcIEEEAC3A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(45.62, e.ka(), T);
            assertEquals(3.77, e.kr(), T);
            assertEquals(0.1, e.vemin(), T);
            assertEquals(0.05, e.kn(), T);
            assertEquals(2.36, e.efdn(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEAC4A") class AC4A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC4AList().size()); }

        @Test void fields() {
            ExcIEEEAC4A e = one(MODEL.excIEEEAC4AList(), "ExcIEEEAC4A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(200.0, e.ka(), T);
            assertEquals(10.0, e.tb(), T);
            assertEquals(10.0, e.vimax(), T);
            assertEquals(0.0, e.kc(), T);
            assertEquals(5.64, e.vrmax(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEAC5A") class AC5A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC5AList().size()); }

        @Test void fields() {
            ExcIEEEAC5A e = one(MODEL.excIEEEAC5AList(), "ExcIEEEAC5A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(400.0, e.ka(), T);
            assertEquals(7.3, e.vrmax(), T);
            assertEquals(1.0, e.tf1(), T);
            assertEquals(0.8, e.tf2(), T);
            assertEquals(5.6, e.efdn(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEAC6A") class AC6A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC6AList().size()); }

        @Test void fields() {
            ExcIEEEAC6A e = one(MODEL.excIEEEAC6AList(), "ExcIEEEAC6A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(536.0, e.ka(), T);
            assertEquals(0.18, e.tk(), T);
            assertEquals(75.0, e.vhmax(), T);
            assertEquals(92.0, e.kh(), T);
            assertEquals(0.07, e.td(), T);
            assertEquals(19.0, e.vfelim(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEAC7B – dual boolean fields") class AC7B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC7BList().size()); }

        @Test void fields() {
            ExcIEEEAC7B e = one(MODEL.excIEEEAC7BList(), "ExcIEEEAC7B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(4.24, e.kpr(), T);
            assertEquals(65.36, e.kpa(), T);
            assertEquals(6.0, e.vfemax(), T);
            assertEquals(0.0, e.vemin(), T);
            assertFalse(e.uelin());
            assertTrue(e.oelin());
        }
    }

    @Nested @DisplayName("ExcIEEEAC8B") class AC8B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEAC8BList().size()); }

        @Test void fields() {
            ExcIEEEAC8B e = one(MODEL.excIEEEAC8BList(), "ExcIEEEAC8B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(80.0, e.kpr(), T);
            assertEquals(35.0, e.vrmax(), T);
            assertEquals(-10.0, e.vrmin(), T);
            assertEquals(0.55, e.kc(), T);
            assertTrue(e.uelin());
        }
    }

    // =========================================================================
    // IEEE ST exciters
    // =========================================================================

    @Nested @DisplayName("ExcIEEEST1A – three boolean fields") class ST1A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST1AList().size()); }

        @Test void fields() {
            ExcIEEEST1A e = one(MODEL.excIEEEST1AList(), "ExcIEEEST1A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(190.0, e.ka(), T);
            assertEquals(7.8, e.vamax(), T);
            assertEquals(-6.81, e.vamin(), T);
            assertEquals(0.08, e.kc(), T);
            assertEquals(4.54, e.klr(), T);
            assertTrue(e.uelin());
            assertFalse(e.pssin());
            assertFalse(e.ilr());
        }
    }

    @Nested @DisplayName("ExcIEEEST2A – uelin boolean") class ST2A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST2AList().size()); }

        @Test void fields() {
            ExcIEEEST2A e = one(MODEL.excIEEEST2AList(), "ExcIEEEST2A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(120.0, e.ka(), T);
            assertEquals(4.88, e.kp(), T);
            assertEquals(8.0, e.ki(), T);
            assertEquals(99.0, e.efdmax(), T);
            assertFalse(e.uelin());
        }
    }

    @Nested @DisplayName("ExcIEEEST3A – uelin boolean") class ST3A {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST3AList().size()); }

        @Test void fields() {
            ExcIEEEST3A e = one(MODEL.excIEEEST3AList(), "ExcIEEEST3A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(200.0, e.ka(), T);
            assertEquals(7.93, e.km(), T);
            assertEquals(1.0, e.kg(), T);
            assertEquals(0.081, e.xl(), T);
            assertEquals(8.63, e.vbmax(), T);
            assertEquals(6.53, e.vgmax(), T);
            assertTrue(e.uelin());
        }
    }

    @Nested @DisplayName("ExcIEEEST4B – dual boolean") class ST4B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST4BList().size()); }

        @Test void fields() {
            ExcIEEEST4B e = one(MODEL.excIEEEST4BList(), "ExcIEEEST4B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(10.75, e.kpr(), T);
            assertEquals(9.3, e.kp(), T);
            assertEquals(11.63, e.vbmax(), T);
            assertFalse(e.uelin());
            assertTrue(e.oelin());
        }
    }

    @Nested @DisplayName("ExcIEEEST5B – time-constant array") class ST5B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST5BList().size()); }

        @Test void fields() {
            ExcIEEEST5B e = one(MODEL.excIEEEST5BList(), "ExcIEEEST5B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(5.0, e.vrmax(), T);
            assertEquals(-4.0, e.vrmin(), T);
            assertEquals(2.0, e.tob1(), T);
            assertEquals(0.1, e.toc1(), T);
            assertEquals(10.0, e.tub1(), T);
            assertEquals(2.0, e.tuc1(), T);
            assertEquals(0.0, e.kc(), T);
        }
    }

    @Nested @DisplayName("ExcIEEEST6B – String oelin enum") class ST6B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST6BList().size()); }

        @Test void fields() {
            ExcIEEEST6B e = one(MODEL.excIEEEST6BList(), "ExcIEEEST6B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(0.7, e.vimax(), T);
            assertEquals(45.0, e.kia(), T);
            assertEquals(17.33, e.klr(), T);
            assertEquals(0.02, e.tg(), T);
            assertNotNull(e.oelin());
            assertFalse(e.oelin().isBlank(), "oelin enum must not be blank");
        }
    }

    @Nested @DisplayName("ExcIEEEST7B – String uelin and oelin enums") class ST7B {
        @Test void count() {
            assertEquals(1, MODEL.excIEEEST7BList().size()); }

        @Test void fields() {
            ExcIEEEST7B e = one(MODEL.excIEEEST7BList(), "ExcIEEEST7B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(0.02, e.tr(), T);
            assertEquals(1.0, e.kh(), T);
            assertEquals(3.0, e.tia(), T);
            assertEquals(40.0, e.kpa(), T);
            assertEquals(6.0, e.vrmax(), T);
            assertEquals(0.02, e.tg(), T);
            assertFalse(e.uelin().isBlank(), "uelin must not be blank");
            assertFalse(e.oelin().isBlank(), "oelin must not be blank");
        }
    }

    // =========================================================================
    // Vendor exciters (36)
    // =========================================================================

    @Nested @DisplayName("ExcAC1A") class VendorAC1A {
        @Test void count() {
            assertEquals(1, MODEL.excAC1AList().size()); }

        @Test void fields() {
            ExcAC1A e = one(MODEL.excAC1AList(), "ExcAC1A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(400.0, e.ka(), T);
            assertEquals(14.5, e.vamax(), T);
            assertEquals(4.18, e.e1(), T);
        }
    }

    @Nested @DisplayName("ExcAC2A") class VendorAC2A {
        @Test void count() {
            assertEquals(1, MODEL.excAC2AList().size()); }

        @Test void fields() {
            ExcAC2A e = one(MODEL.excAC2AList(), "ExcAC2A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(25.0, e.kb(), T);
            assertEquals(1.0, e.kh(), T);
        }
    }

    @Nested @DisplayName("ExcAC3A") class VendorAC3A {
        @Test void count() {
            assertEquals(1, MODEL.excAC3AList().size()); }

        @Test void fields() {
            ExcAC3A e = one(MODEL.excAC3AList(), "ExcAC3A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(3.77, e.kr(), T);
            assertEquals(16.0, e.vfemax(), T);
        }
    }

    @Nested @DisplayName("ExcAC4A") class VendorAC4A {
        @Test void count() {
            assertEquals(1, MODEL.excAC4AList().size()); }

        @Test void fields() {
            ExcAC4A e = one(MODEL.excAC4AList(), "ExcAC4A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(200.0, e.ka(), T);
            assertEquals(0.0, e.kc(), T);
        }
    }

    @Nested @DisplayName("ExcAC5A") class VendorAC5A {
        @Test void count() {
            assertEquals(1, MODEL.excAC5AList().size()); }

        @Test void fields() {
            ExcAC5A e = one(MODEL.excAC5AList(), "ExcAC5A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(1.0, e.tf1(), T);
            assertEquals(0.8, e.tf2(), T);
            assertEquals(0.0, e.tf3(), T);
        }
    }

    @Nested @DisplayName("ExcAC6A") class VendorAC6A {
        @Test void count() {
            assertEquals(1, MODEL.excAC6AList().size()); }

        @Test void fields() {
            ExcAC6A e = one(MODEL.excAC6AList(), "ExcAC6A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(536.0, e.ka(), T);
            assertEquals(75.0, e.vhmax(), T);
            assertEquals(0.173, e.kc(), T);
        }
    }

    @Nested @DisplayName("ExcAC8B") class VendorAC8B {
        @Test void count() {
            assertEquals(1, MODEL.excAC8BList().size()); }

        @Test void fields() {
            ExcAC8B e = one(MODEL.excAC8BList(), "ExcAC8B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(80.0, e.kpr(), T);
            assertEquals(0.55, e.kc(), T);
        }
    }

    @Nested @DisplayName("ExcAVR1") class AVR1 {
        @Test void count() {
            assertEquals(1, MODEL.excAVR1List().size()); }

        @Test void fields() {
            ExcAVR1 e = one(MODEL.excAVR1List(), "ExcAVR1");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(500.0, e.ka(), T);
            assertEquals(0.02, e.tr(), T);
            assertEquals(-5.0, e.vrmn(), T);
            assertEquals(5.0, e.vrmx(), T);
        }

        @Test void limitsSymmetric() {
            ExcAVR1 e = MODEL.excAVR1List().get(0);
            assertTrue(e.vrmx() >= e.vrmn());
        }
    }

    @Nested @DisplayName("ExcAVR2") class AVR2 {
        @Test void count() {
            assertEquals(1, MODEL.excAVR2List().size()); }

        @Test void fields() {
            ExcAVR2 e = one(MODEL.excAVR2List(), "ExcAVR2");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(0.5, e.tc(), T);
            assertEquals(-5.0, e.vrmn(), T);
        }
    }

    @Nested @DisplayName("ExcAVR3") class AVR3 {
        @Test void count() {
            assertEquals(1, MODEL.excAVR3List().size()); }

        @Test void fields() {
            ExcAVR3 e = one(MODEL.excAVR3List(), "ExcAVR3");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(5.0, e.efdn(), T);
            assertEquals(-5.0, e.vrmn(), T);
        }
    }

    @Nested @DisplayName("ExcAVR4") class AVR4 {
        @Test void count() {
            assertEquals(1, MODEL.excAVR4List().size()); }

        @Test void fields() {
            ExcAVR4 e = one(MODEL.excAVR4List(), "ExcAVR4");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(1.0, e.ke(), T);
            assertEquals(0.05, e.kf(), T);
        }
    }

    @Nested @DisplayName("ExcAVR5") class AVR5 {
        @Test void count() {
            assertEquals(1, MODEL.excAVR5List().size()); }

        @Test void fields() {
            ExcAVR5 e = one(MODEL.excAVR5List(), "ExcAVR5");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(200.0, e.ka(), T);
            assertEquals(0.0, e.rex(), T);
            assertEquals(0.02, e.ta(), T);
        }
    }

    @Nested @DisplayName("ExcAVR7") class AVR7 {
        @Test void count() {
            assertEquals(1, MODEL.excAVR7List().size()); }

        @Test void fields() {
            ExcAVR7 e = one(MODEL.excAVR7List(), "ExcAVR7");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(500.0, e.k1(), T);
            assertEquals(5.0, e.vmax1(), T);
            assertEquals(-5.0, e.vmin1(), T);
            assertEquals(0.01, e.t1(), T);
        }
    }

    @Nested @DisplayName("ExcBBC") class BBC {
        @Test void count() {
            assertEquals(1, MODEL.excBBCList().size()); }

        @Test void fields() {
            ExcBBC e = one(MODEL.excBBCList(), "ExcBBC");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(5.0, e.t1(), T);
            assertEquals(99.0, e.efdmax(), T);
            assertEquals(-99.0, e.efdmin(), T);
            assertEquals(0.0, e.xe(), T);
        }
    }

    @Nested @DisplayName("ExcCZ") class CZ {
        @Test void count() {
            assertEquals(1, MODEL.excCZList().size()); }

        @Test void fields() {
            ExcCZ e = one(MODEL.excCZList(), "ExcCZ");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(20.0, e.ka(), T);
            assertEquals(9.0, e.efdmax(), T);
            assertEquals(0.02, e.krb(), T);
        }
    }

    @Nested @DisplayName("ExcDC1A") class VendorDC1A {
        @Test void count() {
            assertEquals(1, MODEL.excDC1AList().size()); }

        @Test void fields() {
            ExcDC1A e = one(MODEL.excDC1AList(), "ExcDC1A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(46.0, e.ka(), T);
            assertEquals(3.1, e.efd1(), T);
            assertTrue(e.exclim());
        }
    }

    @Nested @DisplayName("ExcDC2A") class VendorDC2A {
        @Test void count() {
            assertEquals(1, MODEL.excDC2AList().size()); }

        @Test void fields() {
            ExcDC2A e = one(MODEL.excDC2AList(), "ExcDC2A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(-0.05, e.ke(), T);
            assertEquals(-99.0, e.vlb(), T);
            assertFalse(e.exclim());
        }
    }

    @Nested @DisplayName("ExcDC3A") class VendorDC3A {
        @Test void count() {
            assertEquals(1, MODEL.excDC3AList().size()); }

        @Test void fields() {
            ExcDC3A e = one(MODEL.excDC3AList(), "ExcDC3A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(0.675, e.tf1(), T);
            assertEquals(3.375, e.efd1(), T);
            assertTrue(e.exclim());
        }
    }

    @Nested @DisplayName("ExcELIN1") class ELIN1 {
        @Test void count() {
            assertEquals(1, MODEL.excELIN1List().size()); }

        @Test void fields() {
            ExcELIN1 e = one(MODEL.excELIN1List(), "ExcELIN1");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(100.0, e.ka(), T);
            assertEquals(0.004, e.tfi(), T);
            assertEquals(99.0, e.efmax(), T);
            assertEquals(-99.0, e.efmin(), T);
        }
    }

    @Nested @DisplayName("ExcELIN2") class ELIN2 {
        @Test void count() {
            assertEquals(1, MODEL.excELIN2List().size()); }

        @Test void fields() {
            ExcELIN2 e = one(MODEL.excELIN2List(), "ExcELIN2");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(1.0, e.te(), T);
            assertEquals(3.0, e.ermax(), T);
            assertEquals(-3.0, e.ermin(), T);
            assertEquals(1.0, e.iefmax(), T);
        }
    }

    @Nested @DisplayName("ExcHU") class HU {
        @Test void count() {
            assertEquals(1, MODEL.excHUList().size()); }

        @Test void fields() {
            ExcHU e = one(MODEL.excHUList(), "ExcHU");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(3.0, e.emax(), T);
            assertEquals(-3.0, e.emin(), T);
            assertEquals(0.5, e.te(), T);
        }
    }

    @Nested @DisplayName("ExcNI") class NI {
        @Test void count() {
            assertEquals(1, MODEL.excNIList().size()); }

        @Test void fields() {
            ExcNI e = one(MODEL.excNIList(), "ExcNI");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(100.0, e.ka(), T);
            assertEquals(5.0, e.vrmx(), T);
        }
    }

    @Nested @DisplayName("ExcOEX3T") class OEX3T {
        @Test void count() {
            assertEquals(1, MODEL.excOEX3TList().size()); }

        @Test void fields() {
            ExcOEX3T e = one(MODEL.excOEX3TList(), "ExcOEX3T");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(1.0, e.ka(), T);
            assertEquals(5.0, e.vrmax(), T);
            assertEquals(0.5, e.t1(), T);
        }
    }

    @Nested @DisplayName("ExcPIC") class PIC {
        @Test void count() {
            assertEquals(1, MODEL.excPICList().size()); }

        @Test void fields() {
            ExcPIC e = one(MODEL.excPICList(), "ExcPIC");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(20.0, e.ka(), T);
            assertEquals(5.0, e.efdmax(), T);
            assertEquals(-5.0, e.efdmin(), T);
            assertEquals(5.0, e.vr1(), T);
        }
    }

    @Nested @DisplayName("ExcREXS – String feedbackSignal + boolean exclfb") class REXS {
        @Test void count() {
            assertEquals(1, MODEL.excREXSList().size()); }

        @Test void fields() {
            ExcREXS e = one(MODEL.excREXSList(), "ExcREXS");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(46.0, e.ka(), T);
            assertEquals(5.0, e.vrmax(), T);
            assertFalse(e.feedbackSignal().isBlank());
            assertTrue(e.exclfb());
        }
    }

    @Nested @DisplayName("ExcRQB") class RQB {
        @Test void count() {
            assertEquals(1, MODEL.excRQBList().size()); }

        @Test void fields() {
            ExcRQB e = one(MODEL.excRQBList(), "ExcRQB");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(5.0, e.ucmax(), T);
            assertEquals(-5.0, e.ucmin(), T);
        }
    }

    @Nested @DisplayName("ExcSCRX – boolean rcmxFlag") class SCRX {
        @Test void count() {
            assertEquals(1, MODEL.excSCRXList().size()); }

        @Test void fields() {
            ExcSCRX e = one(MODEL.excSCRXList(), "ExcSCRX");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(200.0, e.k(), T);
            assertEquals(5.0, e.emax(), T);
            assertEquals(-5.0, e.emin(), T);
            assertTrue(e.rcmxFlag());
        }
    }

    @Nested @DisplayName("ExcSEXS") class SEXS {
        @Test void count() {
            assertEquals(1, MODEL.excSEXSList().size()); }

        @Test void fields() {
            ExcSEXS e = one(MODEL.excSEXSList(), "ExcSEXS");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(100.0, e.k(), T);
            assertEquals(0.1, e.tatb(), T);
            assertEquals(5.0, e.emax(), T);
        }
    }

    @Nested @DisplayName("ExcSK") class SK {
        @Test void count() {
            assertEquals(1, MODEL.excSKList().size()); }

        @Test void fields() {
            ExcSK e = one(MODEL.excSKList(), "ExcSK");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(1.0, e.k(), T);
            assertEquals(100.0, e.sbase(), T);
            assertEquals(5.0, e.uimax(), T);
            assertEquals(-5.0, e.uimin(), T);
        }
    }

    @Nested @DisplayName("ExcST1A – pssin and ilr booleans") class VendorST1A {
        @Test void count() {
            assertEquals(1, MODEL.excST1AList().size()); }

        @Test void fields() {
            ExcST1A e = one(MODEL.excST1AList(), "ExcST1A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(190.0, e.ka(), T);
            assertEquals(4.54, e.klr(), T);
            assertFalse(e.pssin());
            assertTrue(e.ilr());
        }
    }

    @Nested @DisplayName("ExcST2A") class VendorST2A {
        @Test void count() {
            assertEquals(1, MODEL.excST2AList().size()); }

        @Test void fields() {
            ExcST2A e = one(MODEL.excST2AList(), "ExcST2A");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(120.0, e.ka(), T);
            assertEquals(99.0, e.efdmax(), T);
        }
    }

    @Nested @DisplayName("ExcST3") class VendorST3 {
        @Test void count() {
            assertEquals(1, MODEL.excST3List().size()); }

        @Test void fields() {
            ExcST3 e = one(MODEL.excST3List(), "ExcST3");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(7.93, e.km(), T);
            assertEquals(8.63, e.vbmax(), T);
            // ExcST3 (vendor) does NOT have vgmax – verify it compiles without it
        }
    }

    @Nested @DisplayName("ExcST4B") class VendorST4B {
        @Test void count() {
            assertEquals(1, MODEL.excST4BList().size()); }

        @Test void fields() {
            ExcST4B e = one(MODEL.excST4BList(), "ExcST4B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(10.75, e.kpr(), T);
            assertEquals(11.63, e.vbmax(), T);
            assertEquals(0.113, e.kc(), T);
        }
    }

    @Nested @DisplayName("ExcST6B") class VendorST6B {
        @Test void count() {
            assertEquals(1, MODEL.excST6BList().size()); }

        @Test void fields() {
            ExcST6B e = one(MODEL.excST6BList(), "ExcST6B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(45.0, e.kia(), T);
            assertEquals(17.33, e.klr(), T);
            assertEquals(4.81, e.vrmax(), T);
            assertEquals(-3.85, e.vrmin(), T);
        }
    }

    @Nested @DisplayName("ExcST7B") class VendorST7B {
        @Test void count() {
            assertEquals(1, MODEL.excST7BList().size()); }

        @Test void fields() {
            ExcST7B e = one(MODEL.excST7BList(), "ExcST7B");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(40.0, e.kpa(), T);
            assertEquals(6.0, e.vrmax(), T);
            assertEquals(1.1, e.vmax(), T);
        }
    }

    @Nested
    @DisplayName("ExcSYMPTR")
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    class SYMPTR {
        @Test void count() {
            assertEquals(1, MODEL.excSYMPTRList().size()); }

        @Test void fields() {
            ExcSYMPTR e = one(MODEL.excSYMPTRList(), "ExcSYMPTR");
            hasSmId(e.synchronousMachineId(), "sm-exc");
            assertEquals(5.0, e.efmx(), T);
            assertEquals(-5.0, e.efmn(), T);
            assertEquals(5.0, e.vrmax(), T);
        }
    }

    // =========================================================================
    // Cross-cutting
    // =========================================================================

    @Nested @DisplayName("Cross-cutting: total count and non-blank IDs")
    class CrossCutting {

        @Test void totalExciterCount() {
            int total =
                MODEL.excIEEEDC1AList().size() + MODEL.excIEEEDC2AList().size() +
                MODEL.excIEEEDC3AList().size() + MODEL.excIEEEDC4BList().size() +
                MODEL.excIEEEAC1AList().size() + MODEL.excIEEEAC2AList().size() +
                MODEL.excIEEEAC3AList().size() + MODEL.excIEEEAC4AList().size() +
                MODEL.excIEEEAC5AList().size() + MODEL.excIEEEAC6AList().size() +
                MODEL.excIEEEAC7BList().size() + MODEL.excIEEEAC8BList().size() +
                MODEL.excIEEEST1AList().size() + MODEL.excIEEEST2AList().size() +
                MODEL.excIEEEST3AList().size() + MODEL.excIEEEST4BList().size() +
                MODEL.excIEEEST5BList().size() + MODEL.excIEEEST6BList().size() +
                MODEL.excIEEEST7BList().size() +
                MODEL.excAC1AList().size() + MODEL.excAC2AList().size() +
                MODEL.excAC3AList().size() + MODEL.excAC4AList().size() +
                MODEL.excAC5AList().size() + MODEL.excAC6AList().size() +
                MODEL.excAC8BList().size() +
                MODEL.excAVR1List().size() + MODEL.excAVR2List().size() +
                MODEL.excAVR3List().size() + MODEL.excAVR4List().size() +
                MODEL.excAVR5List().size() + MODEL.excAVR7List().size() +
                MODEL.excBBCList().size() + MODEL.excCZList().size() +
                MODEL.excDC1AList().size() + MODEL.excDC2AList().size() +
                MODEL.excDC3AList().size() +
                MODEL.excELIN1List().size() + MODEL.excELIN2List().size() +
                MODEL.excHUList().size() + MODEL.excNIList().size() +
                MODEL.excOEX3TList().size() + MODEL.excPICList().size() +
                MODEL.excREXSList().size() + MODEL.excRQBList().size() +
                MODEL.excSCRXList().size() + MODEL.excSEXSList().size() +
                MODEL.excSKList().size() +
                MODEL.excST1AList().size() + MODEL.excST2AList().size() +
                MODEL.excST3List().size() + MODEL.excST4BList().size() +
                MODEL.excST6BList().size() + MODEL.excST7BList().size() +
                MODEL.excSYMPTRList().size();
            assertEquals(55, total, "Expected exactly 55 exciter instances");
        }

        @Test void allVrmaxGteVrmin() {
            // IEEE DC / AC / ST – all have vrmax/vrmin
            MODEL.excIEEEDC1AList().forEach(e -> assertTrue(e.vrmax() >= e.vrmin()));
            MODEL.excIEEEDC2AList().forEach(e -> assertTrue(e.vrmax() >= e.vrmin()));
            MODEL.excIEEEAC1AList().forEach(e -> assertTrue(e.vrmax() >= e.vrmin()));
            MODEL.excIEEEST1AList().forEach(e -> assertTrue(e.vrmax() >= e.vrmin()));
        }

        @Test
        @DisplayName("All exciters have non-blank synchronousMachineId (verifies SPARQL fix)")
        void allExcitersHaveSynchronousMachineId() {
            // Bug: ExcitationSystemDynamics.SynchronousMachineDynamics was bound to
            // ?excitationSystemId instead of ?synchronousMachineId — fixed in all .sparql files
            MODEL.excIEEEDC1AList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEDC1A"));
            MODEL.excIEEEDC2AList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEDC2A"));
            MODEL.excIEEEDC3AList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEDC3A"));
            MODEL.excIEEEDC4BList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEDC4B"));
            MODEL.excIEEEAC1AList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEAC1A"));
            MODEL.excIEEEAC7BList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEAC7B"));
            MODEL.excIEEEST1AList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEST1A"));
            MODEL.excIEEEST7BList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcIEEEST7B"));
            MODEL.excAC1AList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcAC1A"));
            MODEL.excREXSList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcREXS"));
            MODEL.excSCRXList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcSCRX"));
            MODEL.excSYMPTRList().forEach(e -> assertFalse(e.synchronousMachineId().isBlank(), "ExcSYMPTR"));
        }
    }
}
