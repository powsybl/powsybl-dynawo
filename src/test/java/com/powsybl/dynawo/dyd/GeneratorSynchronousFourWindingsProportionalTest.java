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
        assertEquals("1", parameters.getGeneratorExcitationPu());
        assertEquals("0", parameters.getGeneratorMdPuEfd());
        assertEquals("0", parameters.getGeneratorDPu());
        assertEquals("5.4000000000000004", parameters.getGeneratorH());
        assertEquals("0.0027959999999999999", parameters.getGeneratorRaPu());
        assertEquals("0.20200000000000001", parameters.getGeneratorXlPu());
        assertEquals("2.2200000000000002", parameters.getGeneratorXdPu());
        assertEquals("0.38400000000000001", parameters.getGeneratorXpdPu());
        assertEquals("0.26400000000000001", parameters.getGeneratorXppdPu());
        assertEquals("8.0939999999999994", parameters.getGeneratorTpd0());
        assertEquals("0.080000000000000002", parameters.getGeneratorTppd0());
        assertEquals("2.2200000000000002", parameters.getGeneratorXqPu());
        assertEquals("0.39300000000000002", parameters.getGeneratorXpqPu());
        assertEquals("0.26200000000000001", parameters.getGeneratorXppqPu());
        assertEquals("1.5720000000000001", parameters.getGeneratorTpq0());
        assertEquals("0.084000000000000005", parameters.getGeneratorTppq0());
        assertEquals("24", parameters.getGeneratorUNom());
        assertEquals("1211", parameters.getGeneratorSNom());
        assertEquals("1090", parameters.getGeneratorPNomTurb());
        assertEquals("1090", parameters.getGeneratorPNomAlt());
        assertEquals("1211", parameters.getGeneratorSnTfo());
        assertEquals("69", parameters.getGeneratorUNomHV());
        assertEquals("24", parameters.getGeneratorUNomLV());
        assertEquals("69", parameters.getGeneratorUBaseHV());
        assertEquals("24", parameters.getGeneratorUBaseLV());
        assertEquals("0.0", parameters.getGeneratorRTfPu());
        assertEquals("0.1", parameters.getGeneratorXTfPu());
        assertEquals("0", parameters.getVoltageRegulatorLagEfdMax());
        assertEquals("0", parameters.getVoltageRegulatorLagEfdMin());
        assertEquals("-5", parameters.getVoltageRegulatorEfdMinPu());
        assertEquals("5", parameters.getVoltageRegulatorEfdMaxPu());
        assertEquals("0.8", parameters.getVoltageRegulatorUsRefMinPu());
        assertEquals("1.2", parameters.getVoltageRegulatorUsRefMaxPu());
        assertEquals("20", parameters.getVoltageRegulatorGain());
        assertEquals("5", parameters.getGovernorKGover());
        assertEquals("0", parameters.getGovernorPMin());
        assertEquals("1090", parameters.getGovernorPMax());
        assertEquals("1090", parameters.getGovernorPNom());
        assertEquals("0", parameters.getURefValueIn());
        assertEquals("0", parameters.getPmValueIn());
    }
}
