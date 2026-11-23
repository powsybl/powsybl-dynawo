/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.misc;

import com.powsybl.dynawo.cgmesdy.CgmesDyConstants;
import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.dynawo.cgmesdy.hvdc.*;
import com.powsybl.dynawo.cgmesdy.load.*;
import com.powsybl.dynawo.cgmesdy.parser.CgmesDyModelLoader;
import com.powsybl.dynawo.cgmesdy.protection.*;
// import com.powsybl.dynawo.cgmesdy.userdef.UserDefinedModel;
import com.powsybl.dynawo.cgmesdy.protection.VCompIEEEType1;
import com.powsybl.triplestore.api.TripleStore;
import com.powsybl.triplestore.api.TripleStoreFactory;
import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for:
 * <ul>
 *   <li>Load models (6): LoadStatic, LoadComposite, LoadMotor, LoadAggregate,
 *       LoadGenericNonLinear, MechLoad1</li>
 *   <li>HVDC converters (2): CsConverterDynamics, VsConverterDynamics</li>
 *   <li>Protection / limiters (11): DiscExcContIEEEDEC1A/2A/3A, OverexcLimIEEE/X,
 *       UnderexcLimIEEE1/2, UnderexcLimX1/2, VoltageAdjusterIEEE, VoltageCompensatorIEEE</li>
 *   <li>UserDefinedModel (1 sample)</li>
 * </ul>
 * All loaded from {@code misc_cim16.xml}.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@DisplayName("Misc elements – load, HVDC, protection, user-defined (CIM16)")
class MiscLoaderTest {

    private static final String FIXTURE = "/com/powsybl/dynawo/cgmesdy/misc_cim16.xml";
    private static final double T = 1e-9;
    private static CgmesDyModel MODEL;

    @BeforeAll
    static void loadFixture() throws Exception {
        try (InputStream is = MiscLoaderTest.class.getResourceAsStream(FIXTURE)) {
            assertNotNull(is, "Fixture not found: " + FIXTURE);
            TripleStore ts = TripleStoreFactory.create("rdf4j");
            ts.read(is, CgmesDyConstants.RDF_NS, "urn:test:misc");
            MODEL = new CgmesDyModelLoader(ts, CgmesDyConstants.CIM16_NS).load();
        }
    }

    private static <X> X one(java.util.List<X> list, String name) {
        assertEquals(1, list.size(), name + " count");
        return list.get(0);
    }

    // =========================================================================
    // LOAD MODELS
    // =========================================================================

    @Nested @DisplayName("LoadStatic – ZIP polynomial load")
    class LoadStaticTest {
        @Test void count() {
            assertEquals(1, MODEL.loadStaticList().size()); }

        @Test void fields() {
            LoadStatic ls = one(MODEL.loadStaticList(), "LoadStatic");
            assertFalse(ls.id().isBlank());
            assertFalse(ls.energyConsumerId().isBlank());
            assertEquals(1.0, ls.kp4(), T);
            assertEquals(1.0, ls.kq4(), T);
            assertEquals(1.0, ls.ep1(), T);
            assertEquals(2.0, ls.ep2(), T);
            assertEquals(3.0, ls.ep3(), T);
            assertEquals(1.0, ls.eq1(), T);
            assertFalse(ls.staticLoadModelType().isBlank());
        }

        @Test void polynomialCoeffsNonNegative() {
            LoadStatic ls = MODEL.loadStaticList().get(0);
            // kp fractions must all be >= 0
            assertTrue(ls.kp1() >= 0); assertTrue(ls.kp4() >= 0);
            assertTrue(ls.kq1() >= 0); assertTrue(ls.kq4() >= 0);
        }
    }

    @Nested @DisplayName("LoadComposite – induction motor composite load")
    class LoadCompositeTest {
        @Test void count() {
            assertEquals(1, MODEL.loadCompositeList().size()); }

        @Test void fields() {
            LoadComposite lc = one(MODEL.loadCompositeList(), "LoadComposite");
            assertFalse(lc.energyConsumerId().isBlank());
            assertEquals(1.0, lc.epvs(), T);
            assertEquals(1.5, lc.epfs(), T);
            assertEquals(0.5, lc.lfrac(), T);
            assertEquals(0.5, lc.pfrac(), T);
            assertEquals(0.05, lc.td(), T);
            assertEquals(3.0, lc.xm(), T);
            assertEquals(0.2, lc.xp(), T);
            assertEquals(0.12, lc.xpp(), T);
            assertEquals(1.5, lc.tpo(), T);
            assertEquals(0.02, lc.tppo(), T);
        }

