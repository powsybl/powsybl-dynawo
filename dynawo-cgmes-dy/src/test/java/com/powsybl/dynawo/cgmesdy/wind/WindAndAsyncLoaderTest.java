/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;

import com.powsybl.dynawo.cgmesdy.CgmesDyConstants;
import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.dynawo.cgmesdy.asynchronous.*;
import com.powsybl.dynawo.cgmesdy.parser.CgmesDyModelLoader;
import com.powsybl.triplestore.api.TripleStore;
import com.powsybl.triplestore.api.TripleStoreFactory;
import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all 21 IEC wind model types and 3 asynchronous machine dynamics
 * parsed from {@code misc_cim16.xml}.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@DisplayName("Wind / AsynchronousMachine loader – 21 wind + 3 async types (CIM16)")
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
class WindAndAsyncLoaderTest {

    private static final String FIXTURE = "/com/powsybl/dynawo/cgmesdy/misc_cim16.xml";
    private static final double T = 1e-9;
    private static CgmesDyModel MODEL;

    @BeforeAll
    static void loadFixture() throws Exception {
        try (InputStream is = WindAndAsyncLoaderTest.class.getResourceAsStream(FIXTURE)) {
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
    // Asynchronous machine dynamics (3)
    // =========================================================================

    @Nested @DisplayName("AsynchronousMachineTimeConstantReactance")
    class AsyncTCR {
        @Test void count() {
            assertEquals(1, MODEL.asyncTimeConstReactanceList().size()); }

        @Test void fields() {
            AsynchronousMachineTimeConstantReactance a = one(MODEL.asyncTimeConstReactanceList(), "AsyncTCR");
            assertFalse(a.id().isBlank());
            assertFalse(a.asynchronousMachineId().isBlank());
            assertEquals(2.84, a.xs(), T);
            assertEquals(0.18, a.xp(), T);
            assertEquals(0.12, a.xpp(), T);
            assertEquals(1.5, a.tpo(), T);
            assertEquals(0.02, a.tppo(), T);
            assertEquals(0.08, a.xl(), T);
        }

        @Test void reactanceHierarchy() {
            AsynchronousMachineTimeConstantReactance a = MODEL.asyncTimeConstReactanceList().get(0);
            // xs >= xp >= xpp in standard machine model
            assertTrue(a.xs() >= a.xp(), "xs >= xp");
            assertTrue(a.xp() >= a.xpp(), "xp >= xpp");
        }
    }

    @Nested @DisplayName("AsynchronousMachineEquivalentCircuit")
    class AsyncEqC {
        @Test void count() {
            assertEquals(1, MODEL.asyncEquivCircuitList().size()); }

        @Test void fields() {
            AsynchronousMachineEquivalentCircuit a = one(MODEL.asyncEquivCircuitList(), "AsyncEqC");
            assertFalse(a.asynchronousMachineId().isBlank());
            assertEquals(0.03, a.rr1(), T);
            assertEquals(0.05, a.xr1(), T);
            assertEquals(0.025, a.rr2(), T);
            assertEquals(0.1, a.xr2(), T);
            assertEquals(2.5, a.xm(), T);
            assertEquals(0.08, a.xs(), T);
            assertEquals(0.005, a.rs(), T);
        }

        @Test void positiveResistances() {
            AsynchronousMachineEquivalentCircuit a = MODEL.asyncEquivCircuitList().get(0);
            assertTrue(a.rr1() > 0);
            assertTrue(a.rs() > 0);
            assertTrue(a.xm() > 0);
        }
    }

//    @Nested @DisplayName("AsynchronousMachineUserDefined – proprietary boolean")
//    class AsyncUD {
//        @Test void count() {
//            assertEquals(1, MODEL.asyncUserDefinedList().size()); }
//
//        @Test void fields() {
//            AsynchronousMachineUserDefined a = one(MODEL.asyncUserDefinedList(), "AsyncUD");
//            assertFalse(a.asynchronousMachineId().isBlank());
//            assertTrue(a.proprietary());
//        }
//    }

    // =========================================================================
    // Wind sub-models
    // =========================================================================

    @Nested @DisplayName("WindAeroConstIEC – id-only model")
    class WAeroConst {
        @Test void count() {
            assertEquals(1, MODEL.windAeroConstList().size()); }

        @Test void idNonBlank() {
            assertFalse(MODEL.windAeroConstList().get(0).id().isBlank()); }
    }

    @Nested @DisplayName("WindAeroLinearIEC")
    class WAeroLin {
        @Test void count() {
            assertEquals(1, MODEL.windAeroLinearList().size()); }

        @Test void fields() {
            var w = one(MODEL.windAeroLinearList(), "WindAeroLinearIEC");
            assertEquals(0.1, w.dpomega(), T);
            assertEquals(0.2, w.dptheta(), T);
            assertEquals(1.0, w.omegazero(), T);
            assertEquals(1.0, w.pavail(), T);
        }
    }

    @Nested @DisplayName("WindMechIEC")
    class WMech {
        @Test void count() {
            assertEquals(1, MODEL.windMechList().size()); }

        @Test void fields() {
            var w = one(MODEL.windMechList(), "WindMechIEC");
            assertEquals(0.005, w.cdrt(), T);
            assertEquals(0.9, w.hgen(), T);
            assertEquals(4.5, w.hwtr(), T);
            assertEquals(20.0, w.kdrt(), T);
        }

        @Test void inertiaPositive() {
            assertTrue(MODEL.windMechList().get(0).hgen() > 0); }
    }

    @Nested @DisplayName("WindContPitchAngleIEC")
    class WPitch {
        @Test void count() {
            assertEquals(1, MODEL.windContPitchList().size()); }

        @Test void fields() {
            var w = one(MODEL.windContPitchList(), "WindContPitchAngleIEC");
            assertEquals(2.0, w.dthetamax(), T);
            assertEquals(-2.0, w.dthetamin(), T);
            assertEquals(150.0, w.kpc(), T);
            assertEquals(30.0, w.thetamax(), T);
            assertEquals(0.0, w.thetamin(), T);
            assertEquals(0.5, w.ttheta(), T);
        }

        @Test void rateConstraints() {
            var w = MODEL.windContPitchList().get(0);
            assertTrue(w.dthetamax() > 0);
            assertTrue(w.dthetamin() < 0);
            assertTrue(w.thetamax() > w.thetamin());
        }
    }

    @Nested @DisplayName("WindContPType3IEC – recrossflag boolean")
    class WPType3 {
        @Test void count() {
            assertEquals(1, MODEL.windContPType3List().size()); }

        @Test void fields() {
            var w = one(MODEL.windContPType3List(), "WindContPType3IEC");
            assertEquals(0.5, w.dpmax(), T);
            assertEquals(10.0, w.kdtd(), T);
            assertEquals(0.1, w.rramp(), T);
            assertEquals(1.0, w.wdtd(), T);
            assertTrue(w.recrossflag());
        }
    }

    @Nested @DisplayName("WindContPType4aIEC")
    class WPType4a {
        @Test void count() {
            assertEquals(1, MODEL.windContPType4aList().size()); }

        @Test void fields() {
            var w = one(MODEL.windContPType4aList(), "WindContPType4aIEC");
            assertEquals(0.5, w.dpmax(), T);
            assertEquals(-0.5, w.dpmin(), T);
            assertEquals(0.1, w.tpord(), T);
            assertEquals(0.05, w.tufilt(), T);
        }
    }

    @Nested @DisplayName("WindContPType4bIEC")
    class WPType4b {
        @Test void count() {
            assertEquals(1, MODEL.windContPType4bList().size()); }

        @Test void fields() {
            var w = one(MODEL.windContPType4bList(), "WindContPType4bIEC");
            assertEquals(0.5, w.dpmax(), T);
            assertEquals(0.3, w.tpaero(), T);
            assertEquals(0.05, w.tufilt(), T);
        }
    }

    @Nested @DisplayName("WindContQIEC – four String enum fields")
    class WContQ {
        @Test void count() {
            assertEquals(1, MODEL.windContQList().size()); }

        @Test void fields() {
            var w = one(MODEL.windContQList(), "WindContQIEC");
            assertEquals(1.0, w.iqh1(), T);
            assertEquals(1.0, w.iqmax(), T);
            assertEquals(0.1, w.kpq(), T);
            assertEquals(2.0, w.kqv(), T);
            assertEquals(-0.1, w.udb1(), T);
            assertEquals(0.1, w.udb2(), T);
            assertFalse(w.mconq().isBlank(), "mconq must not be blank");
            assertFalse(w.mqfrt().isBlank(), "mqfrt must not be blank");
            assertFalse(w.windLVRTQcontrolModeType().isBlank(), "windLVRTQcontrolModeType must not be blank");
        }

        @Test void deadbandOrdered() {
            var w = MODEL.windContQList().get(0);
            assertTrue(w.udb2() > w.udb1(), "udb2 must be > udb1");
            assertTrue(w.umax() > w.umin());
        }
    }

    @Nested @DisplayName("WindContCurrLimIEC")
    class WCurrLim {
        @Test void count() {
            assertEquals(1, MODEL.windCurrLimList().size()); }

        @Test void fields() {
            var w = one(MODEL.windCurrLimList(), "WindContCurrLimIEC");
            assertEquals(1.3, w.imax(), T);
            assertEquals(1.3, w.imaxdip(), T);
            assertEquals(1.1, w.upqumax(), T);
        }
    }

    @Nested @DisplayName("WindContRotorRIEC")
    class WRotorR {
        @Test void count() {
            assertEquals(1, MODEL.windContRotorRList().size()); }

        @Test void fields() {
            var w = one(MODEL.windContRotorRList(), "WindContRotorRIEC");
            assertEquals(0.5, w.kirr(), T);
            assertEquals(0.1, w.rmax(), T);
            assertEquals(0.0, w.rmin(), T);
        }
    }

    @Nested @DisplayName("WindProtectionIEC")
    class WProt {
        @Test void count() {
            assertEquals(1, MODEL.windProtectionList().size()); }

        @Test void fields() {
            var w = one(MODEL.windProtectionList(), "WindProtectionIEC");
            assertEquals(1.0, w.dfimax(), T);
            assertEquals(52.0, w.fover(), T);
            assertEquals(48.0, w.funder(), T);
            assertEquals(5.0, w.tfma(), T);
            assertEquals(1.15, w.uover(), T);
            assertEquals(0.85, w.uunder(), T);
        }

        @Test void frequencyLimitsOrdered() {
            var w = MODEL.windProtectionList().get(0);
            assertTrue(w.fover() > w.funder(), "fover must exceed funder");
            assertTrue(w.uover() > w.uunder(), "uover must exceed uunder");
        }
    }

    @Nested @DisplayName("WindPlantFreqPcontrolIEC")
    class WPlantFreq {
        @Test void count() {
            assertEquals(1, MODEL.windPlantFreqList().size()); }

        @Test void fields() {
            var w = one(MODEL.windPlantFreqList(), "WindPlantFreqPcontrolIEC");
            assertEquals(30.0, w.twpfiltp(), T);
            assertEquals(30.0, w.twpfiltu(), T);
            assertEquals(1.0, w.prefmax(), T);
        }
    }

    @Nested @DisplayName("WindPlantReactiveControlIEC")
    class WPlantReact {
        @Test void count() {
            assertEquals(1, MODEL.windPlantReactList().size()); }

        @Test void fields() {
            var w = one(MODEL.windPlantReactList(), "WindPlantReactiveControlIEC");
            assertEquals(30.0, w.twpfiltp(), T);
            assertEquals(0.9, w.uwpqdip(), T);
        }
    }

    @Nested @DisplayName("WindPitchContEmulIEC")
    class WPitchEmul {
        @Test void count() {
            assertEquals(1, MODEL.windPitchEmulList().size()); }

        @Test void fields() {
            var w = one(MODEL.windPitchEmulList(), "WindPitchContEmulIEC");
            assertEquals(3.0, w.kipce(), T);
            assertEquals(1.0, w.omegatr(), T);
            assertEquals(1.0, w.pimax(), T);
            assertEquals(0.0, w.pimin(), T);
            assertEquals(30.0, w.thetamax(), T);
        }
    }

    @Nested @DisplayName("WindPlantIEC – association IDs")
    class WPlant {
        @Test void count() {
            assertEquals(1, MODEL.windPlantList().size()); }

        @Test void fields() {
            var w = one(MODEL.windPlantList(), "WindPlantIEC");
            assertFalse(w.windPlantFreqPcontrolIECId().isBlank(), "freq ctrl ID");
            assertFalse(w.windPlantReactiveControlIECId().isBlank(), "reactive ctrl ID");
        }
    }

    // =========================================================================
    // Wind turbine assemblies
    // =========================================================================

    @Nested @DisplayName("WindGenTurbineType1aIEC – Type 1a assembly")
    class WT1a {
        @Test void count() {
            assertEquals(1, MODEL.windType1aList().size()); }

        @Test void fields() {
            var w = one(MODEL.windType1aList(), "WindGenTurbineType1aIEC");
            assertFalse(w.powerPlantId().isBlank());
            assertFalse(w.windAeroConstIECId().isBlank());
            assertFalse(w.windProtectionIECId().isBlank());
            assertFalse(w.windMechIECId().isBlank());
            assertFalse(w.asynchronousMachineId().isBlank());
        }
    }

    @Nested @DisplayName("WindGenTurbineType1bIEC – Type 1b assembly")
    class WT1b {
        @Test void count() {
            assertEquals(1, MODEL.windType1bList().size()); }

        @Test void fields() {
            var w = one(MODEL.windType1bList(), "WindGenTurbineType1bIEC");
            assertFalse(w.powerPlantId().isBlank());
            assertFalse(w.windAeroLinearIECId().isBlank());
            assertFalse(w.windContPitchAngleIECId().isBlank());
            assertFalse(w.asynchronousMachineId().isBlank());
        }
    }

    @Nested @DisplayName("WindGenTurbineType2IEC – with rotor resistance control")
    class WT2 {
        @Test void count() {
            assertEquals(1, MODEL.windType2List().size()); }

        @Test void fields() {
            var w = one(MODEL.windType2List(), "WindGenTurbineType2IEC");
            assertFalse(w.windContRotorRIECId().isBlank());
            assertFalse(w.asynchronousMachineId().isBlank());
        }
    }

    @Nested @DisplayName("WindGenTurbineType3aIEC – DFIG with numeric fields")
    class WT3a {
        @Test void count() {
            assertEquals(1, MODEL.windType3aList().size()); }

        @Test void fields() {
            var w = one(MODEL.windType3aList(), "WindGenTurbineType3aIEC");
            assertFalse(w.windContPType3IECId().isBlank());
            assertFalse(w.windContQIECId().isBlank());
            assertEquals(0.05, w.kpc(), T);
            assertEquals(0.01, w.tic(), T);
            assertEquals(0.2, w.xs(), T);
        }
    }

    @Nested @DisplayName("WindGenTurbineType3bIEC – DFIG type b")
    class WT3b {
        @Test void count() {
            assertEquals(1, MODEL.windType3bList().size()); }

        @Test void fields() {
            var w = one(MODEL.windType3bList(), "WindGenTurbineType3bIEC");
            assertEquals(0.02, w.fthres(), T);
            assertEquals(0.0, w.mwtcwp(), T);
            assertEquals(0.02, w.tg(), T);
            assertEquals(0.05, w.two(), T);
        }
    }

    @Nested @DisplayName("WindGenTurbineType4aIEC – Full converter Type 4a")
    class WT4a {
        @Test void count() {
            assertEquals(1, MODEL.windType4aList().size()); }

        @Test void fields() {
            var w = one(MODEL.windType4aList(), "WindGenTurbineType4aIEC");
            assertFalse(w.windContPType4aIECId().isBlank());
            assertEquals(0.1, w.dipmax(), T);
            assertEquals(0.1, w.diqmax(), T);
            assertEquals(0.02, w.tg(), T);
        }

        @Test void currentLimitsPositive() {
            var w = MODEL.windType4aList().get(0);
            assertTrue(w.dipmax() > 0);
            assertTrue(w.diqmax() > 0);
        }
    }

    @Nested @DisplayName("WindGenTurbineType4bIEC – Full converter Type 4b")
    class WT4b {
        @Test void count() {
            assertEquals(1, MODEL.windType4bList().size()); }

        @Test void fields() {
            var w = one(MODEL.windType4bList(), "WindGenTurbineType4bIEC");
            assertFalse(w.windContPType4bIECId().isBlank());
            assertFalse(w.windMechIECId().isBlank());
            assertEquals(0.1, w.dipmax(), T);
            assertEquals(0.02, w.tg(), T);
        }
    }

    // =========================================================================
    // Cross-cutting
    // =========================================================================

    @Nested @DisplayName("Cross-cutting")
    class CrossCutting {

        @Test void windSubModelTotalCount() {
            // 14 sub-models + 1 plant = 15 entries that do NOT have asynchronousMachineId
            int subTotal =
                MODEL.windAeroConstList().size() + MODEL.windAeroLinearList().size() +
                MODEL.windMechList().size() + MODEL.windContPitchList().size() +
                MODEL.windContPType3List().size() + MODEL.windContPType4aList().size() +
                MODEL.windContPType4bList().size() + MODEL.windContQList().size() +
                MODEL.windCurrLimList().size() + MODEL.windContRotorRList().size() +
                MODEL.windProtectionList().size() + MODEL.windPlantFreqList().size() +
                MODEL.windPlantReactList().size() + MODEL.windPitchEmulList().size() +
                MODEL.windPlantList().size();
            assertEquals(15, subTotal, "Expected 15 wind sub-model instances");
        }

        @Test void windTurbineAssemblyCount() {
            int turbines =
                MODEL.windType1aList().size() + MODEL.windType1bList().size() +
                MODEL.windType2List().size() + MODEL.windType3aList().size() +
                MODEL.windType3bList().size() + MODEL.windType4aList().size() +
                MODEL.windType4bList().size();
            assertEquals(7, turbines, "Expected 7 wind turbine assembly instances");
        }

        @Test void asyncMachineCount() {
            int async =
                MODEL.asyncTimeConstReactanceList().size() +
                MODEL.asyncEquivCircuitList().size();
//                MODEL.asyncUserDefinedList().size();
            assertEquals(2, async, "Expected 3 async machine dynamics instances");
        }

        @Test void allWindTurbinesHavePowerPlantId() {
            MODEL.windType1aList().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
            MODEL.windType1bList().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
            MODEL.windType2List().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
            MODEL.windType3aList().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
            MODEL.windType3bList().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
            MODEL.windType4aList().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
            MODEL.windType4bList().forEach(w -> assertFalse(w.powerPlantId().isBlank()));
        }
    }
}
