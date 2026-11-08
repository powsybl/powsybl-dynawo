/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;

import com.powsybl.dynawo.cgmesdy.CgmesDyConstants;
import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.dynawo.cgmesdy.parser.CgmesDyModelLoader;
import com.powsybl.triplestore.api.TripleStore;
import com.powsybl.triplestore.api.TripleStoreFactory;
import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all 16 PSS types parsed from {@code exciters_pss_cim16.xml}.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@DisplayName("PssLoader – all 16 PSS types (CIM16)")
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
class PssLoaderTest {

    private static final String FIXTURE = "/com/powsybl/dynawo/cgmesdy/exciters_pss_cim16.xml";
    private static final double T = 1e-9;
    private static CgmesDyModel MODEL;

    @BeforeAll
    static void loadFixture() throws Exception {
        try (InputStream is = PssLoaderTest.class.getResourceAsStream(FIXTURE)) {
            assertNotNull(is, "Fixture not found: " + FIXTURE);
            TripleStore ts = TripleStoreFactory.create("rdf4j");
            ts.read(is, CgmesDyConstants.RDF_NS, "urn:test:pss");
            MODEL = new CgmesDyModelLoader(ts, CgmesDyConstants.CIM16_NS).load();
        }
    }

    private static <X> X one(java.util.List<X> list, String name) {
        assertEquals(1, list.size(), name + " count");
        return list.get(0);
    }

    // =========================================================================
    // PSS types
    // =========================================================================

    @Nested @DisplayName("Pss1") class P1 {
        @Test void count() {
            assertEquals(1, MODEL.pss1List().size()); }

        @Test void fields() {
            Pss1 p = one(MODEL.pss1List(), "Pss1");
            assertTrue(p.id().contains("pss1"));
            assertFalse(p.excitationSystemId().isBlank());
            assertEquals(1.0, p.kx(), T);
            assertEquals(0.3, p.t1(), T);
            assertEquals(0.06, p.t2(), T);
            assertEquals(1.0, p.t5(), T);
            assertEquals(0.05, p.vsmax(), T);
            assertEquals(-0.05, p.vsmin(), T);
        }

        @Test void vsConstraint() {
            Pss1 p = MODEL.pss1List().get(0); assertTrue(p.vsmax() >= p.vsmin()); }
    }

    @Nested @DisplayName("Pss1A – String inputSignalType") class P1A {
        @Test void count() {
            assertEquals(1, MODEL.pss1AList().size()); }

        @Test void fields() {
            Pss1A p = one(MODEL.pss1AList(), "Pss1A");
            assertFalse(p.inputSignalType().isBlank(), "inputSignalType must not be blank");
            assertEquals(5.0, p.ks(), T);
            assertEquals(0.04, p.t1(), T);
            assertEquals(0.1, p.vrmax(), T);
            assertEquals(-0.1, p.vrmin(), T);
        }
    }

    @Nested @DisplayName("Pss2B – dual String signal types") class P2B {
        @Test void count() {
            assertEquals(1, MODEL.pss2BList().size()); }

        @Test void fields() {
            Pss2B p = one(MODEL.pss2BList(), "Pss2B");
            assertFalse(p.inputSignal1Type().isBlank());
            assertFalse(p.inputSignal2Type().isBlank());
            assertEquals(1.0, p.ks1(), T);
            assertEquals(0.1, p.ks2(), T);
            assertEquals(10.0, p.tw1(), T);
            assertEquals(0.12, p.t1(), T);
            assertEquals(2.0, p.t7(), T);
            assertEquals(0.1, p.vsmax(), T);
            assertEquals(1.0, p.n(), T);
            assertEquals(5.0, p.m(), T);
        }
    }

    @Nested @DisplayName("Pss2ST") class P2ST {
        @Test void count() {
            assertEquals(1, MODEL.pss2STList().size()); }

        @Test void fields() {
            Pss2ST p = one(MODEL.pss2STList(), "Pss2ST");
            assertFalse(p.inputSignal1Type().isBlank());
            assertEquals(1.0, p.k1(), T);
            assertEquals(0.12, p.t1(), T);
            assertEquals(0.1, p.lsmax(), T);
        }
    }

    @Nested @DisplayName("Pss5") class P5 {
        @Test void count() {
            assertEquals(1, MODEL.pss5List().size()); }

