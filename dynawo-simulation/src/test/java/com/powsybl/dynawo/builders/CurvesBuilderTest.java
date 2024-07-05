package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawo.curves.DynawoCurve;
import com.powsybl.dynawo.curves.DynawoCurvesBuilder;
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

class CurvesBuilderTest {

    private ReportNode reporter;

    @BeforeEach
    void setup() {
        reporter = ReportNode.newRootReportNode().withMessageTemplate("builderTests", "Builder tests").build();
    }

    @Test
    void buildFromDynamicId() {
        List<Curve> curveList = new DynawoCurvesBuilder()
                .dynamicModelId("BBM_GEN")
                .variable("generator_omegaPu")
                .build();
        assertEquals(1, curveList.size());
        DynawoCurve curve = (DynawoCurve) curveList.get(0);
        assertEquals("BBM_GEN", curve.getModelId());
        assertEquals("generator_omegaPu", curve.getVariable());
    }

    @Test
    void buildFromStaticId() {
        List<Curve> curveList = new DynawoCurvesBuilder()
                .staticId("GEN")
                .variables("generator_omegaPu", "generator_PGen")
                .build();
        assertEquals(2, curveList.size());
        DynawoCurve curve = (DynawoCurve) curveList.get(0);
        assertEquals("NETWORK", curve.getModelId());
        assertEquals("GEN_generator_omegaPu", curve.getVariable());
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("provideBuilderError")
    void testScriptError(Function<ReportNode, DynawoCurvesBuilder> builderFunction, boolean isInstantiable, String report) throws IOException {
        boolean hasInstance = !builderFunction.apply(reporter).build().isEmpty();
        assertEquals(isInstantiable, hasInstance);
        checkReportNode(report);
    }

    private static Stream<Arguments> provideBuilderError() {
        return Stream.of(
                Arguments.of((Function<ReportNode, DynawoCurvesBuilder>) r ->
                        new DynawoCurvesBuilder(r)
                            .staticId("GEN")
                            .dynamicModelId("BBM_GEN")
                            .variable("uPu"),
                        true,
                        """
                        + Builder tests
                           Both 'dynamicModelId' and 'staticId' are defined, 'dynamicModelId' will be used
                        """),
                Arguments.of((Function<ReportNode, DynawoCurvesBuilder>) r ->
                        new DynawoCurvesBuilder(r)
                            .staticId("GEN"),
                        false,
                        """
                        + Builder tests
                           'variables' field is not set
                           Curve GEN cannot be instantiated
                        """),
                Arguments.of((Function<ReportNode, DynawoCurvesBuilder>) r ->
                        new DynawoCurvesBuilder(r)
                            .staticId("GEN")
                            .variables(),
                        false,
                        """
                        + Builder tests
                           'variables' list is empty
                           Curve GEN cannot be instantiated
                        """)
        );
    }

    private void checkReportNode(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reporter.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