        @Test void fractionConstraints() {
            LoadComposite lc = MODEL.loadCompositeList().get(0);
            assertTrue(lc.xm() >= lc.xp(), "xm >= xp");
            assertTrue(lc.xp() >= lc.xpp(), "xp >= xpp");
            assertTrue(lc.tpo() >= lc.tppo(), "tpo >= tppo");
        }
    }

    @Nested @DisplayName("LoadMotor – standalone motor model")
    class LoadMotorTest {
        @Test void count() {
            assertEquals(1, MODEL.loadMotorList().size()); }

        @Test void fields() {
            LoadMotor lm = one(MODEL.loadMotorList(), "LoadMotor");
            assertEquals(0.5, lm.pfrac(), T);
            assertEquals(0.01, lm.ra(), T);
            assertEquals(1.0, lm.tpo(), T);
            assertEquals(0.02, lm.tppo(), T);
            assertEquals(0.5, lm.h(), T);
            assertEquals(2.0, lm.d(), T);
            assertEquals(0.9, lm.vt(), T);
            assertEquals(0.86, lm.vbrkr(), T);
            assertEquals(0.8, lm.compPF(), T);
        }

        @Test void voltageSwitchpointsOrdered() {
            LoadMotor lm = MODEL.loadMotorList().get(0);
            // vc1off > vc2off (stall/trip ordering)
            assertTrue(lm.vc1off() > lm.vc2off(), "vc1off > vc2off");
            assertTrue(lm.vc1on() > lm.vc2on(), "vc1on  > vc2on");
        }
    }

    @Nested @DisplayName("LoadAggregate – references motor and static IDs")
    class LoadAggregateTest {
        @Test void count() {
            assertEquals(1, MODEL.loadAggregateList().size()); }

        @Test void fields() {
            LoadAggregate la = one(MODEL.loadAggregateList(), "LoadAggregate");
            assertFalse(la.energyConsumerId().isBlank());
            assertFalse(la.loadMotorId().isBlank());
            assertFalse(la.loadStaticId().isBlank());
        }
    }

    @Nested @DisplayName("LoadGenericNonLinear – exponential recovery model")
    class LoadGenericNLTest {
        @Test void count() {
            assertEquals(1, MODEL.loadGenericNLList().size()); }

        @Test void fields() {
            LoadGenericNonLinear lg = one(MODEL.loadGenericNLList(), "LoadGenericNonLinear");
            assertFalse(lg.energyConsumerId().isBlank());
            assertFalse(lg.genericNonLinearLoadModelType().isBlank());
            assertEquals(0.5, lg.bt(), T);
            assertEquals(1.0, lg.lt(), T);
            assertEquals(1.6, lg.pt(), T);
            assertEquals(2.0, lg.qt(), T);
            assertEquals(0.5, lg.tp(), T);
            assertEquals(0.5, lg.tq(), T);
        }
    }

    @Nested @DisplayName("MechLoad1 – mechanical load for async machine")
    class MechLoad1Test {
        @Test void count() {
            assertEquals(1, MODEL.mechLoad1List().size()); }

        @Test void fields() {
            MechLoad1 m = one(MODEL.mechLoad1List(), "MechLoad1");
            assertFalse(m.asynchronousMachineId().isBlank());
            assertEquals(1.0, m.a(), T);
            assertEquals(0.0, m.b(), T);
            assertEquals(0.0, m.d(), T);
            assertEquals(0.0, m.e(), T);
        }
    }

    // =========================================================================
    // HVDC CONVERTERS
    // =========================================================================

    @Nested @DisplayName("CsConverterDynamics – LCC converter angles")
    class CsConverterTest {
        @Test void count() {
            assertEquals(1, MODEL.csConverterList().size()); }

        @Test void fields() {
            CsConverterDynamics c = one(MODEL.csConverterList(), "CsConverter");
            assertFalse(c.id().isBlank());
            assertEquals(15.0, c.alpha(), T);
            assertEquals(18.0, c.gamma(), T);
            assertEquals(30.0, c.maxAlpha(), T);
            assertEquals(5.0, c.minAlpha(), T);
            assertEquals(30.0, c.maxGamma(), T);
            assertEquals(5.0, c.minGamma(), T);
        }

