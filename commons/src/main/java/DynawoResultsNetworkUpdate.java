import com.powsybl.iidm.network.*;

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
            targetPhaseTapChanger.setTapPosition(sourcePhaseTapChanger.getTapPosition());

            RatioTapChanger sourceRatioTapChanger = sourceTwoWindingsTransformer.getRatioTapChanger();
            RatioTapChanger targetRatioTapChanger = targetNetwork.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getRatioTapChanger();
            targetRatioTapChanger.setTapPosition(sourceRatioTapChanger.getTapPosition());
        }
        for (Load sourceLoad : sourceNetwork.getLoads()) {
            update(targetNetwork.getLoad(sourceLoad.getId()).getTerminal(), sourceLoad.getTerminal());
        }
        for (Generator sourceGenerator : sourceNetwork.getGenerators()) {
            update(targetNetwork.getGenerator(sourceGenerator.getId()).getTerminal(), sourceGenerator.getTerminal());
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
}
