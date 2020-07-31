/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.powsybl.dynawo.dyd.GeneratorSynchronousFourWindingsProportionalRegulations.Parameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GeneratorSynchronousFourWindingsProportionalTest extends DynamicModelUtilTest {

    @Test
    public void test() {
        GeneratorSynchronousFourWindingsProportionalRegulations.Parameters parameters = (Parameters) GeneratorSynchronousFourWindingsProportionalRegulations.Parameters.load(parametersDatabase, "GeneratorSynchronousFourWindingsProportionalRegulations");
        assertEquals(1, parameters.getGeneratorExcitationPu());
        assertEquals(0, parameters.getGeneratorMdPuEfd(), 0.0);
        assertEquals(0, parameters.getGeneratorDPu(), 0.0);
        assertEquals(5.4000000000000004, parameters.getGeneratorH(), 0.0);
        assertEquals(0.0027959999999999999, parameters.getGeneratorRaPu(), 0.0);
        assertEquals(0.20200000000000001, parameters.getGeneratorXlPu(), 0.0);
        assertEquals(2.2200000000000002, parameters.getGeneratorXdPu(), 0.0);
        assertEquals(0.38400000000000001, parameters.getGeneratorXpdPu(), 0.0);
        assertEquals(0.26400000000000001, parameters.getGeneratorXppdPu(), 0.0);
        assertEquals(8.0939999999999994, parameters.getGeneratorTpd0(), 0.0);
        assertEquals(0.080000000000000002, parameters.getGeneratorTppd0(), 0.0);
        assertEquals(2.2200000000000002, parameters.getGeneratorXqPu(), 0.0);
        assertEquals(0.39300000000000002, parameters.getGeneratorXpqPu(), 0.0);
        assertEquals(0.26200000000000001, parameters.getGeneratorXppqPu(), 0.0);
        assertEquals(1.5720000000000001, parameters.getGeneratorTpq0(), 0.0);
        assertEquals(0.084000000000000005, parameters.getGeneratorTppq0(), 0.0);
        assertEquals(24, parameters.getGeneratorUNom(), 0.0);
        assertEquals(1211, parameters.getGeneratorSNom(), 0.0);
        assertEquals(1090, parameters.getGeneratorPNomTurb(), 0.0);
        assertEquals(1090, parameters.getGeneratorPNomAlt(), 0.0);
        assertEquals(1211, parameters.getGeneratorSnTfo(), 0.0);
        assertEquals(69, parameters.getGeneratorUNomHV(), 0.0);
        assertEquals(24, parameters.getGeneratorUNomLV(), 0.0);
        assertEquals(69, parameters.getGeneratorUBaseHV(), 0.0);
        assertEquals(24, parameters.getGeneratorUBaseLV(), 0.0);
        assertEquals(0.0, parameters.getGeneratorRTfPu(), 0.0);
        assertEquals(0.1, parameters.getGeneratorXTfPu(), 0.0);
        assertEquals(0, parameters.getVoltageRegulatorLagEfdMax(), 0.0);
        assertEquals(0, parameters.getVoltageRegulatorLagEfdMin(), 0.0);
        assertEquals(-5, parameters.getVoltageRegulatorEfdMinPu(), 0.0);
        assertEquals(5, parameters.getVoltageRegulatorEfdMaxPu(), 0.0);
        assertEquals(0.8, parameters.getVoltageRegulatorUsRefMinPu(), 0.0);
        assertEquals(1.2, parameters.getVoltageRegulatorUsRefMaxPu(), 0.0);
        assertEquals(20, parameters.getVoltageRegulatorGain(), 0.0);
        assertEquals(5, parameters.getGovernorKGover(), 0.0);
        assertEquals(0, parameters.getGovernorPMin(), 0.0);
        assertEquals(1090, parameters.getGovernorPMax(), 0.0);
        assertEquals(1090, parameters.getGovernorPNom(), 0.0);
        assertEquals(0, parameters.getURefValueIn(), 0.0);
        assertEquals(0, parameters.getPmValueIn(), 0.0);
    }
}
