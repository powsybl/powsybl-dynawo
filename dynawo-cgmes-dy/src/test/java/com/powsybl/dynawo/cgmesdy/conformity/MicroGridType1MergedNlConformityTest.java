/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.conformity;

import com.powsybl.dynawo.cgmesdy.CgmesDyConstants;
import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.dynawo.cgmesdy.parser.CgmesDyImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Conformity test: loads the ENTSO-E MicroGrid Type1 Merged Netherlands dynamic profile
 * and verifies that all model instances are correctly parsed by the dynawo-cgmes-dy module.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
class MicroGridType1MergedNlConformityTest {

    private static final String RESOURCE =
        "/conformity/MicroGrid-Type1/MicroGrid-Type1-Merged/20210420T1730Z_1D_NL_DY_001.xml";

    private static final double T = 1e-9;

    private static CgmesDyModel model;

    @BeforeAll
    static void parse() {
        InputStream stream = MicroGridType1MergedNlConformityTest.class.getResourceAsStream(RESOURCE);
        assertNotNull(stream, "Conformity resource not found: " + RESOURCE);
        model = new CgmesDyImporter().importDy(stream, CgmesDyConstants.CIM17_NS);
    }

    @Test
    void syncMachineTimeConstantReactancesAreParsed() {
        assertEquals(3, model.syncTimeConstReactanceList().size(),
            "Expected 3 SynchronousMachineTimeConstantReactance instances");
    }

