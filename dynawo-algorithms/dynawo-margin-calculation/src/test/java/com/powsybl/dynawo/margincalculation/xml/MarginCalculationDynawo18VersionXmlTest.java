/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.xml;

import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.margincalculation.MarginCalculationContext;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.xml.AbstractDynamicModelXmlTest;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawo.margincalculation.xml.MarginCalculationConstant.LOAD_VARIATION_AREA_FILENAME;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MarginCalculationDynawo18VersionXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void setupDynawoContext() {
        MarginCalculationParameters parameters = MarginCalculationParameters.load();
        List<LoadsVariation> loadsVariationList = List.of(
                new LoadsVariation(List.of(network.getLoad("LOAD")), 20),
                new LoadsVariation(List.of(network.getLoad("LOAD2"), network.getLoad("LOAD3")), 30));
        context = new MarginCalculationContext.Builder(network, dynamicModels, Collections.emptyList(), loadsVariationList)
                .marginCalculationParameters(parameters)
                .currentVersion(DynawoVersion.createFromString("1.8.0"))
                .build();
    }

    @Override
    protected void addDynamicModels() {
        // no models
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, LOAD_VARIATION_AREA_FILENAME, ((MarginCalculationContext) context).getLoadVariationAreaDydData());
        validate("dyd.xsd", "load_variation_area_dynawo_1_8_dyd.xml", tmpDir.resolve(LOAD_VARIATION_AREA_FILENAME));
    }
}
