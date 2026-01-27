package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
class DynawoOutputVariableResolverTest extends DynawoTestUtil {
    private BlackBoxModelSupplier blackBoxModelSupplier;

    @BeforeEach
    void setup() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        dynamicModels.add(TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("BBM_TC_LOAD2")
                .parameterSetId("tc")
                .staticId("LOAD2")
                .side("HIGH_VOLTAGE")
                .build());

        blackBoxModelSupplier = BlackBoxModelSupplier.createFrom(dynamicModels);
    }

    @Test
    void resolveShouldRejectOutputVariableWhenTcbIsNotConnected() {
        // Given
        OutputVariable tcbVar = new DynawoOutputVariablesBuilder()
                .id("BBM_TC_LOAD2")
                .variables("tapPosition")
                .outputType(OutputVariable.OutputType.CURVE)
                .build()
                .get(0);

        Map<OutputVariable.OutputType, List<OutputVariable>> input =
                Map.of(OutputVariable.OutputType.CURVE, List.of(tcbVar));

        DynawoOutputVariableResolver resolver =
                new DynawoOutputVariableResolver(network, blackBoxModelSupplier);

        Map<OutputVariable.OutputType, List<OutputVariable>> result =
                resolver.resolveOutputVariables(input);

        assertEquals(0, result.get(OutputVariable.OutputType.CURVE).size());
    }
}