        @Test void angleConstraints() {
            CsConverterDynamics c = MODEL.csConverterList().get(0);
            assertTrue(c.maxAlpha() > c.minAlpha(), "maxAlpha > minAlpha");
            assertTrue(c.maxGamma() > c.minGamma(), "maxGamma > minGamma");
            assertTrue(c.alpha() >= c.minAlpha() && c.alpha() <= c.maxAlpha(), "alpha within range");
            assertTrue(c.gamma() >= c.minGamma() && c.gamma() <= c.maxGamma(), "gamma within range");
        }
    }

    @Nested @DisplayName("VsConverterDynamics – VSC/HVDC modulation")
    class VsConverterTest {
        @Test void count() {
            assertEquals(1, MODEL.vsConverterList().size()); }

        @Test void fields() {
            VsConverterDynamics v = one(MODEL.vsConverterList(), "VsConverter");
            assertFalse(v.id().isBlank());
            assertEquals(0.02, v.droop(), T);
            assertEquals(0.0, v.droopCompensation(), T);
            assertEquals(1.0, v.pPccControl(), T);
            assertEquals(0.9, v.maxModulationIndex(), T);
            assertEquals(1.1, v.maxValveCurrent(), T);
        }

        @Test void modulationIndexBounded() {
            assertTrue(MODEL.vsConverterList().get(0).maxModulationIndex() <= 1.0,
                "Modulation index must not exceed 1");
        }
    }

    // =========================================================================
    // PROTECTION / LIMITERS
    // =========================================================================

    @Nested @DisplayName("DiscExcContIEEEDEC1A – discontinuous excitation control")
    class DEC1A {
        @Test void count() {
            assertEquals(1, MODEL.discExcDEC1AList().size()); }

        @Test void fields() {
            DiscExcContIEEEDEC1A d = one(MODEL.discExcDEC1AList(), "DEC1A");
            assertFalse(d.excitationSystemId().isBlank());
            assertEquals(1.0, d.vtlmt(), T);
            assertEquals(0.3, d.vomax(), T);
            assertEquals(-0.3, d.vomin(), T);
            assertEquals(0.9, d.vdis(), T);
            assertEquals(0.4, d.vanmax(), T);
            assertEquals(0.95, d.vtm(), T);
            assertEquals(1.05, d.vtn(), T);
            assertEquals(0.1, d.vsmax(), T);
        }

        @Test void voConstraint() {
            DiscExcContIEEEDEC1A d = MODEL.discExcDEC1AList().get(0);
            assertTrue(d.vomax() >= d.vomin());
        }
    }

    @Nested @DisplayName("DiscExcContIEEEDEC2A")
    class DEC2A {
        @Test void count() {
            assertEquals(1, MODEL.discExcDEC2AList().size()); }

        @Test void fields() {
            DiscExcContIEEEDEC2A d = one(MODEL.discExcDEC2AList(), "DEC2A");
            assertEquals(0.03, d.td1(), T);
            assertEquals(0.3, d.td2(), T);
            assertEquals(0.1, d.vdmax(), T);
            assertEquals(0.0, d.vdmin(), T);
        }

        @Test void vdConstraint() {
            assertTrue(MODEL.discExcDEC2AList().get(0).vdmax() >= MODEL.discExcDEC2AList().get(0).vdmin()); }
    }

    @Nested @DisplayName("DiscExcContIEEEDEC3A")
    class DEC3A {
        @Test void count() {
            assertEquals(1, MODEL.discExcDEC3AList().size()); }

        @Test void fields() {
            DiscExcContIEEEDEC3A d = one(MODEL.discExcDEC3AList(), "DEC3A");
            assertEquals(0.1, d.tdr(), T);
            assertEquals(0.9, d.vtmin(), T);
        }
    }

    @Nested @DisplayName("OverexcLimIEEE")
    class OelIEEE {
        @Test void count() {
            assertEquals(1, MODEL.oelIEEEList().size()); }

        @Test void fields() {
            OverexcLimIEEE o = one(MODEL.oelIEEEList(), "OverexcLimIEEE");
            assertFalse(o.excitationSystemId().isBlank());
            assertEquals(1.5, o.ifdmax(), T);
            assertEquals(1.2, o.ifdlim(), T);
            assertEquals(1.05, o.itfpu(), T);
            assertEquals(0.03, o.hyst(), T);
            assertEquals(1.0, o.kcd(), T);
            assertEquals(0.1, o.kramp(), T);
        }

