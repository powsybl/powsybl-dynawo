/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
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
import com.powsybl.dynawo.DynamicModelsConfigUtils;
import com.powsybl.dynawo.DynawoSimulationConfig;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.builders.ModelInfo;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.events.AbstractEvent;
import com.powsybl.dynawo.models.events.EventActivePowerVariation;
import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.NodeFaultEventBuilder;
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
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@Disabled
public class EventModelsTest extends AbstractDynawoTest {

    //TODO check models in every supported version
    private static final DynawoVersion DYNAWO_VERSION = DynawoVersion.createFromString("1.7.0");
    private static final Network SMIB = Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm")));
    private static final Network EUROSTAG = EurostagTutorialExample1Factory.createWithLFResults();
    private static final Network HVDC = Network.read(new ResourceDataSource("HvdcPowerTransfer", new ResourceSet("/hvdc", "HvdcPowerTransfer.iidm")));

    private DynamicSimulationProvider provider;
    private DynamicSimulationParameters parameters;
    private DynawoSimulationParameters dynawoSimulationParameters;

    @BeforeEach
    void setup() {
        //TODO use docker instead
        provider = new DynawoSimulationProvider(new DynawoSimulationConfig(Path.of("/home/issertiallau/projects/Dynawo/dynaflow-launcher_1.7.0/"), true));
//        provider = new DynawoSimulationProvider();
        parameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(1);
        dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/models/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/smib/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/smib/solvers.par"), "3");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.SIM);
    }


    /**
     *
     * @param network
     * @param eventModelSupplier Event supplier with the event to be tested
     * @param dynamicModelSupplier List of dynamic models to be tested with the event
     * @param defaultModelIds Id list of equipment without model on which the vent should be tested
     */
    @MethodSource("eventModelBuilderProvider")
    @ParameterizedTest
    void testEventModel(Network network,
                        BiFunction<Network, String, AbstractEvent> eventModelSupplier,
                        Function<Network, List<EquipmentBlackBoxModel>> dynamicModelSupplier,
                        List<String> defaultModelIds) {

        SoftAssertions assertions = new SoftAssertions();
        StringBuilder sb = new StringBuilder();
        List<EquipmentBlackBoxModel> equipmentModels = dynamicModelSupplier.apply(network);
        for (EquipmentBlackBoxModel dynamicModel : equipmentModels) {
            AbstractEvent eventModel = eventModelSupplier.apply(network, dynamicModel.getDynamicModelId());
            List<DynamicModel> dynamicModels;
            if (dynamicModel.needMandatoryDynamicModels()) {
//                dynamicModels = new ArrayList<>();
//                dynamicModels.add(dynamicModel);
//                // TODO handle more than SMIB case ?
//                dynamicModels.add(StandardBusBuilder.of(network)
//                        .staticId("VL2_BUS1")
//                        .parameterSetId("Bus")
//                        .build());
//                dynamicModels.add(StandardBusBuilder.of(network)
//                                .staticId("VL3_BUS1")
//                                .parameterSetId("Bus")
//                                .build());
//                dynamicModels.add(LineBuilder.of(network)
//                                .staticId("line1")
//                                .parameterSetId("Line")
//                                .build());
//                dynamicModels.add(LineBuilder.of(network)
//                                .staticId("line2")
//                                .parameterSetId("Line")
//                                .build());
//                dynamicModels.add(TransformerFixedRatioBuilder.of(network)
//                                .staticId("TR")
//                                .parameterSetId("Transformer")
//                                .build());
                //TODO fix
                dynamicModels = List.of();
            } else {
                dynamicModels = List.of(dynamicModel);
            }
            testOneSimulation(network, dynamicModels, eventModel, assertions, sb, dynamicModel.getLib());
        }
        for (String defaultModelId : defaultModelIds) {
            AbstractEvent eventModel = eventModelSupplier.apply(network, defaultModelId);
            testOneSimulation(network, List.of(), eventModel, assertions, sb, "Default " + defaultModelId);
        }
        System.out.println(sb);
        assertions.assertAll();
    }

    private void  testOneSimulation(Network network, List<DynamicModel> dynamicModels, AbstractEvent eventModel,
                                    SoftAssertions assertions, StringBuilder sb, String modelLib) {

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withAllResourceBundlesFromClasspath()
                .withLocale(Locale.US)
                .withMessageTemplate("test")
                .build();
        dynawoSimulationParameters.setNetworkParameters(ParametersXml.load(getResourceAsStream("/smib/network.par"), "8"));
        DynamicSimulationResult result = provider.run(network, (n, r) -> dynamicModels,
                        (n, r) -> List.of(eventModel),
                        OutputVariablesSupplier.empty(), VariantManagerConstants.INITIAL_VARIANT_ID, computationManager,
                        parameters, reportNode)
                .join();
        Optional<ReportNode> modelLog = reportNode.getChildren().stream()
                .filter(r -> r.getMessageKey().equalsIgnoreCase("dynawo.commons.dynawoLog"))
                .flatMap(o -> o.getChildren().stream())
                .filter(m -> m.getMessage().equalsIgnoreCase("model was built successfully"))
                .findFirst();
        assertions.assertThat(modelLog)
                .withFailMessage(() -> "Model %s was not built successfully: %s".formatted(eventModel.getLib() + " on " + modelLib, result.getStatusText()))
                .isNotEmpty();
        if (modelLog.isEmpty()) {
            sb.append(eventModel.getLib())
                    .append(" on ")
                    .append(modelLib)
                    .append(" - ")
                    .append(result.getStatusText())
                    .append("\n");
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

    //TODO add list of id to use for default models to handle
    private static Stream<Arguments> eventModelBuilderProvider() {
        return Stream.of(
                Arguments.of(
                        SMIB,
                        (BiFunction<Network, String, EventModel>) (n, id) -> NodeFaultEventBuilder.of(n)
                                .staticId(id)
                                .startTime(0.1)
                                .faultTime(0.5)
                                .rPu(0.1)
                                .xPu(0.1)
                                .build(),
                        (Function<Network, List<EquipmentBlackBoxModel>>) (n) -> List.of(
                                StandardBusBuilder.of(n)
                                        .staticId("VL1_BUS1")
                                        .parameterSetId("Bus")
                                        .build(),
                                InfiniteBusBuilder.of(n)
                                        .staticId("VL1_BUS1")
                                        .parameterSetId("Bus")
                                        .build()),
                        List.of("VL1_BUS1")
                ),
                Arguments.of(
                        EUROSTAG,
                        (BiFunction<Network, String, EventModel>) (n, id) -> EventActivePowerVariationBuilder.of(n)
                                .staticId(id)
                                .startTime(0.1)
                                .deltaP(0.5)
                                .build(),
                        (Function<Network, List<EquipmentBlackBoxModel>>) (n) -> List.of(
                                BaseLoadBuilder.of(n, "LoadAlphaBeta")
                                        .staticId("LOAD")
                                        .parameterSetId("LAB")
                                        .build(),
                                SynchronizedGeneratorBuilder.of(n, "GeneratorPV")
                                        .staticId("GEN")
                                        .parameterSetId("SynchronizedGenerator")
                                        .build(),
                                SynchronousGeneratorBuilder.of(n, "GeneratorSynchronousFourWindingsGoverPropVRPropInt")
                                        .staticId("GEN")
                                        .parameterSetId("SynchronousGenerator")
                                        .build()),
                        List.of("LOAD", "GEN")
                )
        );
    }
}
