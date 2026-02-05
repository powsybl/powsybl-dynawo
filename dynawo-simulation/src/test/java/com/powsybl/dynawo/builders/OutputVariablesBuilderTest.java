package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.PowsyblCoreReportResourceBundle;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void buildFromId() {
        List<OutputVariable> outputVariables = new ArrayList<>();

        outputVariables.addAll(
                new DynawoOutputVariablesBuilder()
                        .id("BBM_GEN")
                        .variable("generator_omegaPu")
                        .build()
        );
        outputVariables.addAll(
                new DynawoOutputVariablesBuilder()
                        .id("GEN")
                        .variables("generator_omegaPu", "generator_PGen")
                        .build()
        );

        assertEquals(3, outputVariables.size());

        // Dynamic ID assertions
        OutputVariable dynamicVar = outputVariables.getFirst();
        assertEquals("BBM_GEN", dynamicVar.getModelId());
        assertEquals("generator_omegaPu", dynamicVar.getVariableName());

        // Static ID assertions
        OutputVariable staticVar1 = outputVariables.get(1);
        assertEquals("GEN", staticVar1.getModelId());
        assertEquals("generator_omegaPu", staticVar1.getVariableName());

        OutputVariable staticVar2 = outputVariables.get(2);
        assertEquals("GEN", staticVar2.getModelId());
        assertEquals("generator_PGen", staticVar2.getVariableName());
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("provideBuilderError")
    void testScriptError(Function<ReportNode, DynawoOutputVariablesBuilder> builderFunction, boolean isInstantiable, String report) throws IOException {
        boolean hasInstance = !builderFunction.apply(reporter).build().isEmpty();
        assertEquals(isInstantiable, hasInstance);
        checkReportNode(report);
    }

    @Test
    void testOutputVariablesNoContent() {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testOutputVariables")
                .build();

        List<OutputVariable> outputVariables = new DynawoOutputVariablesBuilder(reportNode)
                .id("GEN")
                .variables("", "")
                .build();

        assertTrue(outputVariables.isEmpty());
        assertEquals(Collections.emptyList(), outputVariables);
        assertTrue(reportNode.getChildren().stream().anyMatch(child -> "dynawo.dynasim.emptyList".equals(child.getMessageKey())));
    }

    @Test
    void testEmptyListVariables() {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testOutputVariables")
                .build();

        List<OutputVariable> outputVariables = new DynawoOutputVariablesBuilder(reportNode)
                .id("GEN")
                .variables(Collections.emptyList())
                .build();

        assertTrue(outputVariables.isEmpty());
        assertEquals(Collections.emptyList(), outputVariables);
        assertTrue(reportNode.getChildren().stream().anyMatch(child -> "dynawo.dynasim.emptyList".equals(child.getMessageKey())));
    }

    private static Stream<Arguments> provideBuilderError() {
        return Stream.of(
                Arguments.of((Function<ReportNode, DynawoOutputVariablesBuilder>) r ->
                                new DynawoOutputVariablesBuilder(r)
                                        .id("GEN"),
                        false,
                        """
                        + Builder tests
                           'variables' field is not set
                           Output variable GEN cannot be instantiated
                        """),
                Arguments.of((Function<ReportNode, DynawoOutputVariablesBuilder>) r ->
                                new DynawoOutputVariablesBuilder(r)
                                        .id("GEN")
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
