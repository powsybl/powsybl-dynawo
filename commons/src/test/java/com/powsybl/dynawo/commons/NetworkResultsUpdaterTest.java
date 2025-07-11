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
import com.powsybl.iidm.network.test.ThreeWindingsTransformerNetworkFactory;
import com.powsybl.iidm.serde.ExportOptions;
import com.powsybl.iidm.serde.NetworkSerDe;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Guillem Jané Guasch {@literal <janeg at aia.es>}
 */
class NetworkResultsUpdaterTest extends AbstractDynawoCommonsTest {

    @Test
    void testUpdateWithoutMergeLoads() throws IOException {
        Network expected = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network actual = NetworkSerDe.copy(expected);
        reset(actual);
        NetworkResultsUpdater.update(actual, expected, false);
        compare(expected, actual);
    }

    @Test
    void testUpdateWithMergeLoads() throws IOException {
        Network expected = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network actual = NetworkSerDe.copy(expected);
        NetworkResultsUpdater.update(actual, LoadsMerger.mergeLoads(expected), true);
        compare(expected, actual);
    }

    @Test
    void testUpdateNetworkPassingThroughBusBreaker() throws IOException {
        Network expected = TestNetworkFactory.createMultiBusesVoltageLevelNetwork();
        Network actual = NetworkSerDe.copy(expected);
        reset(actual);

        // We assume that the original network will be updated by some analysis tool
        // but it is exchanged with that external tool at Bus/Breaker view
        // We want to ensure that the "actual" Node/Breaker network
        // is properly updated from the Bus/Breaker "solution" exchanged
        // with the external analysis tool
        Path pexpectedAsBusBreaker = tmpDir.resolve("expected-as-busbreaker.xiidm");
        NetworkSerDe.write(expected, new ExportOptions().setTopologyLevel(TopologyLevel.BUS_BREAKER), pexpectedAsBusBreaker);
        Network expectedBusBreaker = NetworkSerDe.read(pexpectedAsBusBreaker);
        NetworkResultsUpdater.update(actual, expectedBusBreaker, false);

        compare(expected, actual);
    }

    @Test
    void testUpdateWithDisconnects() throws IOException {
        Network updated = FourSubstationsNodeBreakerFactory.create();
        Network actual = NetworkSerDe.copy(updated);
        reset(actual);

        // Test with some elements disconnected in the network
        updated.getLoads().iterator().next().getTerminal().disconnect();
        updated.getGenerators().iterator().next().getTerminal().disconnect();
        updated.getShuntCompensators().iterator().next().getTerminal().disconnect();
        updated.getLines().iterator().next().getTerminal1().disconnect();
        updated.getLines().iterator().next().getTerminal2().disconnect();
        updated.getTwoWindingsTransformers().iterator().next().getTerminal1().disconnect();
        updated.getTwoWindingsTransformers().iterator().next().getTerminal2().disconnect();

        Network expected = NetworkSerDe.copy(updated);
        expected.getShuntCompensator("SHUNT").setSolvedSectionCount(1);
        expected.getTwoWindingsTransformer("TWT").getPhaseTapChanger().setSolvedTapPosition(15);
        expected.getTwoWindingsTransformer("TWT").getRatioTapChanger().setSolvedTapPosition(1);

        NetworkResultsUpdater.update(actual, updated, false);
        compare(expected, actual);
    }

    @Test
    void testUpdateSolvedValues() throws IOException {
        Network expected = FourSubstationsNodeBreakerFactory.create();
        Network updated = NetworkSerDe.copy(expected);
        Network actual = NetworkSerDe.copy(expected);
        reset(actual);

        updated.getShuntCompensator("SHUNT").setSectionCount(1);
        updated.getTwoWindingsTransformer("TWT").getPhaseTapChanger().setTapPosition(2);
        updated.getTwoWindingsTransformer("TWT").getRatioTapChanger().setTapPosition(2);
        expected.getShuntCompensator("SHUNT").setSolvedSectionCount(1);
        expected.getTwoWindingsTransformer("TWT").getPhaseTapChanger().setSolvedTapPosition(2);
        expected.getTwoWindingsTransformer("TWT").getRatioTapChanger().setSolvedTapPosition(2);

        NetworkResultsUpdater.update(actual, updated, false);
        compare(expected, actual);
    }