        @Test void ifdConstraint() {
            OverexcLimIEEE o = MODEL.oelIEEEList().get(0);
            assertTrue(o.ifdmax() >= o.ifdlim(), "ifdmax >= ifdlim");
            assertTrue(o.ifdlim() >= o.itfpu(), "ifdlim >= itfpu pickup");
        }
    }

    @Nested @DisplayName("OverexcLimX")
    class OelX {
        @Test void count() {
            assertEquals(1, MODEL.oelXList().size()); }

        @Test void fields() {
            OverexcLimX o = one(MODEL.oelXList(), "OverexcLimX");
            assertEquals(3.0, o.efd1(), T);
            assertEquals(2.5, o.efd2(), T);
            assertEquals(2.0, o.efd3(), T);
            assertEquals(1.1, o.efddes(), T);
            assertEquals(1.0, o.efdrated(), T);
            assertEquals(0.5, o.kmx(), T);
            assertEquals(0.95, o.vlow(), T);
        }

        @Test void efdDecreasing() {
            OverexcLimX o = MODEL.oelXList().get(0);
            assertTrue(o.efd1() > o.efd2(), "efd1 > efd2");
            assertTrue(o.efd2() > o.efd3(), "efd2 > efd3");
        }
    }

    @Nested @DisplayName("UnderexcLimIEEE1")
    class UelIEEE1 {
        @Test void count() {
            assertEquals(1, MODEL.uelIEEE1List().size()); }

        @Test void fields() {
            UnderexcLimIEEE1 u = one(MODEL.uelIEEE1List(), "UnderexcLimIEEE1");
            assertFalse(u.excitationSystemId().isBlank());
            assertEquals(1.0, u.kur(), T);
            assertEquals(5.8, u.vurmax(), T);
            assertEquals(0.35, u.vuimax(), T);
            assertEquals(-0.1, u.vuimin(), T);
        }

        @Test void vuiConstraint() {
            assertTrue(MODEL.uelIEEE1List().get(0).vuimax() >= MODEL.uelIEEE1List().get(0).vuimin()); }
    }

    @Nested @DisplayName("UnderexcLimIEEE2")
    class UelIEEE2 {
        @Test void count() {
            assertEquals(1, MODEL.uelIEEE2List().size()); }

        @Test void fields() {
            UnderexcLimIEEE2 u = one(MODEL.uelIEEE2List(), "UnderexcLimIEEE2");
            assertEquals(3.3, u.kuf(), T);
            assertEquals(100.0, u.kul(), T);
            assertEquals(-0.31, u.q0(), T);
            assertEquals(0.0, u.vuimax(), T);
            assertEquals(-5.8, u.vuimin(), T);
        }
    }

    @Nested @DisplayName("UnderexcLimX1")
    class UelX1 {
        @Test void count() {
            assertEquals(1, MODEL.uelX1List().size()); }

        @Test void fields() {
            UnderexcLimX1 u = one(MODEL.uelX1List(), "UnderexcLimX1");
            assertEquals(10.0, u.k(), T);
            assertEquals(0.05, u.melmax(), T);
            assertEquals(5.0, u.tm(), T);
        }
    }

    @Nested @DisplayName("UnderexcLimX2")
    class UelX2 {
        @Test void count() {
            assertEquals(1, MODEL.uelX2List().size()); }

        @Test void fields() {
            UnderexcLimX2 u = one(MODEL.uelX2List(), "UnderexcLimX2");
            assertEquals(3.3, u.kuf(), T);
            assertEquals(100.0, u.kul(), T);
            assertEquals(-0.31, u.q0(), T);
            assertEquals(-5.8, u.vuimin(), T);
        }
    }

    @Nested @DisplayName("VoltageAdjusterIEEE")
    class VoltAdj {
        @Test void count() {
            assertEquals(1, MODEL.voltageAdjList().size()); }

        @Test void fields() {
            VoltageAdjusterIEEE v = one(MODEL.voltageAdjList(), "VoltageAdjusterIEEE");
            assertFalse(v.excitationSystemId().isBlank());
            assertEquals(1.0, v.ka(), T);
            assertEquals(1.2, v.tamax(), T);
            assertEquals(0.8, v.tamin(), T);
            assertEquals(0.05, v.vimax(), T);
            assertEquals(0.5, v.smax(), T);
        }

