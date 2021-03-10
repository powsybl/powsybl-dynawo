import com.powsybl.iidm.network.*;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public final class DynawoResultsNetworkUpdate {
    private DynawoResultsNetworkUpdate() {
    }

    public static void update(Network targetNetwork, Network sourceNetwork) {
        // TODO Consider if we could go directly a BusBreakerView instead of BusView
        // Looking for a Bus with a given identifier in BusView is not optimal
        for (Bus sourceBus : sourceNetwork.getBusView().getBuses()) {
            Bus targetBus = targetNetwork.getBusView().getBus(sourceBus.getId());
            targetBus.setV(sourceBus.getV());
            targetBus.setAngle(sourceBus.getAngle());
        }
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
    }

    public static boolean equalState(Network sourceNetwork, Network verifyNetwork) {
        for (Bus sourceBus : sourceNetwork.getBusView().getBuses()) {
            Bus verifyBus = verifyNetwork.getBusView().getBus(sourceBus.getId());
            if (sourceBus.getV() != verifyBus.getV()) {
                return false;
            }
            if (sourceBus.getAngle() != verifyBus.getAngle()) {
                return false;
            }
        }
        for (Line lineSource : sourceNetwork.getLines()) {
            if (!equalState(lineSource.getTerminal1(), verifyNetwork.getLine(lineSource.getId()).getTerminal1())) {
                return false;
            }
            if (!equalState(lineSource.getTerminal2(), verifyNetwork.getLine(lineSource.getId()).getTerminal2())) {
                return false;
            }
        }
        for (DanglingLine sourceDangling : sourceNetwork.getDanglingLines()) {
            if (!equalState(sourceDangling.getTerminal(), verifyNetwork.getDanglingLine(sourceDangling.getId()).getTerminal())) {
                return false;
            }
        }
        for (HvdcLine sourceHvdcLine : sourceNetwork.getHvdcLines()) {
            if (!equalState(sourceHvdcLine.getConverterStation(HvdcLine.Side.ONE).getTerminal(),
                    verifyNetwork.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(HvdcLine.Side.ONE).getTerminal())) {
                return false;
            }
            if (!equalState(sourceHvdcLine.getConverterStation(HvdcLine.Side.TWO).getTerminal(),
                    verifyNetwork.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(HvdcLine.Side.TWO).getTerminal())) {
                return false;
            }
        }
        for (TwoWindingsTransformer sourceTwoWindingsTransformer : sourceNetwork.getTwoWindingsTransformers()) {
            Terminal verifyTerminal1 = verifyNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getTerminal1();
            Terminal verifyTerminal2 = verifyNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getTerminal2();
            Terminal sourceTerminal1 = sourceTwoWindingsTransformer.getTerminal1();
            Terminal sourceTerminal2 = sourceTwoWindingsTransformer.getTerminal2();
            if (!equalState(sourceTerminal1, verifyTerminal1)) {
                return false;
            }
            if (!equalState(sourceTerminal2, verifyTerminal2)) {
                return false;
            }
            PhaseTapChanger sourcePhaseTapChanger = sourceTwoWindingsTransformer.getPhaseTapChanger();
            PhaseTapChanger verifyPhaseTapChanger = verifyNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getPhaseTapChanger();
            if ((sourcePhaseTapChanger != null) && (verifyPhaseTapChanger != null)) {
                if (sourcePhaseTapChanger.getTapPosition() != verifyPhaseTapChanger.getTapPosition()) {
                    return false;
                }
            }

            RatioTapChanger sourceRatioTapChanger = sourceTwoWindingsTransformer.getRatioTapChanger();
            RatioTapChanger verifyRatioTapChanger = verifyNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getRatioTapChanger();
            if ((sourceRatioTapChanger != null) && (verifyRatioTapChanger != null)) {
                if (sourceRatioTapChanger.getTapPosition() != verifyRatioTapChanger.getTapPosition()) {
                    return false;
                }
            }
        }
        for (Load sourceLoad : sourceNetwork.getLoads()) {
            if (!equalState(sourceLoad.getTerminal(), verifyNetwork.getLoad(sourceLoad.getId()).getTerminal())) {
                return false;
            }
        }
        for (Generator sourceGenerator : sourceNetwork.getGenerators()) {
            if (!equalState(sourceGenerator.getTerminal(), verifyNetwork.getGenerator(sourceGenerator.getId()).getTerminal())) {
                return false;
            }
        }
        for (ShuntCompensator sourceShuntCompensator : sourceNetwork.getShuntCompensators()) {
            if (sourceShuntCompensator.getSectionCount() != verifyNetwork.getShuntCompensator(sourceShuntCompensator.getId()).getSectionCount()) {
                return false;
            }
            if (!equalState(sourceShuntCompensator.getTerminal(), verifyNetwork.getShuntCompensator(sourceShuntCompensator.getId()).getTerminal())) {
                return false;
            }
        }
        for (StaticVarCompensator sourceStaticVarCompensator : sourceNetwork.getStaticVarCompensators()) {
            if (sourceStaticVarCompensator.getRegulationMode() != verifyNetwork.getStaticVarCompensator(sourceStaticVarCompensator.getId()).getRegulationMode()) {
                return false;
            }
            if (!equalState(sourceStaticVarCompensator.getTerminal(), verifyNetwork.getStaticVarCompensator(sourceStaticVarCompensator.getId()).getTerminal())) {
                return false;
            }
        }
        for (Switch sourceSwitch : sourceNetwork.getSwitches()) {
            if (sourceSwitch.isOpen() != verifyNetwork.getSwitch(sourceSwitch.getId()).isOpen()) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalState(Terminal sourceTerminal, Terminal verifyTerminal) {
        if (sourceTerminal.getP() != verifyTerminal.getP()) {
            return false;
        }
        if (sourceTerminal.getQ() != verifyTerminal.getQ()) {
            return false;
        }
        return sourceTerminal.isConnected() == verifyTerminal.isConnected();
    }

    public static void zero(Network targetNetwork) {
        for (Bus targetBus : targetNetwork.getBusView().getBuses()) {
            targetBus.setV(0.0);
            targetBus.setAngle(0.0);
        }
        for (Line targetline : targetNetwork.getLines()) {
            zero(targetline.getTerminal1());
            zero(targetline.getTerminal2());
        }
        for (DanglingLine targetDangling : targetNetwork.getDanglingLines()) {
            zero(targetDangling.getTerminal());
        }
        for (HvdcLine targetHvdcLine : targetNetwork.getHvdcLines()) {
            zero(targetHvdcLine.getConverterStation(HvdcLine.Side.ONE).getTerminal());
            zero(targetHvdcLine.getConverterStation(HvdcLine.Side.TWO).getTerminal());
        }
        for (TwoWindingsTransformer targetTwoWindingsTransformer : targetNetwork.getTwoWindingsTransformers()) {
            zero(targetTwoWindingsTransformer.getTerminal1());
            zero(targetTwoWindingsTransformer.getTerminal2());

            PhaseTapChanger targetPhaseTapChanger = targetTwoWindingsTransformer.getPhaseTapChanger();
            if (targetPhaseTapChanger != null) {
                targetPhaseTapChanger.setTapPosition(0);
            }

            RatioTapChanger targetRatioTapChanger = targetTwoWindingsTransformer.getRatioTapChanger();
            if (targetRatioTapChanger != null) {
                targetRatioTapChanger.setTapPosition(0);
            }
        }
        for (Load targetLoad : targetNetwork.getLoads()) {
            zero(targetLoad.getTerminal());
        }
        for (Generator targetGenerator : targetNetwork.getGenerators()) {
            zero(targetGenerator.getTerminal());
        }
        for (ShuntCompensator targetShuntCompensator : targetNetwork.getShuntCompensators()) {
            targetShuntCompensator.setSectionCount(0);
            zero(targetShuntCompensator.getTerminal());
        }
        for (StaticVarCompensator targetStaticVarCompensator : targetNetwork.getStaticVarCompensators()) {
            targetStaticVarCompensator.setRegulationMode(StaticVarCompensator.RegulationMode.OFF);
            zero(targetStaticVarCompensator.getTerminal());
        }
        for (Switch targetSwitch : targetNetwork.getSwitches()) {
            targetSwitch.setOpen(false);
        }
    }

    private static void update(Terminal source, Terminal target) {
        target.setP(source.getP());
        target.setQ(source.getQ());
        if (source.isConnected() && !target.isConnected()) {
            target.connect();
        } else if (!source.isConnected() && target.isConnected()) {
            target.disconnect();
        }
    }

    private static void zero(Terminal target) {
        target.setP(0.0);
        target.setQ(0.0);
        target.disconnect();
    }
}
