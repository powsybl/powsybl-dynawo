/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.events.EventActivePowerVariation;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousControllable;
import com.powsybl.dynawaltz.models.generators.OmegaRefGeneratorControllable;
import com.powsybl.dynawaltz.models.loads.LoadAlphaBetaControllable;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class ActivePowerVariationEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(new OmegaRefGeneratorControllable("BBM_GENC", network.getGenerator("GEN2"), "GPV", "GeneratorPV"));
        dynamicModels.add(new GeneratorSynchronousControllable("BBM_GENC2", network.getGenerator("GEN3"), "GSTWPR", "GeneratorSynchronousFourWindingsGoverPropVRPropInt"));
        dynamicModels.add(new LoadAlphaBetaControllable("BBM_LOADC", network.getLoad("LOAD2"), "load", "LoadAlphaBeta"));
        eventModels.add(new EventActivePowerVariation(network.getGenerator("GEN"), 1, 1.1));
        eventModels.add(new EventActivePowerVariation(network.getGenerator("GEN2"), 1, 1.2));
        eventModels.add(new EventActivePowerVariation(network.getGenerator("GEN3"), 1, 1.3));
        eventModels.add(new EventActivePowerVariation(network.getLoad("LOAD"), 10, 1.2));
        eventModels.add(new EventActivePowerVariation(network.getLoad("LOAD2"), 10, 1.3));
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "apv_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        validate("parameters.xsd", "apv_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
