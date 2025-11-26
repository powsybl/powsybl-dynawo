package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

public class HighVoltageRideThroughAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<HighVoltageRideThroughAutomationSystemBuilder> {
    public static final String CATEGORY = "OVER_VOLTAGE";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Generator> generator;

    public static HighVoltageRideThroughAutomationSystemBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static HighVoltageRideThroughAutomationSystemBuilder of(Network network, ReportNode reportNode) {
        return new HighVoltageRideThroughAutomationSystemBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static HighVoltageRideThroughAutomationSystemBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static HighVoltageRideThroughAutomationSystemBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, CATEGORY, modelName);
            return null;
        }
        return new HighVoltageRideThroughAutomationSystemBuilder(network, modelConfig, reportNode);
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

    protected HighVoltageRideThroughAutomationSystemBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, parentReportNode);
        generator = new BuilderEquipment<>(IdentifiableType.GENERATOR.toString(), "generator", reportNode);
    }

    public HighVoltageRideThroughAutomationSystemBuilder generator(String staticId) {
        generator.addEquipment(staticId, network::getGenerator);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= generator.checkEquipmentData();
    }

    @Override
    public HighVoltageRideThroughAutomationSystem build() {
        return isInstantiable() ? new HighVoltageRideThroughAutomationSystem(dynamicModelId, parameterSetId, generator.getEquipment(), modelConfig) : null;
    }

    @Override
    protected HighVoltageRideThroughAutomationSystemBuilder self() {
        return this;
    }
}