        @Test void timeConstantOrdered() {
            VoltageAdjusterIEEE v = MODEL.voltageAdjList().get(0);
            assertTrue(v.tamax() > v.tamin());
        }
    }

    @Nested @DisplayName("VoltageCompensatorIEEE")
    class VoltComp {
        @Test void count() {
            assertEquals(1, MODEL.voltageCompList().size()); }

        @Test void fields() {
            VoltageCompensatorIEEE v = one(MODEL.voltageCompList(), "VoltageCompensatorIEEE");
            assertFalse(v.excitationSystemId().isBlank());
            assertEquals(0.0, v.tr(), T);
            assertEquals(0.0, v.rc(), T);
            assertEquals(0.0, v.xc(), T);
        }
    }

    @Nested @DisplayName("VCompIEEEType1")
    class VCompType1 {
        @Test void count() {
            assertEquals(1, MODEL.vCompIEEEType1List().size());
        }

        @Test void fields() {
            VCompIEEEType1 v = one(MODEL.vCompIEEEType1List(), "VCompIEEEType1");
            assertEquals("exc-misc-1", v.excitationSystemId());
            assertEquals(0.01, v.tr(), T);
            assertEquals(0.02, v.rc(), T);
            assertEquals(0.05, v.xc(), T);
        }
    }

    // =========================================================================
    // USER-DEFINED
    // =========================================================================

//    @Nested @DisplayName("UserDefinedModel – proprietary governor")
//    class UserDefined {
//        @Test void count() {
//            assertFalse(MODEL.userDefinedList().isEmpty(), "At least one UserDefined instance"); }
//
//        @Test void fields() {
//            UserDefinedModel u = MODEL.userDefinedList().get(0);
//            assertFalse(u.id().isBlank(), "id must not be blank");
//            assertFalse(u.cimClassName().isBlank(), "cimClassName must not be blank");
//            // proprietary may be true or false – just assert it parses without throwing
//        }
//
//        @Test void governorUd() {
//            boolean found = MODEL.userDefinedList().stream()
//                .anyMatch(u -> u.cimClassName().contains("TurbineGovernor") ||
//                               u.cimClassName().contains("Gov"));
//            assertTrue(found, "Expected a TurbineGovernorUserDefined entry");
//        }
//    }

    // =========================================================================
    // Cross-cutting
    // =========================================================================

    @Nested @DisplayName("Cross-cutting totals")
    class CrossCutting {

        @Test void loadModelCount() {
            int total =
                MODEL.loadStaticList().size() + MODEL.loadCompositeList().size() +
                MODEL.loadMotorList().size() + MODEL.loadAggregateList().size() +
                MODEL.loadGenericNLList().size() + MODEL.mechLoad1List().size();
            assertEquals(6, total, "Expected 6 load model instances");
        }

        @Test void hvdcCount() {
            assertEquals(1, MODEL.csConverterList().size());
            assertEquals(1, MODEL.vsConverterList().size());
        }

        @Test void protectionCount() {
            int total =
                MODEL.discExcDEC1AList().size() + MODEL.discExcDEC2AList().size() +
                MODEL.discExcDEC3AList().size() +
                MODEL.oelIEEEList().size() + MODEL.oelXList().size() +
                MODEL.uelIEEE1List().size() + MODEL.uelIEEE2List().size() +
                MODEL.uelX1List().size() + MODEL.uelX2List().size() +
                MODEL.voltageAdjList().size() + MODEL.voltageCompList().size() + MODEL.vCompIEEEType1List().size();
            assertEquals(12, total, "Expected 12 protection/limiter instances");
        }

        @Test void allProtectionHaveExcitationSystemId() {
            MODEL.discExcDEC1AList().forEach(d -> assertFalse(d.excitationSystemId().isBlank()));
            MODEL.discExcDEC2AList().forEach(d -> assertFalse(d.excitationSystemId().isBlank()));
            MODEL.oelIEEEList().forEach(o -> assertFalse(o.excitationSystemId().isBlank()));
            MODEL.uelIEEE1List().forEach(u -> assertFalse(u.excitationSystemId().isBlank()));
            MODEL.voltageAdjList().forEach(v -> assertFalse(v.excitationSystemId().isBlank()));
            MODEL.vCompIEEEType1List().forEach(v -> assertFalse(v.excitationSystemId().isBlank()));
        }
    }
}
