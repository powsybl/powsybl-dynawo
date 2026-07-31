/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.networkextension;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.builders.EquipmentModelBuilder;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.extensions.api.model.DynawoEquipmentModel;
import com.powsybl.dynawo.extensions.api.model.DynawoUnderVoltageModel;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.iidm.network.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Loads {@link DynamicModel} for network extensions
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO handle network extension with services
public class NetworkExtensionSupplier implements DynamicModelsSupplier {

    @Override
    public String getName() {
        return DynawoSimulationProvider.NAME;
    }

    @Override
    public List<DynamicModel> get(Network network, ReportNode reportNode) {
        List<DynamicModel> models = new ArrayList<>();
        ModelConfigsHandler modelConfigsHandler = ModelConfigsHandler.getInstance();

        //TODO handle buses properly
        getModelFromDynawoEquipmentModel(network.getBusBreakerView().getBusStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getGeneratorStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add,
                (gen, ma) -> getUvaModel(gen, modelName -> UnderVoltageAutomationSystemBuilder.of(network, modelName, reportNode), ma));
        getModelFromDynawoEquipmentModel(network.getHvdcLineStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getLineStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getLoadStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getStaticVarCompensatorStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getShuntCompensatorStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getTwoWindingsTransformerStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);

        return models;
    }

    private <I extends Identifiable<I>> void getModelFromDynawoEquipmentModel(Stream<I> equipments,
                                                                              Function<String, EquipmentModelBuilder<I, ?>> builderSupplier,
                                                                              Consumer<DynamicModel> modelAdder) {
        equipments.forEach(eq -> {
            DynawoEquipmentModel<I> ext = eq.getExtensionByName(DynawoEquipmentModel.NAME);
            if (ext != null) {
                EquipmentModelBuilder<I, ?> builder = builderSupplier.apply(ext.getModelName());
                if (builder != null) {
                    modelAdder.accept(builder.equipment(eq)
                            .parameterSetId(ext.getParameterSetId())
                            .build());
                }
            }
        });
    }

    @SafeVarargs
    private <I extends Identifiable<I>> void getModelFromDynawoEquipmentModel(Stream<I> equipments,
                                                                              Function<String, EquipmentModelBuilder<I, ?>> builderSupplier,
                                                                              Consumer<DynamicModel> modelAdder,
                                                                              BiConsumer<I, Consumer<DynamicModel>>... automationSystemModels) {
        equipments.forEach(eq -> {
            DynawoEquipmentModel<I> ext = eq.getExtensionByName(DynawoEquipmentModel.NAME);
            if (ext != null) {
                EquipmentModelBuilder<I, ?> builder = builderSupplier.apply(ext.getModelName());
                if (builder != null) {
                    modelAdder.accept(builder.equipment(eq)
                            .parameterSetId(ext.getParameterSetId())
                            .build());
                    for (BiConsumer<I, Consumer<DynamicModel>> automationSystemModel : automationSystemModels) {
                        automationSystemModel.accept(eq, modelAdder);
                    }
                }
            }
        });
    }

    private void getUvaModel(Generator gen, Function<String, UnderVoltageAutomationSystemBuilder> builderSupplier,
                             Consumer<DynamicModel> modelAdder) {
        DynawoUnderVoltageModel uva = gen.getExtensionByName(DynawoUnderVoltageModel.NAME);
        if (uva != null) {
            UnderVoltageAutomationSystemBuilder builder = builderSupplier.apply(uva.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(uva.getDynamicModelId())
                        .parameterSetId(uva.getParameterSetId())
                        .generator(gen.getId())
                        .build());
            }
        }
    }
}