    @Test
    void testUpdateThreeWindingsTransformerSolvedValues() throws IOException {
        Network expected = ThreeWindingsTransformerNetworkFactory.create();
        expected.getThreeWindingsTransformer("3WT").getLeg2().newPhaseTapChanger()
                .setLowTapPosition(0)
                .setTapPosition(2)
                .setRegulationMode(PhaseTapChanger.RegulationMode.CURRENT_LIMITER)
                .setRegulating(false)
                .beginStep().setR(39.78473).setX(29.784725).setG(0.0).setB(0.0).setRho(1.0).setAlpha(-42.8).endStep()
                .beginStep().setR(31.720245).setX(21.720242).setG(0.0).setB(0.0).setRho(1.0).setAlpha(-40.18).endStep()
                .beginStep().setR(23.655737).setX(13.655735).setG(0.0).setB(0.0).setRho(1.0).setAlpha(-37.54).endStep()
                .add();
        Network updated = NetworkSerDe.copy(expected);
        Network actual = NetworkSerDe.copy(expected);
        reset(actual);

        updated.getThreeWindingsTransformer("3WT").getLeg2().getPhaseTapChanger().setTapPosition(1);
        updated.getThreeWindingsTransformer("3WT").getLeg2().getRatioTapChanger().setTapPosition(1);
        expected.getThreeWindingsTransformer("3WT").getLeg2().getPhaseTapChanger().setSolvedTapPosition(1);
        expected.getThreeWindingsTransformer("3WT").getLeg2().getRatioTapChanger().setSolvedTapPosition(1);
        expected.getThreeWindingsTransformer("3WT").getLeg3().getRatioTapChanger().setSolvedTapPosition(0);

        NetworkResultsUpdater.update(actual, updated, false);
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
            reset(targetHvdcLine.getConverterStation(TwoSides.ONE).getTerminal());
            reset(targetHvdcLine.getConverterStation(TwoSides.TWO).getTerminal());
        }
        for (TwoWindingsTransformer targetTwoWindingsTransformer : targetNetwork.getTwoWindingsTransformers()) {
            reset(targetTwoWindingsTransformer.getTerminal1());
            reset(targetTwoWindingsTransformer.getTerminal2());

            PhaseTapChanger targetPhaseTapChanger = targetTwoWindingsTransformer.getPhaseTapChanger();
            if (targetPhaseTapChanger != null) {
                targetPhaseTapChanger.setSolvedTapPosition(targetPhaseTapChanger.getLowTapPosition());
            }

            RatioTapChanger targetRatioTapChanger = targetTwoWindingsTransformer.getRatioTapChanger();
            if (targetRatioTapChanger != null) {
                targetRatioTapChanger.setSolvedTapPosition(targetRatioTapChanger.getLowTapPosition());
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
            targetShuntCompensator.setSolvedSectionCount(0);
            reset(targetShuntCompensator.getTerminal());
        }
        for (StaticVarCompensator targetStaticVarCompensator : targetNetwork.getStaticVarCompensators()) {
            targetStaticVarCompensator.setRegulationMode(StaticVarCompensator.RegulationMode.VOLTAGE);
            targetStaticVarCompensator.setRegulating(false);
            reset(targetStaticVarCompensator.getTerminal());
        }
    }

    private static void reset(ThreeWindingsTransformer.Leg leg) {
        reset(leg.getTerminal());
        PhaseTapChanger phaseTapChanger = leg.getPhaseTapChanger();
        if (phaseTapChanger != null) {
            phaseTapChanger.setSolvedTapPosition(phaseTapChanger.getLowTapPosition());
        }
        RatioTapChanger ratioTapChanger = leg.getRatioTapChanger();
        if (ratioTapChanger != null) {
            ratioTapChanger.setSolvedTapPosition(ratioTapChanger.getLowTapPosition());
        }
    }

    private static void reset(Terminal target) {
        target.setP(Double.NaN);
        target.setQ(Double.NaN);
        target.disconnect();
    }
}
