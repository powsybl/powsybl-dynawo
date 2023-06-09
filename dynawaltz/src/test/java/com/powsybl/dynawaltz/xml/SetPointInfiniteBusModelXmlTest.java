/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.buses.InfiniteBus;
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class SetPointInfiniteBusModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(new SynchronizedGenerator("BBM_GEN", network.getGenerator("GEN"), "pq", "GeneratorPQ"));
        dynamicModels.add(new TransformerFixedRatio("BBM_TR", network.getTwoWindingsTransformer("NGEN_NHV1"), "t", "TransformerFixedRatio"));
        dynamicModels.add(new InfiniteBus("BBM_BUS", network.getBusBreakerView().getBus("NGEN"), "ib", "InfiniteBus"));
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "set_point_inf_bus_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        validate("parameters.xsd", "set_point_inf_bus_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
