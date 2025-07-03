/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.google.common.collect.Iterables;
import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.commons.loadmerge.LoadPowersSigns;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
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
            update(targetNetwork.getLine(lineSource.getId()).getTerminal1(), lineSource.getTerminal1());
            update(targetNetwork.getLine(lineSource.getId()).getTerminal2(), lineSource.getTerminal2());
        }
        for (DanglingLine sourceDangling : sourceNetwork.getDanglingLines()) {
            update(targetNetwork.getDanglingLine(sourceDangling.getId()).getTerminal(), sourceDangling.getTerminal());
        }

        updateHvdcLines(targetNetwork, sourceNetwork.getHvdcLines());
        updateTwoWindingsTransformers(targetNetwork, sourceNetwork.getTwoWindingsTransformers());
        updateThreeWindingsTransformers(targetNetwork, sourceNetwork.getThreeWindingsTransformers());

        for (Generator sourceGenerator : sourceNetwork.getGenerators()) {
            update(targetNetwork.getGenerator(sourceGenerator.getId()).getTerminal(), sourceGenerator.getTerminal());
        }
        for (ShuntCompensator sourceShuntCompensator : sourceNetwork.getShuntCompensators()) {
            targetNetwork.getShuntCompensator(sourceShuntCompensator.getId()).setSectionCount(sourceShuntCompensator.getSectionCount());
            update(targetNetwork.getShuntCompensator(sourceShuntCompensator.getId()).getTerminal(), sourceShuntCompensator.getTerminal());
        }
        for (StaticVarCompensator sourceStaticVarCompensator : sourceNetwork.getStaticVarCompensators()) {
            update(targetNetwork.getStaticVarCompensator(sourceStaticVarCompensator.getId()).getTerminal(), sourceStaticVarCompensator.getTerminal());
            targetNetwork.getStaticVarCompensator(sourceStaticVarCompensator.getId()).setRegulationMode(sourceStaticVarCompensator.getRegulationMode());
            targetNetwork.getStaticVarCompensator(sourceStaticVarCompensator.getId()).setRegulating(sourceStaticVarCompensator.isRegulating());
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
            Terminal targetTerminal1 = targetNetwork.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(TwoSides.ONE).getTerminal();
            Terminal targetTerminal2 = targetNetwork.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(TwoSides.TWO).getTerminal();
            Terminal sourceTerminal1 = sourceHvdcLine.getConverterStation(TwoSides.ONE).getTerminal();
            Terminal sourceTerminal2 = sourceHvdcLine.getConverterStation(TwoSides.TWO).getTerminal();
            update(targetTerminal1, sourceTerminal1);
            update(targetTerminal2, sourceTerminal2);
        }
    }

    private static void updateTwoWindingsTransformers(Network targetNetwork, Iterable<TwoWindingsTransformer> twoWindingsTransformers) {
        for (TwoWindingsTransformer sourceTwoWindingsTransformer : twoWindingsTransformers) {
            Terminal targetTerminal1 = targetNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getTerminal1();
            Terminal targetTerminal2 = targetNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getTerminal2();
            Terminal sourceTerminal1 = sourceTwoWindingsTransformer.getTerminal1();
            Terminal sourceTerminal2 = sourceTwoWindingsTransformer.getTerminal2();
            update(targetTerminal1, sourceTerminal1);
            update(targetTerminal2, sourceTerminal2);

            PhaseTapChanger sourcePhaseTapChanger = sourceTwoWindingsTransformer.getPhaseTapChanger();
            PhaseTapChanger targetPhaseTapChanger = targetNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getPhaseTapChanger();
            if (targetPhaseTapChanger != null) {
                targetPhaseTapChanger.setTapPosition(sourcePhaseTapChanger.getTapPosition());
            }

            RatioTapChanger sourceRatioTapChanger = sourceTwoWindingsTransformer.getRatioTapChanger();
            RatioTapChanger targetRatioTapChanger = targetNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getRatioTapChanger();
            if (targetRatioTapChanger != null) {
                targetRatioTapChanger.setTapPosition(sourceRatioTapChanger.getTapPosition());
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
            targetPhaseTapChanger.setTapPosition(sourcePhaseTapChanger.getTapPosition());
        }

        RatioTapChanger sourceRatioTapChanger = source.getRatioTapChanger();
        RatioTapChanger targetRatioTapChanger = target.getRatioTapChanger();
        if (targetRatioTapChanger != null) {
            targetRatioTapChanger.setTapPosition(sourceRatioTapChanger.getTapPosition());
        }
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
        if (!mergeLoads) {
            for (Load sourceLoad : sourceNetwork.getLoads()) {
                update(targetNetwork.getLoad(sourceLoad.getId()).getTerminal(), sourceLoad.getTerminal());
            }
        } else {
            Map<String, Bus> sourceBusesById = sourceNetwork.getBusBreakerView().getBusStream().collect(Collectors.toMap(Identifiable::getId, Function.identity()));
            for (Bus busTarget : targetNetwork.getBusBreakerView().getBuses()) {
                updateLoads(busTarget, sourceBusesById.get(busTarget.getId()));
            }
        }
    }

    private static void updateLoads(Bus busTarget, Bus busSource) {
        Iterable<Load> loadsTarget = busTarget.getLoads();
        int nbLoads = Iterables.size(loadsTarget);
        if (nbLoads == 0) {
            return;
        }
        Map<LoadPowersSigns, Terminal> mergedLoadsTerminal = busSource.getLoadStream()
                .collect(Collectors.toMap(LoadsMerger::getLoadPowersSigns, Load::getTerminal));
        LoadsMerger.getLoadPowersSignsGrouping(busTarget).forEach((loadPowersSigns, loadsGroup) -> {
            Terminal mergedLoadTerminal = Optional.ofNullable(mergedLoadsTerminal.get(loadPowersSigns))
                    .orElseThrow(() -> new PowsyblException("Missing merged load in bus " + busTarget.getId()));
            if (loadsGroup.size() == 1) {
                update(loadsGroup.get(0).getTerminal(), mergedLoadTerminal);
            } else {
                double groupP = loadsGroup.stream().map(Load::getTerminal).mapToDouble(Terminal::getP).sum();
                double groupQ = loadsGroup.stream().map(Load::getTerminal).mapToDouble(Terminal::getQ).sum();
                loadsGroup.forEach(load -> update(load.getTerminal(), mergedLoadTerminal, groupP, groupQ));
            }
        });
    }
}
