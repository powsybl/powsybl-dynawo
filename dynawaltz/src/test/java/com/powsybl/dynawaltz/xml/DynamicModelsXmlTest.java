/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynamicModelsXmlTest extends DynaWaltzTestUtil {

    @Test
    public void writeDynamicModel() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    public void writeDynamicModelWithLoadsAndOnlyOneFictitiousGenerator() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        dynamicModels.clear();
        network.getGeneratorStream().forEach(gen -> {
            if (gen.getId().equals("GEN6")) {
                dynamicModels.add(new GeneratorFictitious("BBM_" + gen.getId(), gen.getId(), "GF"));
            }
        });

        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd_fictitious.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    public void duplicateStaticId() {
        dynamicModels.clear();
        network.getGeneratorStream().forEach(gen -> {
            if (gen.getId().equals("GEN5") || gen.getId().equals("GEN6")) {
                dynamicModels.add(new GeneratorFictitious("BBM_" + gen.getId(), "duplicateID", "GF"));
            }
        });
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        Exception e = assertThrows(PowsyblException.class, () -> new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, null, null));
        assertEquals("Duplicate staticId: duplicateID", e.getMessage());
    }

}
