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
import com.powsybl.dynawo.extensions.api.model.*;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
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
        getModelFromDynawoEquipmentModel(network.getLineStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add,
                (line, ma) -> getOverloadManagementSystemModel(line, modelName -> DynamicOverloadManagementSystemBuilder.of(network, modelName, reportNode), ma),
                (line, ma) -> getTwoLevelOverloadManagementSystemModel(line, modelName -> DynamicTwoLevelOverloadManagementSystemBuilder.of(network, modelName, reportNode), ma));
        getModelFromDynawoEquipmentModel(network.getLoadStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add,
                (load, ma) -> getTapChangerModel(load, modelName -> TapChangerAutomationSystemBuilder.of(network, modelName, reportNode), ma));
        getModelFromDynawoEquipmentModel(network.getStaticVarCompensatorStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getShuntCompensatorStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add);
        getModelFromDynawoEquipmentModel(network.getTwoWindingsTransformerStream(), modelName -> modelConfigsHandler.getEquipmentModelBuilder(network, modelName, reportNode), models::add,
                (tfo, ma) -> getOverloadManagementSystemModel(tfo, modelName -> DynamicOverloadManagementSystemBuilder.of(network, modelName, reportNode), ma),
                (tfo, ma) -> getTwoLevelOverloadManagementSystemModel(tfo, modelName -> DynamicTwoLevelOverloadManagementSystemBuilder.of(network, modelName, reportNode), ma),
                (tfo, ma) -> getPhaseShifterIModel(tfo, modelName -> PhaseShifterIAutomationSystemBuilder.of(network, modelName, reportNode), ma),
                (tfo, ma) -> getPhaseShifterPModel(tfo, modelName -> PhaseShifterPAutomationSystemBuilder.of(network, modelName, reportNode), ma),
                (tfo, ma) -> getPhaseShifterBlockingIModel(tfo, modelName -> PhaseShifterBlockingIAutomationSystemBuilder.of(network, modelName, reportNode), ma));
        return models;
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
                }
            }
            for (BiConsumer<I, Consumer<DynamicModel>> automationSystemModel : automationSystemModels) {
                automationSystemModel.accept(eq, modelAdder);
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

    private void getTapChangerModel(Load load, Function<String, TapChangerAutomationSystemBuilder> builderSupplier,
                             Consumer<DynamicModel> modelAdder) {
        DynawoTapChangerModel tapChanger = load.getExtensionByName(DynawoTapChangerModel.NAME);
        if (tapChanger != null) {
            TapChangerAutomationSystemBuilder builder = builderSupplier.apply(tapChanger.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(tapChanger.getDynamicModelId())
                        .parameterSetId(tapChanger.getParameterSetId())
                        .staticId(load.getId())
                        .side(tapChanger.getSide())
                        .build());
            }
        }
    }

    private void getPhaseShifterIModel(TwoWindingsTransformer tfo, Function<String, PhaseShifterIAutomationSystemBuilder> builderSupplier,
                                    Consumer<DynamicModel> modelAdder) {
        DynawoPhaseShifterIModel phaseShifter = tfo.getExtensionByName(DynawoPhaseShifterIModel.NAME);
        if (phaseShifter != null) {
            PhaseShifterIAutomationSystemBuilder builder = builderSupplier.apply(phaseShifter.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(phaseShifter.getDynamicModelId())
                        .parameterSetId(phaseShifter.getParameterSetId())
                        .transformer(tfo.getId())
                        .build());
            }
        }
    }

    private void getPhaseShifterPModel(TwoWindingsTransformer tfo, Function<String, PhaseShifterPAutomationSystemBuilder> builderSupplier,
                                       Consumer<DynamicModel> modelAdder) {
        DynawoPhaseShifterPModel phaseShifter = tfo.getExtensionByName(DynawoPhaseShifterPModel.NAME);
        if (phaseShifter != null) {
            PhaseShifterPAutomationSystemBuilder builder = builderSupplier.apply(phaseShifter.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(phaseShifter.getDynamicModelId())
                        .parameterSetId(phaseShifter.getParameterSetId())
                        .transformer(tfo.getId())
                        .build());
            }
        }
    }

    private void getPhaseShifterBlockingIModel(TwoWindingsTransformer tfo, Function<String, PhaseShifterBlockingIAutomationSystemBuilder> builderSupplier,
                                       Consumer<DynamicModel> modelAdder) {
        DynawoPhaseShifterBlockingIModel phaseShifterBlocking = tfo.getExtensionByName(DynawoPhaseShifterBlockingIModel.NAME);
        if (phaseShifterBlocking != null) {
            PhaseShifterBlockingIAutomationSystemBuilder builder = builderSupplier.apply(phaseShifterBlocking.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(phaseShifterBlocking.getDynamicModelId())
                        .parameterSetId(phaseShifterBlocking.getParameterSetId())
                        .phaseShifterId(phaseShifterBlocking.getPhaseShifterId())
                        .build());
            }
        }
    }

    private <B extends Branch<B>> void getOverloadManagementSystemModel(B branch, Function<String, DynamicOverloadManagementSystemBuilder> builderSupplier,
                                    Consumer<DynamicModel> modelAdder) {
        DynawoOverloadManagementSystemModel<B> oms = branch.getExtensionByName(DynawoOverloadManagementSystemModel.NAME);
        if (oms != null) {
            DynamicOverloadManagementSystemBuilder builder = builderSupplier.apply(oms.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(oms.getDynamicModelId())
                        .parameterSetId(oms.getParameterSetId())
                        .controlledBranch(branch.getId())
                        .iMeasurement(oms.getIMeasurement())
                        .iMeasurementSide(oms.getIMeasurementSide())
                        .build());
            }
        }
    }

    private <B extends Branch<B>> void getTwoLevelOverloadManagementSystemModel(B branch, Function<String, DynamicTwoLevelOverloadManagementSystemBuilder> builderSupplier,
                                                                       Consumer<DynamicModel> modelAdder) {
        DynawoTwoLevelOverloadManagementSystemModel<B> oms = branch.getExtensionByName(DynawoTwoLevelOverloadManagementSystemModel.NAME);
        if (oms != null) {
            DynamicTwoLevelOverloadManagementSystemBuilder builder = builderSupplier.apply(oms.getModelName());
            if (builder != null) {
                modelAdder.accept(builder.dynamicModelId(oms.getDynamicModelId())
                        .parameterSetId(oms.getParameterSetId())
                        .controlledBranch(branch.getId())
                        .iMeasurement1(oms.getIMeasurement1())
                        .iMeasurement1Side(oms.getIMeasurement1Side())
                        .iMeasurement2(oms.getIMeasurement2())
                        .iMeasurement2Side(oms.getIMeasurement2Side())
                        .build());
            }
        }
    }
}
