import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.xml.NetworkXml;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public class DynawoResultsNetworkUpdateTest extends AbstractConverterTest {

    @Test
    public void testUpdate() {
        Network expected = createTestCase();
        Network actual = createTestCase();

        reset(actual);
        System.err.println("buses expected before update : " + Arrays.toString(expected.getBusView().getBusStream().toArray()));
        DynawoResultsNetworkUpdate.update(actual, expected);
        System.err.println("buses expected after update  : " + Arrays.toString(expected.getBusView().getBusStream().toArray()));
        // TODO(Guillem) Use compareXml from AbstractConverterTest instead of checkEquals ...
        // First write to xml actual and expected,
        // then compare the resulting XML
        Path pexpected = tmpDir.resolve("expected.xiidm");
        Path pactual = tmpDir.resolve("actual.xiidm");
        NetworkXml.write(expected, pexpected);
        NetworkXml.write(actual, pactual);
        assertTrue(checkEquals(expected, actual));
    }

    private static Network createTestCase() {
        // TODO(Luma) use a test case that corresponds to a solved power flow (voltages, angles, flows on lines, ...)
        return EurostagTutorialExample1Factory.create();
    }

    private static final double TOLERANCE = 1e-14;

    private static boolean checkEquals(Network expected, Network actual) {
        for (Bus sourceBus : expected.getBusView().getBuses()) {
            Bus verifyBus = actual.getBusView().getBus(sourceBus.getId());
            assertEquals(sourceBus.getV(), verifyBus.getV(), TOLERANCE);
            assertEquals(sourceBus.getAngle(), verifyBus.getAngle(), TOLERANCE);
        }
        for (Line lineSource : expected.getLines()) {
            if (checkDifferent(lineSource.getTerminal1(), actual.getLine(lineSource.getId()).getTerminal1())) {
                return false;
            }
            if (checkDifferent(lineSource.getTerminal2(), actual.getLine(lineSource.getId()).getTerminal2())) {
                return false;
            }
        }
        for (DanglingLine sourceDangling : expected.getDanglingLines()) {
            if (checkDifferent(sourceDangling.getTerminal(), actual.getDanglingLine(sourceDangling.getId()).getTerminal())) {
                return false;
            }
        }
        for (HvdcLine sourceHvdcLine : expected.getHvdcLines()) {
            if (checkDifferent(sourceHvdcLine.getConverterStation(HvdcLine.Side.ONE).getTerminal(),
                actual.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(HvdcLine.Side.ONE).getTerminal())) {
                return false;
            }
            if (checkDifferent(sourceHvdcLine.getConverterStation(HvdcLine.Side.TWO).getTerminal(),
                actual.getHvdcLine(sourceHvdcLine.getId()).getConverterStation(HvdcLine.Side.TWO).getTerminal())) {
                return false;
            }
        }
        for (TwoWindingsTransformer sourceTwoWindingsTransformer : expected.getTwoWindingsTransformers()) {
            Terminal verifyTerminal1 = actual.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getTerminal1();
            Terminal verifyTerminal2 = actual.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getTerminal2();
            Terminal sourceTerminal1 = sourceTwoWindingsTransformer.getTerminal1();
            Terminal sourceTerminal2 = sourceTwoWindingsTransformer.getTerminal2();
            if (checkDifferent(sourceTerminal1, verifyTerminal1)) {
                return false;
            }
            if (checkDifferent(sourceTerminal2, verifyTerminal2)) {
                return false;
            }
            PhaseTapChanger sourcePhaseTapChanger = sourceTwoWindingsTransformer.getPhaseTapChanger();
            PhaseTapChanger verifyPhaseTapChanger = actual.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getPhaseTapChanger();
            if ((sourcePhaseTapChanger != null) && (verifyPhaseTapChanger != null)) {
                if (sourcePhaseTapChanger.getTapPosition() != verifyPhaseTapChanger.getTapPosition()) {
                    return false;
                }
            }

            RatioTapChanger sourceRatioTapChanger = sourceTwoWindingsTransformer.getRatioTapChanger();
            RatioTapChanger verifyRatioTapChanger = actual.getTwoWindingsTransformer(sourceTwoWindingsTransformer.getId()).getRatioTapChanger();
            if ((sourceRatioTapChanger != null) && (verifyRatioTapChanger != null)) {
                if (sourceRatioTapChanger.getTapPosition() != verifyRatioTapChanger.getTapPosition()) {
                    return false;
                }
            }
        }
        for (Load sourceLoad : expected.getLoads()) {
            if (checkDifferent(sourceLoad.getTerminal(), actual.getLoad(sourceLoad.getId()).getTerminal())) {
                return false;
            }
        }
        for (Generator sourceGenerator : expected.getGenerators()) {
            if (checkDifferent(sourceGenerator.getTerminal(), actual.getGenerator(sourceGenerator.getId()).getTerminal())) {
                return false;
            }
        }
        for (ShuntCompensator sourceShuntCompensator : expected.getShuntCompensators()) {
            if (sourceShuntCompensator.getSectionCount() != actual.getShuntCompensator(sourceShuntCompensator.getId()).getSectionCount()) {
                return false;
            }
            if (checkDifferent(sourceShuntCompensator.getTerminal(), actual.getShuntCompensator(sourceShuntCompensator.getId()).getTerminal())) {
                return false;
            }
        }
        for (StaticVarCompensator sourceStaticVarCompensator : expected.getStaticVarCompensators()) {
            if (sourceStaticVarCompensator.getRegulationMode() != actual.getStaticVarCompensator(sourceStaticVarCompensator.getId()).getRegulationMode()) {
                return false;
            }
            if (checkDifferent(sourceStaticVarCompensator.getTerminal(), actual.getStaticVarCompensator(sourceStaticVarCompensator.getId()).getTerminal())) {
                return false;
            }
        }
        for (Switch sourceSwitch : expected.getSwitches()) {
            if (sourceSwitch.isOpen() != actual.getSwitch(sourceSwitch.getId()).isOpen()) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkDifferent(Terminal expected, Terminal actual) {
        if (expected.getP() != actual.getP()) {
            return true;
        }
        if (expected.getQ() != actual.getQ()) {
            return true;
        }
        return expected.isConnected() != actual.isConnected();
    }

    private static void reset(Network targetNetwork) {
        for (Bus targetBus : targetNetwork.getBusView().getBuses()) {
            targetBus.setV(0.0);
            targetBus.setAngle(0.0);
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
                targetPhaseTapChanger.setTapPosition(0);
            }

            RatioTapChanger targetRatioTapChanger = targetTwoWindingsTransformer.getRatioTapChanger();
            if (targetRatioTapChanger != null) {
                targetRatioTapChanger.setTapPosition(0);
            }
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

    private static void reset(Terminal target) {
        target.setP(0.0);
        target.setQ(0.0);
        target.disconnect();
    }
}
