/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.*;

import java.util.*;

import static com.powsybl.dynawaltz.builders.BuildersUtil.MEASUREMENT_POINT_TYPE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<TapChangerBlockingAutomationSystemBuilder> {

    public static final String CATEGORY = "TAP_CHANGER_BLOCKING";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);
    private static final String TAP_CHANGER_TYPE = IdentifiableType.TWO_WINDINGS_TRANSFORMER + "/" + IdentifiableType.LOAD;
    private static final String U_MEASUREMENTS_FIELD = "uMeasurements";
    private static final String TRANSFORMER_FIELD = "transformers";

    private final BuilderEquipmentsList<Identifiable<?>> tapChangerEquipments;
    private final BuilderIdListEquipmentList<Identifiable<?>> uMeasurementPoints;

    public static TapChangerBlockingAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static TapChangerBlockingAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new TapChangerBlockingAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static TapChangerBlockingAutomationSystemBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static TapChangerBlockingAutomationSystemBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportLibNotFound(reportNode, TapChangerBlockingAutomationSystemBuilder.class.getSimpleName(), modelName);
            return null;
        }
        return new TapChangerBlockingAutomationSystemBuilder(network, modelConfig, reportNode);
    }

    public static ModelInfos getSupportedModelInfos() {
        return MODEL_CONFIGS;
    }

    protected TapChangerBlockingAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode reportNode) {
        super(network, modelConfig, reportNode);
        tapChangerEquipments = new BuilderEquipmentsList<>(TAP_CHANGER_TYPE, TRANSFORMER_FIELD, true);
        uMeasurementPoints = new BuilderIdListEquipmentList<>(MEASUREMENT_POINT_TYPE, U_MEASUREMENTS_FIELD);
    }

    public TapChangerBlockingAutomationSystemBuilder transformers(String staticId) {
        tapChangerEquipments.addEquipment(staticId, id -> this.getTapChangerEquipment(network, id));
        return self();
    }

    public TapChangerBlockingAutomationSystemBuilder transformers(String... staticIds) {
        tapChangerEquipments.addEquipments(staticIds, id -> this.getTapChangerEquipment(network, id));
        return self();
    }

    public TapChangerBlockingAutomationSystemBuilder transformers(Collection<String> staticIds) {
        tapChangerEquipments.addEquipments(staticIds, id -> this.getTapChangerEquipment(network, id));
        return self();
    }

    private Identifiable<?> getTapChangerEquipment(Network network, String staticId) {
        Identifiable<?> tapChangerEquipment = network.getTwoWindingsTransformer(staticId);
        return tapChangerEquipment != null ? tapChangerEquipment : network.getLoad(staticId);
    }

    public TapChangerBlockingAutomationSystemBuilder uMeasurements(String staticId) {
        uMeasurementPoints.addEquipment(staticId, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    public TapChangerBlockingAutomationSystemBuilder uMeasurements(String... staticIds) {
        uMeasurementPoints.addEquipments(staticIds, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    public TapChangerBlockingAutomationSystemBuilder uMeasurements(Collection<String> staticIds) {
        uMeasurementPoints.addEquipments(staticIds, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    public TapChangerBlockingAutomationSystemBuilder uMeasurements(Collection<String>[] staticIdsArray) {
        uMeasurementPoints.addEquipments(staticIdsArray, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= tapChangerEquipments.checkEquipmentData(reportNode);
        isInstantiable &= uMeasurementPoints.checkEquipmentData(reportNode);
    }

    @Override
    public TapChangerBlockingAutomationSystem build() {
        return isInstantiable() ? new TapChangerBlockingAutomationSystem(dynamicModelId, parameterSetId, tapChangerEquipments.getEquipments(), tapChangerEquipments.getMissingEquipmentIds(), uMeasurementPoints.getEquipments(), getLib()) : null;
    }

    @Override
    protected TapChangerBlockingAutomationSystemBuilder self() {
        return this;
    }
}
