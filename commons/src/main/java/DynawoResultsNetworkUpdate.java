import com.powsybl.iidm.network.*;

public final class DynawoResultsNetworkUpdate {
    private DynawoResultsNetworkUpdate() {
    }

    public static void update(Network networkSource, Network networkTarget) {
        // Update Bus
        for (Bus busSource : networkSource.getBusView().getBuses()) {
            Bus busTarget = networkTarget.getBusView().getBus(busSource.getId());
            busTarget.setV(busSource.getV());
            busTarget.setAngle(busSource.getAngle());
        }

        // Update Lines
        for (Line lineSource : networkSource.getLines()) {
            Terminal terminalTarget1 = networkTarget.getLine(lineSource.getId()).getTerminal1();
            Terminal terminalTarget2 = networkTarget.getLine(lineSource.getId()).getTerminal2();
            Terminal terminalSource1 = lineSource.getTerminal1();
            Terminal terminalSource2 = lineSource.getTerminal2();

            terminalTarget1.setP(terminalSource1.getP());
            terminalTarget1.setQ(terminalSource1.getQ());
            terminalTarget2.setP(terminalSource2.getP());
            terminalTarget2.setQ(terminalSource2.getQ());

            if (terminalSource1.isConnected() && !terminalTarget1.isConnected()) {
                terminalTarget1.connect();
            } else if (!terminalSource1.isConnected() && terminalTarget1.isConnected()) {
                terminalTarget1.disconnect();
            }

            if (terminalSource2.isConnected() && !terminalTarget2.isConnected()) {
                terminalTarget2.connect();
            } else if (!terminalSource2.isConnected() && terminalTarget2.isConnected()) {
                terminalTarget2.disconnect();
            }
        }

        // Update DanglingLines
        for (DanglingLine lineSource : networkSource.getDanglingLines()) {
            Terminal terminalTarget = networkTarget.getDanglingLine(lineSource.getId()).getTerminal();
            Terminal terminalSource = lineSource.getTerminal();

            terminalTarget.setP(terminalSource.getP());
            terminalTarget.setQ(terminalSource.getQ());

            if (terminalSource.isConnected() && !terminalTarget.isConnected()) {
                terminalTarget.connect();
            } else if (!terminalSource.isConnected() && terminalTarget.isConnected()) {
                terminalTarget.disconnect();
            }
        }

        // Update HvdcLines
        for (HvdcLine lineSource : networkSource.getHvdcLines()) {
            Terminal terminalTarget1 = networkTarget.getHvdcLine(lineSource.getId()).getConverterStation(HvdcLine.Side.ONE).getTerminal();
            Terminal terminalTarget2 = networkTarget.getHvdcLine(lineSource.getId()).getConverterStation(HvdcLine.Side.TWO).getTerminal();
            Terminal terminalSource1 = lineSource.getConverterStation(HvdcLine.Side.ONE).getTerminal();
            Terminal terminalSource2 = lineSource.getConverterStation(HvdcLine.Side.TWO).getTerminal();

            terminalTarget1.setP(terminalSource1.getP());
            terminalTarget1.setQ(terminalSource1.getQ());
            terminalTarget2.setP(terminalSource2.getP());
            terminalTarget2.setQ(terminalSource2.getQ());

            if (terminalSource1.isConnected() && !terminalTarget1.isConnected()) {
                terminalTarget1.connect();
            } else if (!terminalSource1.isConnected() && terminalTarget1.isConnected()) {
                terminalTarget1.disconnect();
            }

            if (terminalSource2.isConnected() && !terminalTarget2.isConnected()) {
                terminalTarget2.connect();
            } else if (!terminalSource2.isConnected() && terminalTarget2.isConnected()) {
                terminalTarget2.disconnect();
            }
        }

        // Update TwoWindingTransformer
        for (TwoWindingsTransformer transformerSource : networkSource.getTwoWindingsTransformers()) {
            Terminal terminalTarget1 = networkTarget.getTwoWindingsTransformer(transformerSource.getId()).getTerminal1();
            Terminal terminalTarget2 = networkTarget.getTwoWindingsTransformer(transformerSource.getId()).getTerminal2();
            Terminal terminalSource1 = transformerSource.getTerminal1();
            Terminal terminalSource2 = transformerSource.getTerminal2();

            terminalTarget1.setP(terminalSource1.getP());
            terminalTarget1.setQ(terminalSource1.getQ());
            terminalTarget2.setP(terminalSource2.getP());
            terminalTarget2.setQ(terminalSource2.getQ());

            if (terminalSource1.isConnected() && !terminalTarget1.isConnected()) {
                terminalTarget1.connect();
            } else if (!terminalSource1.isConnected() && terminalTarget1.isConnected()) {
                terminalTarget1.disconnect();
            }

            if (terminalSource2.isConnected() && !terminalTarget2.isConnected()) {
                terminalTarget2.connect();
            } else if (!terminalSource2.isConnected() && terminalTarget2.isConnected()) {
                terminalTarget2.disconnect();
            }

            PhaseTapChanger phaseChangerSource = transformerSource.getPhaseTapChanger();
            PhaseTapChanger phaseChangerTarget = networkTarget.getTwoWindingsTransformer(transformerSource.getId()).getPhaseTapChanger();
            phaseChangerTarget.setTapPosition(phaseChangerSource.getTapPosition());
            RatioTapChanger ratioChangerSource = transformerSource.getRatioTapChanger();
            RatioTapChanger ratioChangerTarget = networkTarget.getTwoWindingsTransformer(transformerSource.getId()).getRatioTapChanger();
            ratioChangerTarget.setTapPosition(ratioChangerSource.getTapPosition());
        }

        // Update Loads
        for (Load loadSource : networkSource.getLoads()) {
            Terminal terminalTarget = networkTarget.getLoad(loadSource.getId()).getTerminal();
            Terminal terminalSource = loadSource.getTerminal();
            terminalTarget.setP(terminalSource.getP());
            terminalTarget.setQ(terminalSource.getQ());
            if (terminalSource.isConnected() && !terminalTarget.isConnected()) {
                terminalTarget.connect();
            } else if (!terminalSource.isConnected() && terminalTarget.isConnected()) {
                terminalTarget.disconnect();
            }
        }

        // Update Generators
        for (Generator generatorSource : networkSource.getGenerators()) {
            Terminal terminalTarget = networkTarget.getGenerator(generatorSource.getId()).getTerminal();
            Terminal terminalSource = generatorSource.getTerminal();
            terminalTarget.setP(terminalSource.getP());
            terminalTarget.setQ(terminalSource.getQ());
            if (terminalSource.isConnected() && !terminalTarget.isConnected()) {
                terminalTarget.connect();
            } else if (!terminalSource.isConnected() && terminalTarget.isConnected()) {
                terminalTarget.disconnect();
            }
        }
    }
}
