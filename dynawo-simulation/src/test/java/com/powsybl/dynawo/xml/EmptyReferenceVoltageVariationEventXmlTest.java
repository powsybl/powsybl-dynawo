package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventReferenceVoltageVariationBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EmptyReferenceVoltageVariationEventXmlTest extends AbstractDynamicModelXmlTest{

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network, "GeneratorPVFixed")
                .staticId("GEN")
                .parameterSetId("g")
                .build());
        eventModels.add(EventReferenceVoltageVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaU(1.1)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "upv_empty_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        checkReport("""
                + Test DYD
                   + Dynawo models processing
                      EventReferenceVoltageVariation ReferenceVoltageVariation_GEN cannot handle connection with GENERATOR dynamic model, the model will be skipped
                """);
    }
}
