/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.ModelBuilder;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.suppliers.DynawoSupplierJsonDeserializer;
import com.powsybl.dynawo.suppliers.Property;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawo.DynawoSimulationReports.createDynawoModelSupplierReportNode;

/**
 * Instantiates an {@link DynamicModelConfig} list from {@link DynamicModelConfig} or JSON input
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoModelsSupplier implements DynamicModelsSupplier {

    private static final String PARAMETER_ID_FIELD = "parameterSetId";

    private final DynamicModelConfigs dynamicModelConfigs;
    private final ModelResolvers resolvers = new ModelResolvers();

    public static DynawoModelsSupplier load(InputStream is) {
        return new DynawoModelsSupplier(new DynawoSupplierJsonDeserializer().deserialize(is));
    }

    public static DynawoModelsSupplier load(Path path) {
        return new DynawoModelsSupplier(new DynawoSupplierJsonDeserializer().deserialize(path));
    }

    public DynawoModelsSupplier(DynamicModelConfigs dynamicModelConfigs) {
        this.dynamicModelConfigs = dynamicModelConfigs;
    }

    public DynawoModelsSupplier(List<DynamicModelConfig> dynamicModelConfigList) {
        this.dynamicModelConfigs = new DynamicModelConfigs(dynamicModelConfigList);
    }

    @Override
    public String getName() {
        return DynawoSimulationProvider.NAME;
    }

    @Override
    public List<DynamicModel> get(Network network, ReportNode reportNode) {
        ReportNode supplierReportNode = createDynawoModelSupplierReportNode(reportNode);
        List<DynamicModel> models = Stream.concat(dynamicModelConfigs.dynamicModelConfigList().stream(), getAlternativeModelsStream(network, reportNode))
                .map(dynamicModelConfig -> buildDynamicModel(dynamicModelConfig, network, supplierReportNode))
                .filter(Objects::nonNull)
                .toList();

        if (dynamicModelConfigs.hasMultipleModels()) {
            Stream<DynamicModel> multiModelStream = getMultipleModelsStream(network, models, reportNode)
                    .map(dynamicModelConfig -> buildDynamicModel(dynamicModelConfig, network, supplierReportNode))
                    .filter(Objects::nonNull);
            return Stream.concat(models.stream(), multiModelStream).toList();
        }
        return models;
    }

    private Stream<DynamicModelConfig> getMultipleModelsStream(Network network, List<DynamicModel> models, ReportNode reportNode) {
        Map<IdentifiableType, Set<String>> mappedEquipmentIds = models.stream().filter(EquipmentBlackBoxModel.class::isInstance)
                .map(dm -> ((EquipmentBlackBoxModel) dm).getEquipment())
                .collect(Collectors.groupingBy(
                        Identifiable::getType,
                        () -> new EnumMap<>(IdentifiableType.class),
                        Collectors.mapping(Identifiable::getId, Collectors.toSet())));
        Set<String> missingResolvers = new HashSet<>();
        return dynamicModelConfigs.dynamicMultipleModelList().stream()
                .flatMap(mm -> {
                    MultipleModelResolver resolver = resolvers.getMultipleModelResolver(mm.modelResolverName());
                    if (resolver == null) {
                        if (missingResolvers.add(mm.modelResolverName())) {
                            DynawoSimulationReports.reportModelResolverNotFound(reportNode, mm.modelResolverName());
                        }
                        return Stream.empty();
                    }
                    Set<String> eqIds = mappedEquipmentIds.get(mm.identifiableType());
                    if (eqIds == null) {
                        return network.getIdentifiableStream(mm.identifiableType())
                                .map(i -> resolver.resolveMultipleModels(i, reportNode));
                    }
                    return network.getIdentifiableStream(mm.identifiableType())
                            .filter(eq -> !eqIds.contains(eq.getId()))
                            .map(i -> resolver.resolveMultipleModels(i, reportNode));
                });
    }

    private Stream<DynamicModelConfig> getAlternativeModelsStream(Network network, ReportNode reportNode) {
        if (dynamicModelConfigs.hasDynamicAlternativeModels()) {
            Set<String> missingResolvers = new HashSet<>();
            return dynamicModelConfigs.dynamicAlternativeModelsConfiglist().stream()
                    .map(am -> {
                        ModelResolver resolver = resolvers.getModelResolver(am.modelResolverName());
                        if (resolver == null) {
                            if (missingResolvers.add(am.modelResolverName())) {
                                DynawoSimulationReports.reportModelResolverNotFound(reportNode, am.modelResolverName());
                            }
                            return null;
                        }
                        return resolver.resolveAlternativeModels(network, am.alternativeModelConfigs(), am.groupType(),
                                am.properties(), reportNode);
                    }
                    )
                    .filter(Objects::nonNull);
        }
        return Stream.empty();
    }

    private static DynamicModel buildDynamicModel(DynamicModelConfig dynamicModelConfig, Network network, ReportNode reportNode) {
        ModelBuilder<DynamicModel> builder = ModelConfigsHandler.getInstance().getModelBuilder(network, dynamicModelConfig.model(), reportNode);
        if (builder != null) {
            Class<? extends ModelBuilder> builderClass = builder.getClass();
            dynamicModelConfig.properties().forEach(p -> invokeMethod(builderClass, builder, p));
            invokeParameterIdMethod(builderClass, builder, getParameterSetId(dynamicModelConfig, builder.getModelId()));
            return builder.build();
        }
        return null;
    }

    private static void invokeMethod(Class<? extends ModelBuilder> builderClass, ModelBuilder<DynamicModel> builder, Property property) {
        try {
            builderClass.getMethod(property.name(), property.propertyClass()).invoke(builder, property.value());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PowsyblException(String.format("Method %s not found for parameter %s on builder %s", property.name(), property.value(), builderClass.getSimpleName()), e);
        }
    }

    private static void invokeParameterIdMethod(Class<? extends ModelBuilder> builderClass, ModelBuilder<DynamicModel> builder, String parameterSetId) {
        try {
            builderClass.getMethod(DynawoModelsSupplier.PARAMETER_ID_FIELD, String.class).invoke(builder, parameterSetId);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PowsyblException(String.format("Method %s not found for parameter %s on builder %s", DynawoModelsSupplier.PARAMETER_ID_FIELD, parameterSetId, builderClass.getSimpleName()), e);
        }
    }

    private static String getParameterSetId(DynamicModelConfig config, String modelId) {
        return switch (config.groupType()) {
            case FIXED -> config.group();
            case PREFIX -> config.group() + modelId;
            case SUFFIX -> modelId + config.group();
        };
    }
}
