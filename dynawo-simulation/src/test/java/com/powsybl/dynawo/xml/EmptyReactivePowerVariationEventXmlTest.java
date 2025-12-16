package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventReactivePowerVariationBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public class EmptyReactivePowerVariationEventXmlTest extends AbstractDynamicModelXmlTest{
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                .staticId("LOAD")
                .parameterSetId("LOT")
                .build());
        eventModels.add(EventReactivePowerVariationBuilder.of(network)
                .staticId("LOAD")
                .startTime(1)
                .deltaQ(1.1)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "rpv_empty_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        checkReport("""
                + Test DYD
                   + Dynawo models processing
                      EventReactivePowerVariation ReactivePowerVariation_LOAD cannot handle connection with LOAD dynamic model, the model will be skipped
                """);
    }
}
