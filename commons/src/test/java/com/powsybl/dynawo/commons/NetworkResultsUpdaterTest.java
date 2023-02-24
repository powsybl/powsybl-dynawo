/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.commons;

import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import com.powsybl.iidm.xml.ExportOptions;
import com.powsybl.iidm.xml.NetworkXml;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public class NetworkResultsUpdaterTest extends AbstractDynawoCommonsTest {

    @Test
    public void testUpdateWithoutMergeLoads() throws IOException {
        Network expected = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network actual = NetworkXml.copy(expected);
        reset(actual);
        NetworkResultsUpdater.update(actual, expected, false);
        compare(expected, actual);
    }

    @Test
    public void testUpdateWithMergeLoads() throws IOException {
        Network expected = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network actual = NetworkXml.copy(expected);
        NetworkResultsUpdater.update(actual, LoadsMerger.mergeLoads(expected), true);
        compare(expected, actual);
    }

    @Test
    public void testUpdateNetworkPassingThroughBusBreaker() throws IOException {
        Network expected = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network actual = NetworkXml.copy(expected);
        reset(actual);

        // We assume that the original network will be updated by some analysis tool
        // but it is exchanged with that external tool at Bus/Breaker view
        // We want to ensure that the "actual" Node/Breaker network
        // is properly updated from the Bus/Breaker "solution" exchanged
        // with the external analysis tool
        Path pexpectedAsBusBreaker = tmpDir.resolve("expected-as-busbreaker.xiidm");
        NetworkXml.write(expected, new ExportOptions().setTopologyLevel(TopologyLevel.BUS_BREAKER), pexpectedAsBusBreaker);
        Network expectedBusBreaker = NetworkXml.read(pexpectedAsBusBreaker);
        NetworkResultsUpdater.update(actual, expectedBusBreaker, false);

        compare(expected, actual);
    }

    @Test
    public void testUpdateWithDisconnects() throws IOException {
        Network expected = FourSubstationsNodeBreakerFactory.create();
        Network actual = NetworkXml.copy(expected);
        reset(actual);

        // Test with some elements disconnected in the network
        expected.getLoads().iterator().next().getTerminal().disconnect();
        expected.getGenerators().iterator().next().getTerminal().disconnect();
        expected.getShuntCompensators().iterator().next().getTerminal().disconnect();
        expected.getLines().iterator().next().getTerminal1().disconnect();
        expected.getLines().iterator().next().getTerminal2().disconnect();
        expected.getTwoWindingsTransformers().iterator().next().getTerminal1().disconnect();
        expected.getTwoWindingsTransformers().iterator().next().getTerminal2().disconnect();

        NetworkResultsUpdater.update(actual, expected, false);
        compare(expected, actual);
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
