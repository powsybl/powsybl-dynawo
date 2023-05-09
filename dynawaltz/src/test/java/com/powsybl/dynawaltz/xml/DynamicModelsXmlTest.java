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
import com.powsybl.dynawaltz.models.events.DisconnectableEquipment;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.iidm.network.Identifiable;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
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
                dynamicModels.add(new GeneratorFictitious("BBM_" + gen.getId(), gen, "GF"));
            }
        });

        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd_fictitious.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    void duplicateStaticId() {
        dynamicModels.clear();
        network.getGeneratorStream().forEach(gen -> {
            if (gen.getId().equals("GEN5") || gen.getId().equals("GEN6")) {
                dynamicModels.add(new GeneratorFictitious("BBM_" + gen.getId(), network.getGenerator("GEN5"), "GF"));
            }
        });
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        Exception e = assertThrows(PowsyblException.class, () -> new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, null, null));
        assertEquals("Duplicate staticId: GEN5", e.getMessage());
    }

    @Test
    void testDynamicModelGetterException() {
        DynaWaltzContext dc = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());

        // incorrect model
        Exception e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel("GEN5", LineModel.class));
        assertEquals("The model identified by the static id GEN5 does not match the expected model (LineModel)", e.getMessage());

        // default model not implemented
        e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel("unknownID", GeneratorSynchronousModel.class));
        assertEquals("Default model not implemented for GeneratorSynchronousModel", e.getMessage());
    }

    @Test
    void testDynamicModelGetterFromIdentifiableTypeException() {
        DynaWaltzContext dc = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());

        // incorrect model
        Identifiable<?> gen = network.getIdentifiable("GEN5");
        Exception e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel(gen, LineModel.class));
        assertEquals("The model identified by the static id GEN5 does not match the expected model (LineModel)", e.getMessage());

        // dynamic model not found
        Identifiable<?> substation = network.getIdentifiable("P1");
        e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel(substation, DisconnectableEquipment.class));
        assertEquals("No dynamic model associated with SUBSTATION", e.getMessage());

        // requested interface not implemented
        Identifiable<?> transformer = network.getIdentifiable("NGEN_NHV1");
        e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel(transformer, DisconnectableEquipment.class));
        assertEquals("Default model DefaultTransformerModel does not implement DisconnectableEquipment interface", e.getMessage());
    }
}
