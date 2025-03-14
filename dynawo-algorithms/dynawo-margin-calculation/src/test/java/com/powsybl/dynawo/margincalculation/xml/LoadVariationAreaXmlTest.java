/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.xml;

import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.margincalculation.MarginCalculationContext;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.AbstractDynamicModelXmlTest;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawo.margincalculation.xml.MarginCalculationConstant.LOAD_VARIATION_AREA_FILENAME;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class LoadVariationAreaXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        network.getLoad("LOAD").setP0(400);
        network.getLoad("LOAD2").setP0(200);
        Load load3 = network.getLoad("LOAD3");
        load3.setP0(100);
        load3.getTerminal().connect();
    }

    @Override
    protected void addDynamicModels() {
        // no models
    }

    @Override
    protected void setupDynawoContext() {
        MarginCalculationParameters parameters = MarginCalculationParameters.load();
        List<LoadsVariation> loadsVariationList = List.of(
                new LoadsVariation(List.of(network.getLoad("LOAD")), 20),
                new LoadsVariation(List.of(network.getLoad("LOAD2"), network.getLoad("LOAD3")), 30));
        context = new MarginCalculationContext.Builder(network, dynamicModels, Collections.emptyList(), loadsVariationList)
                .marginCalculationParameters(parameters)
                .build();
    }

    @Test
    void writeDyd() throws SAXException, IOException {
        DydXml.write(tmpDir, LOAD_VARIATION_AREA_FILENAME, ((MarginCalculationContext) context).getLoadVariationAreaDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "load_variation_area_dyd.xml", tmpDir.resolve(LOAD_VARIATION_AREA_FILENAME));
        validate("parameters.xsd", "load_variation_area_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
