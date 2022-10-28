/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.ComparisonUtils;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.ExportOptions;
import com.powsybl.iidm.xml.NetworkXml;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public class DynawoResultsNetworkUpdateTest extends AbstractConverterTest {

    @Test
    public void testUpdateNodeBreaker() throws IOException {
        Network expected = createTestCaseNodeBreaker();
        Network actual = createTestCaseNodeBreaker();
        reset(actual);
        DynawoResultsNetworkUpdate.update(actual, expected);
        compare(expected, actual);
    }

    @Test
    public void testUpdateNodeBreakerPassingThroughBusBreaker() throws IOException {
        Network expected = createTestCaseNodeBreaker();
        Network actual = createTestCaseNodeBreaker();
        reset(actual);

        // We assume that the original network will be updated by some analysis tool
        // but it is exchanged with that external tool at Bus/Breaker view
        // We want to ensure that the the "actual" Node/Breaker network
        // is properly updated from the Bus/Breaker "solution" exchanged
        // with the external analysis tool
        Path pexpectedAsBusBreaker = tmpDir.resolve("expected-as-busbreaker.xiidm");
        NetworkXml.write(expected, new ExportOptions().setTopologyLevel(TopologyLevel.BUS_BREAKER), pexpectedAsBusBreaker);
        Network expectedBusBreaker = NetworkXml.read(pexpectedAsBusBreaker);
        DynawoResultsNetworkUpdate.update(actual, expectedBusBreaker);

        compare(expected, actual);
    }

    @Test
    public void testUpdateBusBranch() throws IOException {
        Network expected = createTestCaseBusBranch();
        Network actual = createTestCaseBusBranch();
        reset(actual);
        DynawoResultsNetworkUpdate.update(actual, expected);
        compare(expected, actual);
    }

    @Test
    public void testUpdateMicro() throws IOException {
        Network expected = createTestMicro();
        Network actual = createTestMicro();
        reset(actual);
        DynawoResultsNetworkUpdate.update(actual, expected);
        compare(expected, actual);
    }

    @Test
    public void testUpdateMicroDisconnects() throws IOException {
        Network expected = createTestMicro();
        // Test with some elements disconnected in the network
        expected.getLoads().iterator().next().getTerminal().disconnect();
        expected.getGenerators().iterator().next().getTerminal().disconnect();
        expected.getShuntCompensators().iterator().next().getTerminal().disconnect();
        expected.getLines().iterator().next().getTerminal1().disconnect();
        expected.getLines().iterator().next().getTerminal2().disconnect();
        expected.getTwoWindingsTransformers().iterator().next().getTerminal1().disconnect();
        expected.getTwoWindingsTransformers().iterator().next().getTerminal2().disconnect();
        expected.getThreeWindingsTransformers().iterator().next().getLeg1().getTerminal().disconnect();
        expected.getThreeWindingsTransformers().iterator().next().getLeg2().getTerminal().disconnect();
        expected.getThreeWindingsTransformers().iterator().next().getLeg3().getTerminal().disconnect();
        // For consistency, we must remove the voltage for all configured buses that have been left isolated
        // (All buses in bus breaker view that have not received a calculated bus in the bus view)
        for (Bus b : expected.getBusBreakerView().getBuses()) {
            if (b.getVoltageLevel().getBusView().getMergedBus(b.getId()) == null) {
                b.setV(Double.NaN);
                b.setAngle(Double.NaN);
            }
        }

        Network actual = createTestMicro();
        reset(actual);
        DynawoResultsNetworkUpdate.update(actual, expected);
        compare(expected, actual);
    }

    private void compare(Network expected, Network actual) throws IOException {
        Path pexpected = tmpDir.resolve("expected.xiidm");
        assertNotNull(pexpected);
        Path pactual = tmpDir.resolve("actual.xiidm");
        assertNotNull(pactual);
        NetworkXml.write(expected, pexpected);
        actual.setCaseDate(expected.getCaseDate());
        NetworkXml.write(actual, pactual);
        ComparisonUtils.compareXml(Files.newInputStream(pexpected), Files.newInputStream(pactual));
    }

    private static Network createTestCaseBusBranch() {
        return Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranch", new ResourceSet("/SmallBusBranch", "SmallBusBranch.xiidm")), null);
    }

    private static Network createTestCaseNodeBreaker() {
        return Importers.importData("XIIDM", new ResourceDataSource("SmallNodeBreaker_fix_line_044bbe91", new ResourceSet("/SmallNodeBreaker", "SmallNodeBreaker_fix_line_044bbe91.xiidm")), null);
    }

    private static Network createTestMicro() {
        return Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);
    }

    private static void reset(Network targetNetwork) {
        for (Bus targetBus : targetNetwork.getBusView().getBuses()) {
            targetBus.setV(Double.NaN);
            targetBus.setAngle(Double.NaN);
        }
        for (Line targetline : targetNetwork.getLines()) {
            reset(targetline.getTerminal1());
            reset(targetline.getTerminal2());
        }
        for (DanglingLine targetDangling : targetNetwork.getDanglingLines()) {
            reset(targetDangling.getTerminal());
        }
        for (HvdcLine targetHvdcLine : targetNetwork.getHvdcLines()) {
            reset(targetHvdcLine.getConverterStation(HvdcLine.Side.ONE).getTerminal());
            reset(targetHvdcLine.getConverterStation(HvdcLine.Side.TWO).getTerminal());
        }
        for (TwoWindingsTransformer targetTwoWindingsTransformer : targetNetwork.getTwoWindingsTransformers()) {
            reset(targetTwoWindingsTransformer.getTerminal1());
            reset(targetTwoWindingsTransformer.getTerminal2());

            PhaseTapChanger targetPhaseTapChanger = targetTwoWindingsTransformer.getPhaseTapChanger();
            if (targetPhaseTapChanger != null) {
                targetPhaseTapChanger.setTapPosition(targetPhaseTapChanger.getLowTapPosition());
            }

            RatioTapChanger targetRatioTapChanger = targetTwoWindingsTransformer.getRatioTapChanger();
            if (targetRatioTapChanger != null) {
                targetRatioTapChanger.setTapPosition(targetRatioTapChanger.getLowTapPosition());
            }
        }
        for (ThreeWindingsTransformer targetThreeWindingsTransformer : targetNetwork.getThreeWindingsTransformers()) {
            reset(targetThreeWindingsTransformer.getLeg1());
            reset(targetThreeWindingsTransformer.getLeg2());
            reset(targetThreeWindingsTransformer.getLeg3());
        }
        for (Load targetLoad : targetNetwork.getLoads()) {
            reset(targetLoad.getTerminal());
        }
        for (Generator targetGenerator : targetNetwork.getGenerators()) {
            reset(targetGenerator.getTerminal());
        }
        for (ShuntCompensator targetShuntCompensator : targetNetwork.getShuntCompensators()) {
            targetShuntCompensator.setSectionCount(0);
            reset(targetShuntCompensator.getTerminal());
        }
        for (StaticVarCompensator targetStaticVarCompensator : targetNetwork.getStaticVarCompensators()) {
            targetStaticVarCompensator.setRegulationMode(StaticVarCompensator.RegulationMode.OFF);
            reset(targetStaticVarCompensator.getTerminal());
        }
        for (Switch targetSwitch : targetNetwork.getSwitches()) {
            targetSwitch.setOpen(false);
        }
    }

    private static void reset(ThreeWindingsTransformer.Leg leg) {
        reset(leg.getTerminal());
        PhaseTapChanger phaseTapChanger = leg.getPhaseTapChanger();
        if (phaseTapChanger != null) {
            phaseTapChanger.setTapPosition(phaseTapChanger.getLowTapPosition());
        }
        RatioTapChanger ratioTapChanger = leg.getRatioTapChanger();
        if (ratioTapChanger != null) {
            ratioTapChanger.setTapPosition(ratioTapChanger.getLowTapPosition());
        }
    }

    private static void reset(Terminal target) {
        target.setP(Double.NaN);
        target.setQ(Double.NaN);
        target.disconnect();
    }
}
