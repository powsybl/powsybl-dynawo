/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.generators.*;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.lines.LineBuilder;
import com.powsybl.dynawo.models.loads.*;
import com.powsybl.dynawo.models.shunts.BaseShuntBuilder;
import com.powsybl.dynawo.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.ShuntTestCaseFactory;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;


/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@Disabled
public class ModelsTest extends AbstractDynawoTest {

    private static final DynawoVersion DYNAWO_VERSION = DynawoVersion.createFromString("1.7.0");
    private static final Network SMIB = Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm")));
    private static final Network EUROSTAG = EurostagTutorialExample1Factory.create();
    private static final Network HVDC = Network.read(new ResourceDataSource("HvdcPowerTransfer", new ResourceSet("/hvdc", "HvdcPowerTransfer.iidm")));
    //TODO remove skipped model list when fixed (see issue #400)
    private static final List<String> SKIPPED_MODELS = List.of(
            "GeneratorPQPropSignalN",
            "GeneratorPQPropDiagramPQSignalN",
            "GeneratorPVRemoteSignalN",
            "GeneratorPVRemoteDiagramPQSignalN",
            "StaticVarCompensatorPVRemote",
            "StaticVarCompensatorPVPropRemote",
            "StaticVarCompensatorPVRemoteModeHandling",
            "StaticVarCompensatorPVPropRemoteModeHandling",
            "HvdcPQProp",
            "HvdcPQPropDangling",
            "HvdcPQPropDanglingDiagramPQ",
            "HvdcPQPropDiagramPQ",
            "HvdcPQPropDiagramPQEmulation",
            "HvdcPQPropDiagramPQEmulationSet",
            "HvdcPQPropDiagramPQEmulationVariableK",
            "HvdcPQPropEmulation",
            "HvdcPQPropEmulationSet",
            "HvdcPQPropEmulationVariableK"
    );

    private DynamicSimulationProvider provider;
    private DynamicSimulationParameters parameters;

    @BeforeEach
    void setup() {
        //TODO use docker instead
//        provider = new DynawoSimulationProvider(new DynawoSimulationConfig(Path.of("/dynawo"), true));
        provider = new DynawoSimulationProvider();
        parameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(1);
        DynawoSimulationParameters dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/models/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/smib/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/smib/solvers.par"), "3");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.SIM);
    }

