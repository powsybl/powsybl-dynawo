package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.PowsyblCoreReportResourceBundle;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.outputvariables.DynawoOutputVariablesBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputVariablesBuilderTest {

    @Test
    void buildFromDynamicId() {
        List<OutputVariable> outputVariables = new DynawoOutputVariablesBuilder()
                .dynamicModelId("BBM_GEN")
                .variable("generator_omegaPu")
                .build();
        assertEquals(1, outputVariables.size());
        OutputVariable variable = outputVariables.getFirst();
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
        OutputVariable variable = outputVariables.getFirst();
        assertEquals("GEN", variable.getModelId());
        assertEquals("generator_omegaPu", variable.getVariableName());
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
                .staticId("GEN")
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
                .staticId("GEN")
                .variables(Collections.emptyList())
                .build();

        assertTrue(outputVariables.isEmpty());
        assertEquals(Collections.emptyList(), outputVariables);
        assertTrue(reportNode.getChildren().stream().anyMatch(child -> "dynawo.dynasim.emptyList".equals(child.getMessageKey())));
    }
}
