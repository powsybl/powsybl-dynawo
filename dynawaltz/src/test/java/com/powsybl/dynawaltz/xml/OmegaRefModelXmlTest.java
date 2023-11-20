/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.generators.SynchronousGenerator;
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class OmegaRefModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(new SynchronousGenerator("BBM_GEN_SYNCHRO", network.getGenerator("GEN"), "GSFW", "GeneratorSynchronousFourWindingsPmConstVRNordic"));
        dynamicModels.add(new SynchronizedGenerator("BBM_GEN_PQ", network.getGenerator("GEN2"), "GPQ", "GeneratorPQ"));
        dynamicModels.add(new SynchronousGenerator("BBM_GEN_SYNCHRO2", network.getGenerator("GEN3"), "GSTW", "GeneratorSynchronousThreeWindingsPmConstVRNordic"));
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "omega_ref_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        validate("parameters.xsd", "omega_ref_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
