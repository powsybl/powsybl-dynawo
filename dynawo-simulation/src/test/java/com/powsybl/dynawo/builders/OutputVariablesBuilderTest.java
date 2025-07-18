package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.outputvariables.DynawoOutputVariablesBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputVariablesBuilderTest {

    private ReportNode reporter;

    @BeforeEach
    void setup() {
        reporter = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testBuilder")
                .build();
    }

    @Test
    void buildFromDynamicId() {
        List<OutputVariable> outputVariables = new DynawoOutputVariablesBuilder()
                .dynamicModelId("BBM_GEN")
                .variable("generator_omegaPu")
                .build();
        assertEquals(1, outputVariables.size());
        OutputVariable variable = outputVariables.get(0);
        assertEquals("BBM_GEN", variable.getModelId());
        assertEquals("generator_omegaPu", variable.getVariableName());
    }

    @Test
    void buildFromStaticId() {
        List<OutputVariable> outputVariables = new DynawoOutputVariablesBuilder()
                .staticId("GEN")
                .variables("generator_omegaPu", "generator_PGen")
                .build();
        assertEquals(2, outputVariables.size());
        OutputVariable variable = outputVariables.get(0);
        assertEquals("NETWORK", variable.getModelId());
        assertEquals("GEN_generator_omegaPu", variable.getVariableName());
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("provideBuilderError")
    void testScriptError(Function<ReportNode, DynawoOutputVariablesBuilder> builderFunction, boolean isInstantiable, String report) throws IOException {
        boolean hasInstance = !builderFunction.apply(reporter).build().isEmpty();
        assertEquals(isInstantiable, hasInstance);
        checkReportNode(report);
    }

    private static Stream<Arguments> provideBuilderError() {
        return Stream.of(
                Arguments.of((Function<ReportNode, DynawoOutputVariablesBuilder>) r ->
                        new DynawoOutputVariablesBuilder(r)
                            .staticId("GEN")
                            .dynamicModelId("BBM_GEN")
                            .variable("uPu"),
                        true,
                        """
                        + Builder tests
                           Both 'dynamicModelId' and 'staticId' are defined, 'dynamicModelId' will be used
                        """),
                Arguments.of((Function<ReportNode, DynawoOutputVariablesBuilder>) r ->
                        new DynawoOutputVariablesBuilder(r)
                            .staticId("GEN"),
                        false,
                        """
                        + Builder tests
                           'variables' field is not set
                           Output variable GEN cannot be instantiated
                        """),
                Arguments.of((Function<ReportNode, DynawoOutputVariablesBuilder>) r ->
                        new DynawoOutputVariablesBuilder(r)
                            .staticId("GEN")
                            .variables(),
                        false,
                        """
                        + Builder tests
                           'variables' list is empty
                           Output variable GEN cannot be instantiated
                        """)
        );
    }

    private void checkReportNode(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reporter.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