        @Test void fields() {
            Pss5 p = one(MODEL.pss5List(), "Pss5");
            assertEquals(10.0, p.kf(), T);
            assertEquals(5.0, p.kpe(), T);
            assertEquals(0.05, p.pmin(), T);
            assertEquals(0.1, p.vsmx(), T);
        }
    }

    @Nested @DisplayName("PssELIN2") class ELIN2 {
        @Test void count() {
            assertEquals(1, MODEL.pssELIN2List().size()); }

        @Test void fields() {
            PssELIN2 p = one(MODEL.pssELIN2List(), "PssELIN2");
            assertEquals(0.1, p.apss(), T);
            assertEquals(1.0, p.ks1(), T);
            assertEquals(0.5, p.ts1(), T);
            assertEquals(0.1, p.psslim(), T);
        }
    }

    @Nested @DisplayName("PssIEEE1A") class IEEE1A {
        @Test void count() {
            assertEquals(1, MODEL.pssIEEE1AList().size()); }

        @Test void fields() {
            PssIEEE1A p = one(MODEL.pssIEEE1AList(), "PssIEEE1A");
            assertEquals(20.0, p.ks(), T);
            assertEquals(0.05, p.t1(), T);
            assertEquals(10.0, p.t5(), T);
            assertEquals(0.2, p.vsmax(), T);
        }
    }

    @Nested @DisplayName("PssIEEE2B") class IEEE2B {
        @Test void count() {
            assertEquals(1, MODEL.pssIEEE2BList().size()); }

        @Test void fields() {
            PssIEEE2B p = one(MODEL.pssIEEE2BList(), "PssIEEE2B");
            assertFalse(p.inputSignal1Type().isBlank());
            assertFalse(p.inputSignal2Type().isBlank());
            assertEquals(1.0, p.ks1(), T);
            assertEquals(0.2, p.vstmax(), T);
            assertEquals(1.0, p.n(), T);
            assertEquals(5.0, p.m(), T);
        }
    }

    @Nested @DisplayName("PssIEEE3B") class IEEE3B {
        @Test void count() {
            assertEquals(1, MODEL.pssIEEE3BList().size()); }

        @Test void fields() {
            PssIEEE3B p = one(MODEL.pssIEEE3BList(), "PssIEEE3B");
            assertFalse(p.inputSignal1Type().isBlank());
            assertEquals(2.0, p.ks1(), T);
            assertEquals(0.5, p.t1(), T);
            assertEquals(0.1, p.vsmax(), T);
        }
    }

    @Nested @DisplayName("PssIEEE4B – 4-band structure") class IEEE4B {
        @Test void count() {
            assertEquals(1, MODEL.pssIEEE4BList().size()); }

        @Test void fields() {
            PssIEEE4B p = one(MODEL.pssIEEE4BList(), "PssIEEE4B");
            assertEquals(1.0, p.kh(), T);
            assertEquals(1.0, p.ki(), T);
            assertEquals(1.0, p.kl(), T);
            assertEquals(1.0, p.bwh1(), T);
            assertEquals(0.1, p.bwl1(), T);
            assertEquals(0.5, p.th1(), T);
            assertEquals(0.1, p.vsmax(), T);
            assertEquals(-0.1, p.vsmin(), T);
            assertEquals(0.1, p.vshmax(), T);
            assertEquals(0.1, p.vslmax(), T);
        }

        @Test void bandLimitConstraints() {
            PssIEEE4B p = MODEL.pssIEEE4BList().get(0);
            assertTrue(p.vsmax() >= p.vsmin());
            assertTrue(p.vshmax() >= p.vshmin());
            assertTrue(p.vslmax() >= p.vslmin());
        }
    }

    @Nested
    @DisplayName("PssPTIST1")
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    class PTIST1 {
        @Test void count() {
            assertEquals(1, MODEL.pssPTIST1List().size()); }

        @Test void fields() {
            PssPTIST1 p = one(MODEL.pssPTIST1List(), "PssPTIST1");
            assertEquals(9.0, p.k(), T);
            assertEquals(5.0, p.m(), T);
            assertEquals(0.06, p.dtc(), T);
            assertEquals(0.1, p.vsmx(), T);
        }
    }

