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
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.models.loads.BaseLoad;
import com.powsybl.iidm.network.Identifiable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelsXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    void writeDynamicModelWithLoadsAndOnlyOneFictitiousGenerator() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        dynamicModels.clear();
        network.getGeneratorStream().forEach(gen -> {
            if (gen.getId().equals("GEN6")) {
                dynamicModels.add(new GeneratorFictitious("BBM_" + gen.getId(), gen, "GF", "GeneratorFictitious"));
            }
        });

        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd_fictitious.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    void duplicateStaticId() {
        dynamicModels.clear();
        BaseLoad load1 = new BaseLoad("BBM_LOAD", network.getLoad("LOAD"), "lab", "LoadAlphaBeta");
        BaseLoad load2 = new BaseLoad("BBM_LOAD2", network.getLoad("LOAD"), "lab", "LoadAlphaBeta");
        dynamicModels.add(load1);
        dynamicModels.add(load2);
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load1);
    }

    @Test
    void duplicateDynamicId() {
        dynamicModels.clear();
        BaseLoad load1 = new BaseLoad("BBM_LOAD", network.getLoad("LOAD"), "lab", "LoadAlphaBeta");
        BaseLoad load2 = new BaseLoad("BBM_LOAD", network.getLoad("LOAD2"), "lab", "LoadAlphaBeta");
        dynamicModels.add(load1);
        dynamicModels.add(load2);
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load1);
    }

    @Test
    void testIncorrectModelException() {
        DynaWaltzContext dc = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());
        Identifiable<?> gen = network.getIdentifiable("GEN5");
        Exception e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel(gen, LineModel.class, true));
        assertEquals("The model identified by the static id GEN5 does not match the expected model (LineModel)", e.getMessage());
    }
}
