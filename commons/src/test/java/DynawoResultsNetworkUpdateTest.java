/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.NetworkXml;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public class DynawoResultsNetworkUpdateTest extends AbstractConverterTest {

    @Test
    public void testUpdate() throws Exception {
        Network expected = createTestCase();
        Network actual = createTestCase();
        reset(actual);
        DynawoResultsNetworkUpdate.update(actual, expected);
        Path pexpected = tmpDir.resolve("expected.xiidm");
        Path pactual = tmpDir.resolve("actual.xiidm");
        NetworkXml.write(expected, pexpected);
        actual.setCaseDate(expected.getCaseDate());
        NetworkXml.write(actual, pactual);
        compareXml(Files.newInputStream(pexpected), Files.newInputStream(pactual));
    }

    private static Network createTestCase() {
        return Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranch", new ResourceSet("/", "SmallBusBranch.xiidm")), null);
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
        if (targetNetwork.getThreeWindingsTransformerCount() > 0) {
            throw new PowsyblException("Three Windings Transformers not supported");
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
        target.setP(Double.NaN);
        target.setQ(Double.NaN);
        target.disconnect();
    }
}