    @MethodSource("dynamicModelBuilderProvider")
    @ParameterizedTest
    void testDynamicModel(Network network, Collection<ModelInfo> models, BiFunction<Network, String, List<DynamicModel>> dynamicModelSupplier) {
        SoftAssertions assertions = new SoftAssertions();
        StringBuilder sb = new StringBuilder();
        for (ModelInfo modelInfo : models.stream().filter(m -> !SKIPPED_MODELS.contains(m.name())).toList()) {
            ReportNode reportNode = ReportNode.newRootReportNode()
                    .withAllResourceBundlesFromClasspath()
                    .withLocale(Locale.US)
                    .withMessageTemplate("test")
                    .build();
            DynamicSimulationResult result = provider.run(network, (n, r) -> dynamicModelSupplier.apply(n, modelInfo.name()), EventModelsSupplier.empty(),
                            OutputVariablesSupplier.empty(), VariantManagerConstants.INITIAL_VARIANT_ID, computationManager,
                            parameters, reportNode)
                    .join();
            Optional<ReportNode> modelLog = reportNode.getChildren().stream()
                    .filter(r -> r.getMessageKey().equalsIgnoreCase("dynawo.commons.dynawoLog"))
                    .flatMap(o -> o.getChildren().stream())
                    .filter(m -> m.getMessage().equalsIgnoreCase("model was built successfully"))
                    .findFirst();
            assertions.assertThat(modelLog)
                    .withFailMessage(() -> "Model %s was not built successfully: %s".formatted(modelInfo.name(), result.getStatusText()))
                    .isNotEmpty();
            if (modelLog.isEmpty()) {
                sb.append(modelInfo.name()).append(" - ").append(result.getStatusText()).append("\n");
                StringWriter swReporterAs = new StringWriter();
                reportNode.getChildren().stream()
                        .filter(r -> r.getMessageKey().equalsIgnoreCase("dynawoLog"))
                        .findFirst().ifPresent(rr -> {
                            try {
                                rr.print(swReporterAs);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                sb.append(swReporterAs);
            }

        }
        System.out.println(sb);
        assertions.assertAll();
    }

    private static Stream<Arguments> dynamicModelBuilderProvider() {
        return Stream.of(
                Arguments.of(
                        SMIB,
                        BaseGeneratorBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(BaseGeneratorBuilder.of(n, m)
                                .staticId("SM")
                                .parameterSetId("BaseGenerator")
                                .build())
                ),
                Arguments.of(
                        SMIB,
                        SynchronizedGeneratorBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(SynchronizedGeneratorBuilder.of(n, m)
                                .staticId("SM")
                                .parameterSetId("SynchronizedGenerator")
                                .build())
                ),
                Arguments.of(
                        SMIB,
                        SynchronousGeneratorBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(SynchronousGeneratorBuilder.of(n, m)
                                .staticId("SM")
                                .parameterSetId("SynchronousGenerator")
                                .build())
                ),
                Arguments.of(
                        SMIB,
                        WeccBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(WeccBuilder.of(n, m)
                                .staticId("SM")
                                .parameterSetId("GridForming")
                                .build())
                ),
                Arguments.of(
                        SMIB,
                        GridFormingConverterBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(GridFormingConverterBuilder.of(n, m)
                                .staticId("SM")
                                .parameterSetId("GridForming")
                                .build())
                ),
                Arguments.of(
                        SMIB,
                         SignalNGeneratorBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                         (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(SignalNGeneratorBuilder.of(n, m)
                                  .staticId("SM")
                                  .parameterSetId("GeneratorPVSignalN")
                                  .build())
                ),
                Arguments.of(
                        SMIB,
                        StandardBusBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(
                                StandardBusBuilder.of(n, m)
                                    .staticId("VL1_BUS1")
                                    .parameterSetId("Bus")
                                    .build(),
                                StandardBusBuilder.of(n)
                                        .staticId("VL2_BUS1")
                                        .parameterSetId("Bus")
                                        .build(),
                                StandardBusBuilder.of(n)
                                        .staticId("VL3_BUS1")
                                        .parameterSetId("Bus")
                                        .build(),
                                LineBuilder.of(n)
                                        .staticId("line1")
                                        .parameterSetId("Line")
                                        .build(),
                                LineBuilder.of(n)
                                        .staticId("line2")
                                        .parameterSetId("Line")
                                        .build(),
                                TransformerFixedRatioBuilder.of(n)
                                        .staticId("TR")
                                        .parameterSetId("Transformer")
                                        .build(),
                                BaseGeneratorBuilder.of(n)
                                        .staticId("SM")
                                        .parameterSetId("BaseGenerator")
                                        .build())
                ),
                Arguments.of(
                        SMIB,
                        InfiniteBusBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(
                                InfiniteBusBuilder.of(n, m)
                                        .staticId("VL1_BUS1")
                                        .parameterSetId("Bus")
                                        .build(),
                                StandardBusBuilder.of(n)
                                        .staticId("VL2_BUS1")
                                        .parameterSetId("Bus")
                                        .build(),
                                StandardBusBuilder.of(n)
                                        .staticId("VL3_BUS1")
                                        .parameterSetId("Bus")
                                        .build(),
                                LineBuilder.of(n)
                                        .staticId("line1")
                                        .parameterSetId("Line")
                                        .build(),
                                LineBuilder.of(n)
                                        .staticId("line2")
                                        .parameterSetId("Line")
                                        .build(),
                                TransformerFixedRatioBuilder.of(n)
                                        .staticId("TR")
                                        .parameterSetId("Transformer")
                                        .build(),
                                BaseGeneratorBuilder.of(n)
                                        .staticId("SM")
                                        .parameterSetId("BaseGenerator")
                                        .build())
                ),
                Arguments.of(
                        EUROSTAG,
                        BaseLoadBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(BaseLoadBuilder.of(n, m)
                                .staticId("LOAD")
                                .parameterSetId("LAB")
                                .build())
                ),
                Arguments.of(
                        EUROSTAG,
                        LoadOneTransformerBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadOneTransformerBuilder.of(n, m)
                                .staticId("LOAD")
                                .parameterSetId("LAB")
                                .build())
                ),
                Arguments.of(
                        EUROSTAG,
                        LoadOneTransformerTapChangerBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadOneTransformerTapChangerBuilder.of(n, m)
                                .staticId("LOAD")
                                .parameterSetId("LAB")
                                .build())
                ),
                Arguments.of(
                        EUROSTAG,
                        LoadTwoTransformersBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadTwoTransformersBuilder.of(n, m)
                                .staticId("LOAD")
                                .parameterSetId("LAB")
                                .build())
                ),
                Arguments.of(
                        EUROSTAG,
                        LoadTwoTransformersTapChangersBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadTwoTransformersTapChangersBuilder.of(n, m)
                                .staticId("LOAD")
                                .parameterSetId("LAB")
                                .build())
                ),
                Arguments.of(
                        SMIB,
                        LineBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(
                                LineBuilder.of(n, m)
                                        .staticId("line1")
                                        .parameterSetId("Line")
                                        .build())
                ),
                Arguments.of(
                        EUROSTAG,
                        TransformerFixedRatioBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n,m) -> List.of(
                                TransformerFixedRatioBuilder.of(n, m)
                                        .staticId("NGEN_NHV1")
                                        .parameterSetId("Transformer")
                                        .build())
                ),
                Arguments.of(
                        ShuntTestCaseFactory.create(),
                        BaseShuntBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(BaseShuntBuilder.of(n, m)
                                .staticId("SHUNT")
                                .parameterSetId("Shunt")
                                .build())
                ),
                Arguments.of(
                        SvcTestCaseFactory.create(),
                        BaseStaticVarCompensatorBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(BaseStaticVarCompensatorBuilder.of(n, m)
                                .staticId("SVC2")
                                .parameterSetId("SvarC")
                                .build())
                ),
                Arguments.of(
                        HVDC,
                        HvdcPBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n,m) -> List.of(HvdcPBuilder.of(n, m)
                                    .staticId("HVDC1")
                                    .parameterSetId("Hvdc")
                                    .build())
                ),
                Arguments.of(
                        HVDC,
                        HvdcVscBuilder.getSupportedModelInfos(DYNAWO_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(HvdcVscBuilder.of(n, m)
                                    .staticId("HVDC1")
                                    .parameterSetId("Hvdc")
                                    .build())
                )
        );
    }
}
