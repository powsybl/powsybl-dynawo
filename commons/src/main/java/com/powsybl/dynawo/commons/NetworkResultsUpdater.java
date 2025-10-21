/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.commons.loadmerge.LoadPowersSigns;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Guillem Jané Guasch {@literal <janeg at aia.es>}
 */
public final class NetworkResultsUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkResultsUpdater.class);

    private NetworkResultsUpdater() {
    }

    public static void update(Network targetNetwork, Network sourceNetwork, boolean mergeLoads) {
        updateLoads(targetNetwork, sourceNetwork, mergeLoads);
        for (Line lineSource : sourceNetwork.getLines()) {
            update(targetNetwork.getLine(lineSource.getId()), lineSource);
        }
        for (DanglingLine sourceDangling : sourceNetwork.getDanglingLines()) {
            update(targetNetwork.getDanglingLine(sourceDangling.getId()), sourceDangling);
        }

        updateHvdcLines(targetNetwork, sourceNetwork.getHvdcLines());
        updateTwoWindingsTransformers(targetNetwork, sourceNetwork.getTwoWindingsTransformers());
        updateThreeWindingsTransformers(targetNetwork, sourceNetwork.getThreeWindingsTransformers());

        for (Generator sourceGenerator : sourceNetwork.getGenerators()) {
            update(targetNetwork.getGenerator(sourceGenerator.getId()), sourceGenerator);
        }
        for (ShuntCompensator sourceShuntCompensator : sourceNetwork.getShuntCompensators()) {
            ShuntCompensator targetShuntCompensator = targetNetwork.getShuntCompensator(sourceShuntCompensator.getId());
            targetShuntCompensator.setSolvedSectionCount(sourceShuntCompensator.getSectionCount());
            update(targetShuntCompensator, sourceShuntCompensator);
        }
        for (StaticVarCompensator sourceStaticVarCompensator : sourceNetwork.getStaticVarCompensators()) {
            StaticVarCompensator targetStaticVarCompensator = targetNetwork.getStaticVarCompensator(sourceStaticVarCompensator.getId());
            update(targetStaticVarCompensator, sourceStaticVarCompensator);
            targetStaticVarCompensator.setRegulationMode(sourceStaticVarCompensator.getRegulationMode());
            targetStaticVarCompensator.setRegulating(sourceStaticVarCompensator.isRegulating());
        }
        for (Switch sourceSwitch : sourceNetwork.getSwitches()) {
            targetNetwork.getSwitch(sourceSwitch.getId()).setOpen(sourceSwitch.isOpen());
        }
        // We have to update the voltages AFTER all possible topology changes have been updated in the target Network,
        // At this point, the buses in the BusBreakerView of target and source should match
        // We choose to iterate over BusBreakerView buses instead of BusView buses because they are more stable:
        // a use-case when we need to export a node/breaker network to bus/breaker to Dynawo exists,
        // and reading the results from Dynawo-exported bus/breaker will end up with different ids at BusView level
        Map<String, Bus> targetNetworkBusBreakerViewBusById = targetNetwork.getBusBreakerView().getBusStream()
                .collect(Collectors.toMap(Identifiable::getId, Function.identity())); // it is needed to pre-index into a map as in network store n.getBusBreakerView().getBus(id) is slow
        for (Bus sourceBus : sourceNetwork.getBusBreakerView().getBuses()) {
            Bus targetBus = targetNetworkBusBreakerViewBusById.get(sourceBus.getId());
            if (targetBus == null) {
                LOG.error("Source bus {} not found in target network. Voltage not updated ({}, {})", sourceBus.getId(), sourceBus.getV(), sourceBus.getAngle());
            } else {
                targetBus.setV(sourceBus.getV());
                targetBus.setAngle(sourceBus.getAngle());
            }
        }
    }

    private static void updateHvdcLines(Network targetNetwork, Iterable<HvdcLine> hvdcLines) {
        for (HvdcLine sourceHvdcLine : hvdcLines) {
            HvdcLine targetHvdcLine = targetNetwork.getHvdcLine(sourceHvdcLine.getId());
            update(targetHvdcLine.getConverterStation(TwoSides.ONE), sourceHvdcLine.getConverterStation(TwoSides.ONE));
            update(targetHvdcLine.getConverterStation(TwoSides.TWO), sourceHvdcLine.getConverterStation(TwoSides.TWO));
        }
    }

    private static void updateTwoWindingsTransformers(Network targetNetwork, Iterable<TwoWindingsTransformer> twoWindingsTransformers) {
        for (TwoWindingsTransformer sourceTransformer : twoWindingsTransformers) {
            TwoWindingsTransformer targetTransformer = targetNetwork.getTwoWindingsTransformer(sourceTransformer.getId());
            update(targetTransformer, sourceTransformer);

            PhaseTapChanger targetPhaseTapChanger = targetTransformer.getPhaseTapChanger();
            if (targetPhaseTapChanger != null) {
                targetPhaseTapChanger.setSolvedTapPosition(sourceTransformer.getPhaseTapChanger().getTapPosition());
            }

            RatioTapChanger targetRatioTapChanger = targetTransformer.getRatioTapChanger();
            if (targetRatioTapChanger != null) {
                targetRatioTapChanger.setSolvedTapPosition(sourceTransformer.getRatioTapChanger().getTapPosition());
            }
        }
    }

    private static void updateThreeWindingsTransformers(Network targetNetwork, Iterable<ThreeWindingsTransformer> threeWindingsTransformers) {
        for (ThreeWindingsTransformer sourceThreeWindingsTransformer : threeWindingsTransformers) {
            ThreeWindingsTransformer targetThreeWindingsTransformer = targetNetwork.getThreeWindingsTransformer(sourceThreeWindingsTransformer.getId());
            update(targetThreeWindingsTransformer.getLeg1(), sourceThreeWindingsTransformer.getLeg1());
            update(targetThreeWindingsTransformer.getLeg2(), sourceThreeWindingsTransformer.getLeg2());
            update(targetThreeWindingsTransformer.getLeg3(), sourceThreeWindingsTransformer.getLeg3());
        }
    }

    private static void update(ThreeWindingsTransformer.Leg target, ThreeWindingsTransformer.Leg source) {
        update(target.getTerminal(), source.getTerminal());

        PhaseTapChanger sourcePhaseTapChanger = source.getPhaseTapChanger();
        PhaseTapChanger targetPhaseTapChanger = target.getPhaseTapChanger();
        if (targetPhaseTapChanger != null) {
            targetPhaseTapChanger.setSolvedTapPosition(sourcePhaseTapChanger.getTapPosition());
        }

        RatioTapChanger sourceRatioTapChanger = source.getRatioTapChanger();
        RatioTapChanger targetRatioTapChanger = target.getRatioTapChanger();
        if (targetRatioTapChanger != null) {
            targetRatioTapChanger.setSolvedTapPosition(sourceRatioTapChanger.getTapPosition());
        }
    }

    private static void update(Branch<?> target, Branch<?> source) {
        update(target.getTerminal1(), source.getTerminal1());
        update(target.getTerminal2(), source.getTerminal2());
    }

    private static void update(Injection<?> target, Injection<?> source) {
        update(target.getTerminal(), source.getTerminal());
    }

    private static void update(Terminal target, Terminal source) {
        target.setP(source.getP());
        target.setQ(source.getQ());
        if (source.isConnected()) {
            target.connect();
        } else {
            target.disconnect();
        }
    }

    private static void update(Terminal target, Terminal mergedSource, double targetGroupP, double targetGroupQ) {
        double pRatio = target.getP() / targetGroupP;
        double qRatio = target.getQ() / targetGroupQ;
        target.setP(mergedSource.getP() * pRatio);
        target.setQ(mergedSource.getQ() * qRatio);
        if (mergedSource.isConnected()) {
            target.connect();
        } else {
            target.disconnect();
        }
    }

    private static void updateLoads(Network targetNetwork, Network sourceNetwork, boolean mergeLoads) {
        if (mergeLoads) {
            Network.BusBreakerView sourceBusBreakerView = sourceNetwork.getBusBreakerView();
            for (Bus busTarget : targetNetwork.getBusBreakerView().getBuses()) {
                updateLoadsWithMergedLoads(busTarget, sourceBusBreakerView.getBus(busTarget.getId()));
            }
            //handle fictitious load
            sourceNetwork.getLoadStream()
                    .filter(Identifiable::isFictitious)
                    .forEach(l -> update(targetNetwork.getLoad(l.getId()), l));
        } else {
            for (Load sourceLoad : sourceNetwork.getLoads()) {
                update(targetNetwork.getLoad(sourceLoad.getId()), sourceLoad);
            }
        }
    }

    private static void updateLoadsWithMergedLoads(Bus busTarget, Bus busSource) {
        Iterable<Load> loadsTarget = busTarget.getLoads();
        if (loadsTarget instanceof Collection<Load> c ? c.isEmpty() : !loadsTarget.iterator().hasNext()) {
            return;
        }

        Map<LoadPowersSigns, Terminal> mergedLoadsTerminals = LoadsMerger.getLoadTerminalByPowersSigns(busSource);
        LoadsMerger.getLoadTerminalsByPowersSigns(busTarget).forEach((loadPowersSigns, loadTerminalsGroup) -> {
            Terminal mergedLoadTerminal = mergedLoadsTerminals.get(loadPowersSigns);
            if (mergedLoadTerminal == null) {
                throw new PowsyblException("Missing merged load in bus " + busTarget.getId());
            }
            if (loadTerminalsGroup.size() == 1) {
                update(loadTerminalsGroup.get(0), mergedLoadTerminal);
            } else {
                double groupP = loadTerminalsGroup.stream().mapToDouble(Terminal::getP).sum();
                double groupQ = loadTerminalsGroup.stream().mapToDouble(Terminal::getQ).sum();
                loadTerminalsGroup.forEach(terminal -> update(terminal, mergedLoadTerminal, groupP, groupQ));
            }
        });
    }
}
