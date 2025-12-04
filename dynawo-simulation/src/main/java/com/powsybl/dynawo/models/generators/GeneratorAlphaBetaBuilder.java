package com.powsybl.dynawo.models.generators;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.iidm.network.Network;

import java.util.Collection;

public class GeneratorAlphaBetaBuilder extends AbstractGeneratorBuilder<GeneratorAlphaBetaBuilder> {

    public static final String CATEGORY = "GENERATOR_ALPHABETA";
    private static final ModelConfigs MODEL_CONFIGS = ModelConfigsHandler.getInstance().getModelConfigs(CATEGORY);

    public static GeneratorAlphaBetaBuilder of(Network network) {
        return of(network, ReportNode.NO_OP);
    }

    public static GeneratorAlphaBetaBuilder of(Network network, ReportNode reportNode) {
        return new GeneratorAlphaBetaBuilder(network, MODEL_CONFIGS.getDefaultModelConfig(), reportNode);
    }

    public static GeneratorAlphaBetaBuilder of(Network network, String modelName) {
        return of(network, modelName, ReportNode.NO_OP);
    }

    public static GeneratorAlphaBetaBuilder of(Network network, String modelName, ReportNode reportNode) {
        ModelConfig modelConfig = MODEL_CONFIGS.getModelConfig(modelName);
        if (modelConfig == null) {
            BuilderReports.reportModelNotFound(reportNode, CATEGORY, modelName);
            return null;
        }
        return new GeneratorAlphaBetaBuilder(network, modelConfig, reportNode);
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

    protected GeneratorAlphaBetaBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, parentReportNode);
    }

    @Override
    public GeneratorAlphaBeta build() {
        return isInstantiable() ? new GeneratorAlphaBeta(getEquipment(), parameterSetId, modelConfig) : null;
    }

    @Override
    protected GeneratorAlphaBetaBuilder self() {
        return this;
    }
}
