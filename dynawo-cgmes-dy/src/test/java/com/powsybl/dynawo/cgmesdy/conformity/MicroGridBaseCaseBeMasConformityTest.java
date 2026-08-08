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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Conformity test: loads the ENTSO-E MicroGrid Base Case Belgium MAS dynamic profile
 * and verifies that all model instances are correctly parsed by the dynawo-cgmes-dy module.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
class MicroGridBaseCaseBeMasConformityTest {

    private static final String RESOURCE =
        "/conformity/MicroGid-BaseCase/MicroGrid-BE-MAS/20210420T1730Z_1D_BE_DY_001.xml";

    private static final double T = 1e-9;

    private static CgmesDyModel model;

    @BeforeAll
    static void parse() {
        InputStream stream = MicroGridBaseCaseBeMasConformityTest.class.getResourceAsStream(RESOURCE);
        assertNotNull(stream, "Conformity resource not found: " + RESOURCE);
        model = new CgmesDyImporter().importDy(stream, CgmesDyConstants.CIM17_NS);
    }

    @Test
    void syncMachineTimeConstantReactancesAreParsed() {
        assertEquals(2, model.syncTimeConstReactanceList().size(),
            "Expected 2 SynchronousMachineTimeConstantReactance instances");
    }

    @Test
    void syncMachineBeG2ParametersAreParsed() {
        var smtcr = model.syncTimeConstReactanceList().stream()
            .filter(s -> s.synchronousMachineId().equals("550ebe0d-f2b2-48c1-991f-cebea43a21aa"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("BE-G2 SynchronousMachineTimeConstantReactance not found"));
        assertEquals(5.0, smtcr.inertia(), T);
        assertEquals(0.01, smtcr.damping(), T);
        assertEquals(0.15, smtcr.statorLeakageReactance(), T);
        assertEquals(0.0, smtcr.statorResistance(), T);
        assertEquals(0.02, smtcr.saturationFactor(), T);
        assertEquals(0.12, smtcr.saturationFactor120(), T);
        assertEquals("SynchronousMachineModelKind.subtransient", smtcr.modelType());
        assertEquals("IfdBaseKind.ifag", smtcr.ifdBaseType());
        assertEquals(0.0, smtcr.ks(), T);
        assertEquals(1.81, smtcr.xDirectSync(), T);
        assertEquals(0.3, smtcr.xDirectTrans(), T);
        assertEquals(0.23, smtcr.xDirectSubtrans(), T);
        assertEquals(1.76, smtcr.xQuadSync(), T);
        assertEquals(0.65, smtcr.xQuadTrans(), T);
        assertEquals(0.25, smtcr.xQuadSubtrans(), T);
        assertEquals(8.0, smtcr.tpdo(), T);
        assertEquals(0.03, smtcr.tppdo(), T);
        assertEquals(1.0, smtcr.tpqo(), T);
        assertEquals(0.07, smtcr.tppqo(), T);
    }

    @Test
    void syncMachineBeG1ParametersAreParsed() {
        var smtcr = model.syncTimeConstReactanceList().stream()
            .filter(s -> s.synchronousMachineId().equals("3a3b27be-b18b-4385-b557-6735d733baf0"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("BE-G1 SynchronousMachineTimeConstantReactance not found"));
        assertEquals(4.25, smtcr.inertia(), T);
        assertEquals(0.0, smtcr.damping(), T);
        assertEquals(0.16, smtcr.statorLeakageReactance(), T);
        assertEquals(0.0, smtcr.statorResistance(), T);
        assertEquals(0.02, smtcr.saturationFactor(), T);
        assertEquals(0.12, smtcr.saturationFactor120(), T);
        assertEquals(0.0, smtcr.ks(), T);
        assertEquals(1.97, smtcr.xDirectSync(), T);
        assertEquals(0.29, smtcr.xDirectTrans(), T);
        assertEquals(0.2, smtcr.xDirectSubtrans(), T);
        assertEquals(1.97, smtcr.xQuadSync(), T);
        assertEquals(0.29, smtcr.xQuadTrans(), T);
        assertEquals(0.2, smtcr.xQuadSubtrans(), T);
        assertEquals(6.4, smtcr.tpdo(), T);
        assertEquals(0.12, smtcr.tppdo(), T);
        assertEquals(6.4, smtcr.tpqo(), T);
        assertEquals(0.12, smtcr.tppqo(), T);
    }

    @Test
    void govSteam1IsParsed() {
        assertEquals(1, model.govSteam1List().size(), "Expected 1 GovSteam1");
        var gov = model.govSteam1List().get(0);
        assertEquals(255.0, gov.mwbase(), T);
        assertEquals(25.0, gov.k(), T);
        assertEquals(5.0, gov.t1(), T);
        assertEquals(1.5, gov.t2(), T);
        assertEquals(0.3, gov.t3(), T);
        assertEquals(1.0, gov.uo(), T);
        assertEquals(-1.05, gov.uc(), T);
        assertEquals(1.05, gov.pmax(), T);
        assertEquals(0.0, gov.pmin(), T);
        assertEquals(0.15, gov.t4(), T);
        assertEquals(0.25, gov.k1(), T);
        assertEquals(0.0, gov.k2(), T);
        assertEquals(2.0, gov.t5(), T);
        assertEquals(0.6, gov.k3(), T);
        assertEquals(0.0, gov.k4(), T);
        assertEquals(0.1, gov.t6(), T);
        assertEquals(0.15, gov.k5(), T);
        assertEquals(0.0, gov.k6(), T);
        assertEquals(0.0, gov.t7(), T);
        assertEquals(0.0, gov.k7(), T);
        assertEquals(0.0, gov.k8(), T);
        assertEquals(0.0, gov.db1(), T);
        assertEquals(0.0, gov.eps(), T);
        assertEquals(0.0, gov.db2(), T);
        assertEquals(0.0, gov.gv1(), T);
        assertEquals(0.0, gov.pgv1(), T);
        assertEquals(0.25, gov.gv2(), T);
        assertEquals(0.25, gov.pgv2(), T);
        assertEquals(0.5, gov.gv3(), T);
        assertEquals(0.5, gov.pgv3(), T);
        assertEquals(0.75, gov.gv4(), T);
        assertEquals(0.75, gov.pgv4(), T);
        assertEquals(1.0, gov.gv5(), T);
        assertEquals(1.0, gov.pgv5(), T);
        assertEquals(1.25, gov.gv6(), T);
        assertEquals(1.25, gov.pgv6(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void excIEEEAC4AIsParsed() {
        assertEquals(1, model.excIEEEAC4AList().size(), "Expected 1 ExcIEEEAC4A");
        var exc = model.excIEEEAC4AList().get(0);
        assertEquals(200.0, exc.ka(), T);
        assertEquals(0.07, exc.ta(), T);
        assertEquals(8.5, exc.tb(), T);
        assertEquals(1.8, exc.tc(), T);
        assertEquals(0.1, exc.vimax(), T);
        assertEquals(-0.1, exc.vimin(), T);
        assertEquals(5.0, exc.vrmax(), T);
        assertEquals(-4.65, exc.vrmin(), T);
        assertEquals(0.05, exc.kc(), T);
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void pssIEEE2BIsParsed() {
        assertEquals(1, model.pssIEEE2BList().size(), "Expected 1 PssIEEE2B");
        var pss = model.pssIEEE2BList().get(0);
        assertEquals("InputSignalKind.rotorAngularFrequencyDeviation", pss.inputSignal1Type());
        assertEquals("InputSignalKind.generatorElectricalPower", pss.inputSignal2Type());
        assertEquals(16.0, pss.ks1(), T);
        assertEquals(0.31, pss.ks2(), T);
        assertEquals(1.0, pss.ks3(), T);
        assertEquals(2.0, pss.tw1(), T);
        assertEquals(2.0, pss.tw2(), T);
        assertEquals(2.0, pss.tw3(), T);
        assertEquals(0.0, pss.tw4(), T);
        assertEquals(0.2, pss.t1(), T);
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
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    void vCompIEEEType1IsParsed() {
        assertEquals(1, model.vCompIEEEType1List().size(), "Expected 1 VCompIEEEType1");
        var vcomp = model.vCompIEEEType1List().get(0);
        assertEquals("f7d4412c-6567-4759-aa48-4c881dd79902", vcomp.excitationSystemId());
        assertEquals(0.0, vcomp.tr(), T);
    }

}
