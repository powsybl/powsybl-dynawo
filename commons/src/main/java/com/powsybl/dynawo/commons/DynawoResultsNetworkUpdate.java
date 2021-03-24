/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.commons;

import com.powsybl.iidm.network.*;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public final class DynawoResultsNetworkUpdate {
    private DynawoResultsNetworkUpdate() {
    }

    public static void update(Network targetNetwork, Network sourceNetwork) {
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
        for (Load sourceLoad : sourceNetwork.getLoads()) {
            update(targetNetwork.getLoad(sourceLoad.getId()).getTerminal(), sourceLoad.getTerminal());
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
        // At this point, the buses in the BusView of target and source should match
        for (Bus sourceBus : sourceNetwork.getBusView().getBuses()) {
            Bus targetBus = targetNetwork.getBusView().getBus(sourceBus.getId());
            targetBus.setV(sourceBus.getV());
            targetBus.setAngle(sourceBus.getAngle());
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
}
