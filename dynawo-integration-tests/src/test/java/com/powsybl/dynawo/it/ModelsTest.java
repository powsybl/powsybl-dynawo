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
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.DynawoSimulationConfig;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.builders.VersionBound;
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
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import com.powsybl.iidm.network.test.ShuntTestCaseFactory;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.powsybl.commons.report.ReportNode.NO_OP;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelsTest extends AbstractDynawoTest {

    private Network network;
    private DynamicSimulationProvider provider;
    private DynamicSimulationParameters parameters;
    private DynawoSimulationParameters dynawoSimulationParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        network = Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm")));
        provider = new DynawoSimulationProvider(new DynawoSimulationConfig(Path.of("/dynawo"), false));
        parameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(1);
        dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/models/models.par"));
//        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
//        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/smib/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/smib/solvers.par"), "1");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA);
    }

    @MethodSource("dynamicModelBuilderProvider")
    @ParameterizedTest
    void testDynamicModel(Network network, Collection<ModelInfo> models, BiFunction<Network, String, List<DynamicModel>> dynamicModelSupplier) {
        StringBuilder sb = new StringBuilder();
        for(ModelInfo modelInfo : models.stream().sorted(Comparator.comparing(ModelInfo::name)).toList()) {
            List<DynamicModel> modelsList = modelInfo.name().contains("Dangling") ? List.of() : dynamicModelSupplier.apply(network, modelInfo.name());
            DynamicSimulationResult result = provider.run(network, (n, r) -> modelsList, EventModelsSupplier.empty(),
                            CurvesSupplier.empty(), VariantManagerConstants.INITIAL_VARIANT_ID, computationManager,
                            parameters, NO_OP)
                    .join();

            if (result.getStatus() == DynamicSimulationResult.Status.FAILURE) {
                sb.append(String.format("Model %s doesn't work: %s\n",
                        modelInfo.name(), result.getStatusText()));
            }
        }
        System.out.println(sb);
    }

    private static Stream<Arguments> dynamicModelBuilderProvider() {
        Network smib = Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm")));

        return Stream.of(
//                Arguments.of(
//                        Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm"))),
//                        GeneratorFictitiousBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(GeneratorFictitiousBuilder.of(n, m)
//                                .staticId("SM")
//                                .parameterSetId("SynchronizedGenerator")
//                                .build())
//                ),
//                Arguments.of(
//                        Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm"))),
//                        SynchronizedGeneratorBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(SynchronizedGeneratorBuilder.of(n, m)
//                                .staticId("SM")
//                                .parameterSetId("SynchronizedGenerator")
//                                .build())
//                )
//                Arguments.of(
//                        Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm"))),
//                        SynchronousGeneratorBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(SynchronousGeneratorBuilder.of(n, m)
//                                .staticId("SM")
//                                .parameterSetId("SynchronousGenerator")
//                                .build())
//                ),
//                Arguments.of(
//                        Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm"))),
//                        WeccBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(WeccBuilder.of(n, m)
//                                .staticId("SM")
//                                .parameterSetId("SynchronousGenerator")
//                                .build())
//                ),
//                Arguments.of(
//                        Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm"))),
//                        GridFormingConverterBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(GridFormingConverterBuilder.of(n, m)
//                                .staticId("SM")
//                                .parameterSetId("GridForming")
//                                .build())
//                ),
//                Arguments.of(
//                     smib,
//                      SignalNGeneratorBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                     (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(SignalNGeneratorBuilder.of(n, m)
//                              .staticId("SM")
//                              .parameterSetId("GeneratorPVSignalN")
//                              .build())
//                ),
//                Arguments.of(
//                        smib,
//                        StandardBusBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(
//                                StandardBusBuilder.of(n, m)
//                                    .staticId("VL1_BUS1")
//                                    .parameterSetId("Bus")
//                                    .build(),
//                                LineBuilder.of(n)
//                                    .staticId("line1")
//                                    .parameterSetId("Line")
//                                    .build(),
//                                LineBuilder.of(n)
//                                        .staticId("line2")
//                                        .parameterSetId("Line")
//                                        .build())
//                ),
//                Arguments.of(
//                        smib,
//                        InfiniteBusBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(
//                                InfiniteBusBuilder.of(n, m)
//                                        .staticId("VL1_BUS1")
//                                        .parameterSetId("Bus")
//                                        .build(),
//                                LineBuilder.of(n)
//                                        .staticId("line1")
//                                        .parameterSetId("Line")
//                                        .build(),
//                                LineBuilder.of(n)
//                                        .staticId("line2")
//                                        .parameterSetId("Line")
//                                        .build())
//                )
//                Arguments.of(
//                        EurostagTutorialExample1Factory.create(),
//                        BaseLoadBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(BaseLoadBuilder.of(n, m)
//                                .staticId("LOAD")
//                                .parameterSetId("LAB")
//                                .build())
//                ),
//                Arguments.of(
//                        EurostagTutorialExample1Factory.create(),
//                        LoadOneTransformerBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadOneTransformerBuilder.of(n, m)
//                                .staticId("LOAD")
//                                .parameterSetId("LAB")
//                                .build())
//                ),
//                Arguments.of(
//                        EurostagTutorialExample1Factory.create(),
//                        LoadOneTransformerTapChangerBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadOneTransformerTapChangerBuilder.of(n, m)
//                                .staticId("LOAD")
//                                .parameterSetId("LAB")
//                                .build())
//                ),
//                Arguments.of(
//                        EurostagTutorialExample1Factory.create(),
//                        LoadTwoTransformersBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadTwoTransformersBuilder.of(n, m)
//                                .staticId("LOAD")
//                                .parameterSetId("LAB")
//                                .build())
//                ),
//                Arguments.of(
//                        EurostagTutorialExample1Factory.create(),
//                        LoadTwoTransformersTapChangersBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(LoadTwoTransformersTapChangersBuilder.of(n, m)
//                                .staticId("LOAD")
//                                .parameterSetId("LAB")
//                                .build())
//                )
//                Arguments.of(
//                        smib,
//                        LineBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(
//                                LineBuilder.of(n,m)
//                                        .staticId("line1")
//                                        .parameterSetId("Line")
//                                        .build())
//                ),
//                Arguments.of(
//                        smib,
//                        TransformerFixedRatioBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  List.of(
//                                TransformerFixedRatioBuilder.of(n,m)
//                                        .staticId("TR")
//                                        .parameterSetId("Transformer")
//                                        .build())
//                )
//                Arguments.of(
//                        ShuntTestCaseFactory.create(),
//                        BaseShuntBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
//                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> List.of(BaseShuntBuilder.of(n, m)
//                                .staticId("SHUNT")
//                                .parameterSetId("Shunt")
//                                .build())
//                ),
                Arguments.of(
                        Network.read(new ResourceDataSource("HvdcPowerTransfer", new ResourceSet("/hvdc", "HvdcPowerTransfer.iidm"))),
                        HvdcPBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) ->  {
                            HvdcPBuilder builder = HvdcPBuilder.of(n, m)
                                    .staticId("HVDC1")
                                    .parameterSetId("Hvdc");
                            if (m.contains("Dangling")) {
                                builder.dangling(TwoSides.ONE);
                            }
                            return List.of(builder.build());
                        }
                ),
                Arguments.of(
                        Network.read(new ResourceDataSource("HvdcPowerTransfer", new ResourceSet("/hvdc", "HvdcPowerTransfer.iidm"))),
                        HvdcVscBuilder.getSupportedModelInfos(VersionBound.MODEL_DEFAULT_MIN_VERSION),
                        (BiFunction<Network, String, List<DynamicModel>>) (n, m) -> {
                            HvdcVscBuilder builder = HvdcVscBuilder.of(n, m)
                                    .staticId("HVDC1")
                                    .parameterSetId("Hvdc");
                            if (m.contains("Dangling")) {
                                builder.dangling(TwoSides.ONE);
                            }
                            return List.of(builder.build());
                        }
                )
        );
    }
}