    @Nested
    @DisplayName("PssPTIST3")
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    class PTIST3 {
        @Test void count() {
            assertEquals(1, MODEL.pssPTIST3List().size());
        }

        @Test void fields() {
            PssPTIST3 p = one(MODEL.pssPTIST3List(), "PssPTIST3");
            assertEquals(9.0, p.k(), T);
            assertEquals(5.0, p.m(), T);
            assertEquals(1.0, p.isfreq(), T);
            assertEquals(4.0, p.nav(), T);
            assertEquals(2.0, p.ncl(), T);
            assertEquals(0.05, p.pmin(), T);
        }
    }

    @Nested @DisplayName("PssRQB") class RQB {
        @Test void count() {
            assertEquals(1, MODEL.pssRQBList().size()); }

        @Test void fields() {
            PssRQB p = one(MODEL.pssRQBList(), "PssRQB");
            assertEquals(0.0, p.kdpm(), T);
            assertEquals(0.0, p.sibv(), T);
            assertEquals(0.0, p.t4mom(), T);
        }
    }

    @Nested @DisplayName("PssSB4") class SB4 {
        @Test void count() {
            assertEquals(1, MODEL.pssSB4List().size()); }

        @Test void fields() {
            PssSB4 p = one(MODEL.pssSB4List(), "PssSB4");
            assertEquals(1.0, p.kx(), T);
            assertEquals(0.5, p.tx1(), T);
            assertEquals(0.1, p.tx2(), T);
            assertEquals(0.1, p.vsmax(), T);
            assertEquals(-0.1, p.vsmin(), T);
        }
    }

    @Nested @DisplayName("PssSH") class SH {
        @Test void count() {
            assertEquals(1, MODEL.pssSHList().size()); }

        @Test void fields() {
            PssSH p = one(MODEL.pssSHList(), "PssSH");
            assertEquals(1.0, p.k(), T);
            assertEquals(0.5, p.t1(), T);
            assertEquals(0.1, p.vsmax(), T);
        }
    }

    @Nested @DisplayName("PssWECC – dual String signal types") class WECC {
        @Test void count() {
            assertEquals(1, MODEL.pssWECCList().size()); }

        @Test void fields() {
            PssWECC p = one(MODEL.pssWECCList(), "PssWECC");
            assertFalse(p.inputSignal1Type().isBlank());
            assertFalse(p.inputSignal2Type().isBlank());
            assertEquals(1.0, p.k1(), T);
            assertEquals(0.1, p.k2(), T);
            assertEquals(0.5, p.t1(), T);
            assertEquals(0.1, p.vsmax(), T);
        }
    }

    // =========================================================================
    // Cross-cutting
    // =========================================================================

    @Nested @DisplayName("Cross-cutting")
    class CrossCutting {

        @Test void totalPssCount() {
            int total =
                MODEL.pss1List().size() + MODEL.pss1AList().size() +
                MODEL.pss2BList().size() + MODEL.pss2STList().size() +
                MODEL.pss5List().size() + MODEL.pssELIN2List().size() +
                MODEL.pssIEEE1AList().size() + MODEL.pssIEEE2BList().size() +
                MODEL.pssIEEE3BList().size() + MODEL.pssIEEE4BList().size() +
                MODEL.pssPTIST1List().size() + MODEL.pssPTIST3List().size() +
                MODEL.pssRQBList().size() + MODEL.pssSB4List().size() +
                MODEL.pssSHList().size() + MODEL.pssWECCList().size();
            assertEquals(16, total, "Expected exactly 16 PSS instances");
        }

        @Test void allExcitationSystemIdsNonBlank() {
            MODEL.pss1List().forEach(p -> assertFalse(p.excitationSystemId().isBlank()));
            MODEL.pssIEEE4BList().forEach(p -> assertFalse(p.excitationSystemId().isBlank()));
            MODEL.pssSB4List().forEach(p -> assertFalse(p.excitationSystemId().isBlank()));
        }

        @Test void vsMaxGteVsMin() {
            MODEL.pss1List().forEach(p -> assertTrue(p.vsmax() >= p.vsmin()));
            MODEL.pss2BList().forEach(p -> assertTrue(p.vsmax() >= p.vsmin()));
            MODEL.pssIEEE2BList().forEach(p -> assertTrue(p.vstmax() >= p.vstmin()));
            MODEL.pssSB4List().forEach(p -> assertTrue(p.vsmax() >= p.vsmin()));
            MODEL.pssSHList().forEach(p -> assertTrue(p.vsmax() >= p.vsmin()));
        }
    }
}