    @Test
    void syncMachineNlG1ParametersAreParsed() {
        var smtcr = model.syncTimeConstReactanceList().stream()
            .filter(s -> s.synchronousMachineId().equals("9c3b8f97-7972-477d-9dc8-87365cc0ad0e"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G1 SynchronousMachineTimeConstantReactance not found"));
        assertEquals(9.9, smtcr.inertia(), T);
        assertEquals(1.0, smtcr.damping(), T);
        assertEquals(0.1934, smtcr.statorLeakageReactance(), T);
        assertEquals(0.0, smtcr.statorResistance(), T);
        assertEquals("SynchronousMachineModelKind.subtransient", smtcr.modelType());
        assertEquals("IfdBaseKind.ifag", smtcr.ifdBaseType());
        assertEquals(0.0, smtcr.ks(), T);
        assertEquals(2.35, smtcr.xDirectSync(), T);
        assertEquals(0.452, smtcr.xDirectTrans(), T);
        assertEquals(0.318, smtcr.xDirectSubtrans(), T);
        assertEquals(2.24, smtcr.xQuadSync(), T);
        assertEquals(0.318, smtcr.xQuadTrans(), T);
        assertEquals(0.318, smtcr.xQuadSubtrans(), T);
        assertEquals(8.8, smtcr.tpdo(), T);
        assertEquals(0.031, smtcr.tppdo(), T);
        assertEquals(0.04, smtcr.tpqo(), T);
        assertEquals(0.0354, smtcr.tppqo(), T);
    }

    @Test
    void syncMachineNlG2ParametersAreParsed() {
        var smtcr = model.syncTimeConstReactanceList().stream()
            .filter(s -> s.synchronousMachineId().equals("2844585c-0d35-488d-a449-685bcd57afbf"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G2 SynchronousMachineTimeConstantReactance not found"));
        assertEquals(4.4, smtcr.inertia(), T);
        assertEquals(1.0, smtcr.damping(), T);
        assertEquals(0.23, smtcr.statorLeakageReactance(), T);
        assertEquals(0.0, smtcr.statorResistance(), T);
        assertTrue(Double.isNaN(smtcr.saturationFactor()));
        assertTrue(Double.isNaN(smtcr.saturationFactor120()));
        assertEquals("SynchronousMachineModelKind.subtransient", smtcr.modelType());
        assertEquals("IfdBaseKind.ifag", smtcr.ifdBaseType());
        assertEquals(0.0, smtcr.ks(), T);
        assertEquals(1.98, smtcr.xDirectSync(), T);
        assertEquals(0.37, smtcr.xDirectTrans(), T);
        assertEquals(0.24, smtcr.xDirectSubtrans(), T);
        assertEquals(0.8, smtcr.xQuadSync(), T);
        assertEquals(0.24, smtcr.xQuadTrans(), T);
        assertEquals(0.24, smtcr.xQuadSubtrans(), T);
        assertEquals(9.6, smtcr.tpdo(), T);
        assertEquals(0.08, smtcr.tppdo(), T);
        assertEquals(0.09, smtcr.tpqo(), T);
        assertEquals(0.0837, smtcr.tppqo(), T);
    }

    @Test
    void syncMachineNlG3ParametersAreParsed() {
        var smtcr = model.syncTimeConstReactanceList().stream()
            .filter(s -> s.synchronousMachineId().equals("1dc9afba-23b5-41a0-8540-b479ed8baf4b"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G3 SynchronousMachineTimeConstantReactance not found"));
        assertEquals(4.4, smtcr.inertia(), T);
        assertEquals(1.0, smtcr.damping(), T);
        assertEquals(0.23, smtcr.statorLeakageReactance(), T);
        assertEquals(0.0, smtcr.statorResistance(), T);
        assertEquals(0.02, smtcr.saturationFactor(), T);
        assertEquals(0.12, smtcr.saturationFactor120(), T);
        assertEquals("SynchronousMachineModelKind.subtransient", smtcr.modelType());
        assertEquals("IfdBaseKind.ifag", smtcr.ifdBaseType());
        assertEquals(0.0, smtcr.ks(), T);
        assertEquals(1.98, smtcr.xDirectSync(), T);
        assertEquals(0.37, smtcr.xDirectTrans(), T);
        assertEquals(0.24, smtcr.xDirectSubtrans(), T);
        assertEquals(0.8, smtcr.xQuadSync(), T);
        assertEquals(0.24, smtcr.xQuadTrans(), T);
        assertEquals(0.24, smtcr.xQuadSubtrans(), T);
        assertEquals(9.6, smtcr.tpdo(), T);
        assertEquals(0.08, smtcr.tppdo(), T);
        assertEquals(0.09, smtcr.tpqo(), T);
        assertEquals(0.0837, smtcr.tppqo(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEST1AsAreParsed() {
        assertEquals(2, model.excIEEEST1AList().size(), "Expected 2 ExcIEEEST1A");
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEST1ANlG2IsParsed() {
        var exc = model.excIEEEST1AList().stream()
            .filter(e -> e.synchronousMachineId().equals("e58068fe-5436-4a2a-aa72-da3377ea1355"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G2 ExcIEEEST1A not found"));
        assertEquals(190.0, exc.ka(), T);
        assertEquals(0.1, exc.ta(), T);
        assertEquals(10.0, exc.tb(), T);
        assertEquals(0.001, exc.tb1(), T);
        assertEquals(1.5, exc.tc(), T);
        assertEquals(0.001, exc.tc1(), T);
        assertEquals(0.2, exc.vimax(), T);
        assertEquals(-0.2, exc.vimin(), T);
        assertEquals(8.0, exc.vamax(), T);
        assertEquals(-8.0, exc.vamin(), T);
        assertEquals(7.8, exc.vrmax(), T);
        assertEquals(-6.7, exc.vrmin(), T);
        assertEquals(0.08, exc.kc(), T);
        assertEquals(0.02, exc.kf(), T);
        assertEquals(0.8, exc.tf(), T);
        assertEquals(0.0, exc.klr(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEST1ANlG3IsParsed() {
        var exc = model.excIEEEST1AList().stream()
            .filter(e -> e.synchronousMachineId().equals("f450a965-7cfb-4516-a2bf-618f05e84eed"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G3 ExcIEEEST1A not found"));
        assertEquals(220.0, exc.ka(), T);
        assertEquals(0.1, exc.ta(), T);
        assertEquals(10.0, exc.tb(), T);
        assertEquals(0.001, exc.tb1(), T);
        assertEquals(1.5, exc.tc(), T);
        assertEquals(0.001, exc.tc1(), T);
        assertEquals(0.2, exc.vimax(), T);
        assertEquals(-0.2, exc.vimin(), T);
        assertEquals(8.0, exc.vamax(), T);
        assertEquals(-8.0, exc.vamin(), T);
        assertEquals(7.8, exc.vrmax(), T);
        assertEquals(-6.7, exc.vrmin(), T);
        assertEquals(0.08, exc.kc(), T);
        assertEquals(0.02, exc.kf(), T);
        assertEquals(0.8, exc.tf(), T);
        assertEquals(0.0, exc.klr(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEAC1AIsParsed() {
        assertEquals(1, model.excIEEEAC1AList().size(), "Expected 1 ExcIEEEAC1A");
        var exc = model.excIEEEAC1AList().get(0);
        assertEquals("bd63d5d8-3d61-4233-a3e0-a66d512c1574", exc.synchronousMachineId());
        assertEquals(30.0, exc.ka(), T);
        assertEquals(0.05, exc.ta(), T);
        assertEquals(0.02, exc.tb(), T);
        assertEquals(0.001, exc.tc(), T);
        assertEquals(0.4, exc.te(), T);
        assertEquals(0.03, exc.kf(), T);
        assertEquals(1.0, exc.tf(), T);
        assertEquals(0.2, exc.kc(), T);
        assertEquals(0.4, exc.kd(), T);
        assertEquals(1.0, exc.ke(), T);
        assertEquals(10.0, exc.vamax(), T);
        assertEquals(-10.0, exc.vamin(), T);
        assertEquals(10.0, exc.vrmax(), T);
        assertEquals(-5.0, exc.vrmin(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void pssIEEE2BsAreParsed() {
        assertEquals(3, model.pssIEEE2BList().size(), "Expected 3 PssIEEE2B");
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void pssIEEE2BNlG2IsParsed() {
        var pss = model.pssIEEE2BList().stream()
            .filter(p -> p.excitationSystemId().equals("3bd610c9-8ecd-4466-a5ea-c998749a38ea"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G2 PssIEEE2B not found"));
        assertEquals("InputSignalKind.rotorAngularFrequencyDeviation", pss.inputSignal1Type());
        assertEquals("InputSignalKind.generatorElectricalPower", pss.inputSignal2Type());
        assertEquals(10.0, pss.ks1(), T);
        assertEquals(0.31, pss.ks2(), T);
        assertEquals(1.0, pss.ks3(), T);
        assertEquals(2.0, pss.tw1(), T);
        assertEquals(2.0, pss.tw2(), T);
        assertEquals(2.0, pss.tw3(), T);
        assertEquals(0.0, pss.tw4(), T);
        assertEquals(0.12, pss.t1(), T);
        assertEquals(0.02, pss.t2(), T);
        assertEquals(0.4, pss.t3(), T);
        assertEquals(0.02, pss.t4(), T);
        assertEquals(0.0, pss.t6(), T);
        assertEquals(2.0, pss.t7(), T);
        assertEquals(0.2, pss.t8(), T);
        assertEquals(0.1, pss.t9(), T);
        assertEquals(0.0, pss.t10(), T);
        assertEquals(0.0, pss.t11(), T);
        assertEquals(1.0, pss.n(), T);
        assertEquals(5.0, pss.m(), T);
        assertEquals(0.1, pss.vstmax(), T);
        assertEquals(-0.1, pss.vstmin(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void pssIEEE2BNlG1IsParsed() {
        var pss = model.pssIEEE2BList().stream()
            .filter(p -> p.excitationSystemId().equals("a29ea0a1-af24-4c9b-aa8f-5c97374996e2"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G1 PssIEEE2B not found"));
        assertEquals("InputSignalKind.rotorAngularFrequencyDeviation", pss.inputSignal1Type());
        assertEquals("InputSignalKind.generatorElectricalPower", pss.inputSignal2Type());
        assertEquals(12.0, pss.ks1(), T);
        assertEquals(0.2, pss.ks2(), T);
        assertEquals(1.0, pss.ks3(), T);
        assertEquals(2.0, pss.tw1(), T);
        assertEquals(2.0, pss.tw2(), T);
        assertEquals(2.0, pss.tw3(), T);
        assertEquals(0.0, pss.tw4(), T);
        assertEquals(0.12, pss.t1(), T);
        assertEquals(0.02, pss.t2(), T);
        assertEquals(0.3, pss.t3(), T);
        assertEquals(0.02, pss.t4(), T);
        assertEquals(0.0, pss.t6(), T);
        assertEquals(2.0, pss.t7(), T);
        assertEquals(0.0, pss.t8(), T);
        assertEquals(0.1, pss.t9(), T);
        assertEquals(0.0, pss.t10(), T);
        assertEquals(0.0, pss.t11(), T);
        assertEquals(1.0, pss.n(), T);
        assertEquals(5.0, pss.m(), T);
        assertEquals(0.1, pss.vstmax(), T);
        assertEquals(-0.1, pss.vstmin(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void pssIEEE2BNlG3IsParsed() {
        var pss = model.pssIEEE2BList().stream()
            .filter(p -> p.excitationSystemId().equals("cf42c5ad-b3f9-4f9f-ae5e-6169cb07ae89"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G3 PssIEEE2B not found"));
        assertEquals("InputSignalKind.rotorAngularFrequencyDeviation", pss.inputSignal1Type());
        assertEquals("InputSignalKind.generatorElectricalPower", pss.inputSignal2Type());
        assertEquals(10.0, pss.ks1(), T);
        assertEquals(0.31, pss.ks2(), T);
        assertEquals(1.0, pss.ks3(), T);
        assertEquals(2.0, pss.tw1(), T);
        assertEquals(2.0, pss.tw2(), T);
        assertEquals(2.0, pss.tw3(), T);
        assertEquals(0.0, pss.tw4(), T);
        assertEquals(0.3, pss.t1(), T);
        assertEquals(0.02, pss.t2(), T);
        assertEquals(0.45, pss.t3(), T);
        assertEquals(0.02, pss.t4(), T);
        assertEquals(0.0, pss.t6(), T);
        assertEquals(2.0, pss.t7(), T);
        assertEquals(0.2, pss.t8(), T);
        assertEquals(0.1, pss.t9(), T);
        assertEquals(0.0, pss.t10(), T);
        assertEquals(0.0, pss.t11(), T);
        assertEquals(1.0, pss.n(), T);
        assertEquals(5.0, pss.m(), T);
        assertEquals(0.1, pss.vstmax(), T);
        assertEquals(-0.1, pss.vstmin(), T);
    }

    @Test
    void govHydro1sAreParsed() {
        assertEquals(2, model.govHydro1List().size(), "Expected 2 GovHydro1");
    }

    @Test
    void govHydro1NlG2IsParsed() {
        var gov = model.govHydro1List().stream()
            .filter(g -> g.synchronousMachineId().equals("e58068fe-5436-4a2a-aa72-da3377ea1355"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G2 GovHydro1 not found"));
        assertEquals(225.0, gov.mwbase(), T);
        assertEquals(10.4, gov.tr(), T);
        assertEquals(0.099, gov.tf(), T);
        assertEquals(0.9, gov.tg(), T);
        assertEquals(0.2, gov.velm(), T);
        assertEquals(2.6, gov.tw(), T);
        assertEquals(1.2, gov.at(), T);
        assertEquals(0.3, gov.dturb(), T);
        assertEquals(0.08, gov.qnl(), T);
        assertEquals(0.05, gov.rperm(), T);
        assertEquals(1.27, gov.rtemp(), T);
        assertEquals(1.0, gov.hdam(), T);
    }

    @Test
    void govHydro1NlG3IsParsed() {
        var gov = model.govHydro1List().stream()
            .filter(g -> g.synchronousMachineId().equals("f450a965-7cfb-4516-a2bf-618f05e84eed"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G3 GovHydro1 not found"));
        assertEquals(225.0, gov.mwbase(), T);
        assertEquals(10.4, gov.tr(), T);
        assertEquals(0.099, gov.tf(), T);
        assertEquals(0.9, gov.tg(), T);
        assertEquals(0.2, gov.velm(), T);
        assertEquals(2.6, gov.tw(), T);
        assertEquals(1.2, gov.at(), T);
        assertEquals(0.3, gov.dturb(), T);
        assertEquals(0.08, gov.qnl(), T);
        assertEquals(0.05, gov.rperm(), T);
        assertEquals(1.27, gov.rtemp(), T);
        assertEquals(1.0, gov.hdam(), T);
    }

    @Test
    void govHydro2IsParsed() {
        assertEquals(1, model.govHydro2List().size(), "Expected 1 GovHydro2");
        var gov = model.govHydro2List().get(0);
        assertEquals("bd63d5d8-3d61-4233-a3e0-a66d512c1574", gov.synchronousMachineId());
        assertEquals(990.0, gov.mwbase(), T);
        assertEquals(9.19, gov.tr(), T);
        assertEquals(1.0, gov.pmax(), T);
        assertEquals(-1.0, gov.pmin(), T);
        assertEquals(0.04, gov.tp(), T);
        assertEquals(-1.0, gov.aturb(), T);
        assertEquals(0.5, gov.bturb(), T);
        assertEquals(1.84, gov.tw(), T);
        assertEquals(0.0, gov.db1(), T);
        assertEquals(0.0, gov.eps(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void vCompIEEEType1sAreParsed() {
        assertEquals(3, model.vCompIEEEType1List().size(), "Expected 3 VCompIEEEType1");
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void vCompIEEEType1NlG2IsParsed() {
        var vcomp = model.vCompIEEEType1List().stream()
            .filter(v -> v.excitationSystemId().equals("3bd610c9-8ecd-4466-a5ea-c998749a38ea"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G2 VCompIEEEType1 not found"));
        assertEquals(0.0, vcomp.tr(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void vCompIEEEType1NlG1IsParsed() {
        var vcomp = model.vCompIEEEType1List().stream()
            .filter(v -> v.excitationSystemId().equals("a29ea0a1-af24-4c9b-aa8f-5c97374996e2"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G1 VCompIEEEType1 not found"));
        assertEquals(0.0, vcomp.tr(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void vCompIEEEType1NlG3IsParsed() {
        var vcomp = model.vCompIEEEType1List().stream()
            .filter(v -> v.excitationSystemId().equals("cf42c5ad-b3f9-4f9f-ae5e-6169cb07ae89"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NL-G3 VCompIEEEType1 not found"));
        assertEquals(0.0, vcomp.tr(), T);
    }

}
