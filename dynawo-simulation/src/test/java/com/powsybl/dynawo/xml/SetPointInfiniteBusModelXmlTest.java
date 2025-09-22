/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynamicModelsConfigUtils;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class SetPointInfiniteBusModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();

    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                .staticId("GEN")
                .parameterSetId("pq")
                .build());
        dynamicModels.add(TransformerFixedRatioBuilder.of(network)
                .staticId("NGEN_NHV1")
                .parameterSetId("t")
                .build());
        dynamicModels.add(InfiniteBusBuilder.of(network, "InfiniteBus")
                .staticId("NGEN")
                .parameterSetId("ib")
                .build());
        DynamicModelsConfigUtils.mandatoryModelsAdder(network, dynamicModels);
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "set_point_inf_bus_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "set_point_inf_bus_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
