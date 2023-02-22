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
import com.powsybl.dynawo.commons.loadmerge.LoadPowers;
import com.powsybl.dynawo.commons.loadmerge.LoadState;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
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
        for (HvdcLine sourceHvdcLine : sourceNetwork.getHvdcLines()) {
            Terminal targetTerminal1 = targetNetwork.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(HvdcLine.Side.ONE).getTerminal();
            Terminal targetTerminal2 = targetNetwork.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(HvdcLine.Side.TWO).getTerminal();
            Terminal sourceTerminal1 = sourceHvdcLine.getConverterStation(HvdcLine.Side.ONE).getTerminal();
            Terminal sourceTerminal2 = sourceHvdcLine.getConverterStation(HvdcLine.Side.TWO).getTerminal();
            update(targetTerminal1, sourceTerminal1);
            update(targetTerminal2, sourceTerminal2);
        }
        for (TwoWindingsTransformer sourceTwoWindingsTransformer : sourceNetwork.getTwoWindingsTransformers()) {
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
        for (ThreeWindingsTransformer sourceThreeWindingsTransformer : sourceNetwork.getThreeWindingsTransformers()) {
            ThreeWindingsTransformer targetThreeWindingsTransformer = targetNetwork.getThreeWindingsTransformer(sourceThreeWindingsTransformer.getId());
            update(targetThreeWindingsTransformer.getLeg1(), sourceThreeWindingsTransformer.getLeg1());
            update(targetThreeWindingsTransformer.getLeg2(), sourceThreeWindingsTransformer.getLeg2());
            update(targetThreeWindingsTransformer.getLeg3(), sourceThreeWindingsTransformer.getLeg3());
        }
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
        }
        for (Switch sourceSwitch : sourceNetwork.getSwitches()) {
            targetNetwork.getSwitch(sourceSwitch.getId()).setOpen(sourceSwitch.isOpen());
        }
        // We have to update the voltages AFTER all possible topology changes have been updated in the target Network,
        // At this point, the buses in the BusBreakerView of target and source should match
        // We choose to iterate over BusBreakerView buses instead of BusView buses because they are more stable:
        // a use-case when we need to export a node/breaker network to bus/breaker to Dynawo exists,
        // and reading the results from Dynawo-exported bus/breaker will end up with different ids at BusView level
        for (Bus sourceBus : sourceNetwork.getBusBreakerView().getBuses()) {
            Bus targetBus = targetNetwork.getBusBreakerView().getBus(sourceBus.getId());
            if (targetBus == null) {
                LOG.error("Source bus {} not found in target network. Voltage not updated ({}, {})", sourceBus.getId(), sourceBus.getV(), sourceBus.getAngle());
            } else {
                targetBus.setV(sourceBus.getV());
                targetBus.setAngle(sourceBus.getAngle());
            }
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
        } else if (!source.isConnected()) {
            target.disconnect();
        }
    }

    private static void update(Terminal target, Terminal mergedSource, LoadState mergedLoadState) {
        target.setP(mergedSource.getP() * target.getP() / mergedLoadState.getP());
        target.setQ(mergedSource.getQ() * target.getQ() / mergedLoadState.getQ());
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
            for (Bus busTarget : targetNetwork.getBusBreakerView().getBuses()) {
                updateLoads(busTarget, sourceNetwork.getBusBreakerView().getBus(busTarget.getId()));
            }
        }
    }

    private static void updateLoads(Bus busTarget, Bus busSource) {
        Iterable<Load> loadsTarget = busTarget.getLoads();
        int nbLoads = Iterables.size(loadsTarget);
        if (nbLoads == 0) {
            return;
        }
        Map<LoadPowers, Terminal> mergedLoadsTerminal = busSource.getLoadStream()
                .collect(Collectors.toMap(LoadsMerger::getLoadPowers, Load::getTerminal));
        LoadsMerger.getLoadsToMergeList(busTarget).forEach(unmergedLoads -> {
            Terminal mergedLoadTerminal = Optional.ofNullable(mergedLoadsTerminal.get(unmergedLoads.getLoadPowers()))
                    .orElseThrow(() -> new PowsyblException("Missing merged load in bus " + busTarget.getId()));
            for (Load load : unmergedLoads.getLoads()) {
                if (unmergedLoads.isSingle()) {
                    update(load.getTerminal(), mergedLoadTerminal);
                } else {
                    update(load.getTerminal(), mergedLoadTerminal, unmergedLoads.getMergedState());
                }
            }
        });
    }
}
