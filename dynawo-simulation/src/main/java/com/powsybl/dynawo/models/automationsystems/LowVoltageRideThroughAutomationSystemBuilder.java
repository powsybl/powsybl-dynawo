package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

public class LowVoltageRideThroughAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<LowVoltageRideThroughAutomationSystemBuilder> {
    public static final String CATEGORY = "UNDER_VOLTAGE";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Generator> generator;

    public static LowVoltageRideThroughAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static LowVoltageRideThroughAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new LowVoltageRideThroughAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static LowVoltageRideThroughAutomationSystemBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static LowVoltageRideThroughAutomationSystemBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, CATEGORY, modelName);
            return null;
        }
        return new LowVoltageRideThroughAutomationSystemBuilder(network, modelConfig, reportNode);
    }

    public static ModelInfo getDefaultModelInfo() {
        return MODEL_CONFIGS.getDefaultModelConfig();
    }

    public static Collection<ModelInfo> getSupportedModelInfos() {
        return MODEL_CONFIGS.getModelInfos();
    }

    /**
     * Returns models usable with the given {@link DynawoVersion}
     */
    public static Collection<ModelInfo> getSupportedModelInfos(DynawoVersion dynawoVersion) {
        return MODEL_CONFIGS.getModelInfos(dynawoVersion);
    }

    protected LowVoltageRideThroughAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, parentReportNode);
        generator = new BuilderEquipment<>(IdentifiableType.GENERATOR.toString(), "generator", reportNode);
    }

    public LowVoltageRideThroughAutomationSystemBuilder generator(String staticId) {
        generator.addEquipment(staticId, network::getGenerator);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= generator.checkEquipmentData();
    }

    @Override
    public LowVoltageRideThroughAutomationSystem build() {
        return isInstantiable() ? new LowVoltageRideThroughAutomationSystem(dynamicModelId, parameterSetId, generator.getEquipment(), modelConfig) : null;
    }

    @Override
    protected LowVoltageRideThroughAutomationSystemBuilder self() {
        return this;
    }
}
