/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.models.utils.BlackBoxSupplierUtils;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.xml.DynawoSimulationConstants.JOBS_FILENAME;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    public static final String NAME = "Dynawo";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";

    private final DynawoSimulationConfig config;

    public DynawoSimulationProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynawoSimulationProvider(PlatformConfig platformConfig) {
        this(DynawoSimulationConfig.load(platformConfig));
    }

    public DynawoSimulationProvider(DynawoSimulationConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    public static Command getCommand(DynawoSimulationConfig dynawoSimulationConfig) {
        return new GroupCommandBuilder()
                .id("dyn_fs")
                .subCommand()
                .program(dynawoSimulationConfig.getProgram())
                .args("jobs", JOBS_FILENAME)
                .add()
                .build();
    }

    public static Command getVersionCommand(DynawoSimulationConfig dynawoSimulationConfig) {
        List<String> args = Collections.singletonList("version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(dynawoSimulationConfig.getProgram())
                .args(args)
                .build();
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventModelsSupplier, CurvesSupplier curvesSupplier, String workingVariantId,
                                                          ComputationManager computationManager, DynamicSimulationParameters parameters, ReportNode reportNode) {
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventModelsSupplier);
        Objects.requireNonNull(curvesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(reportNode);

        ReportNode dsReportNode = DynawoSimulationReports.createDynawoSimulationReportNode(reportNode, network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynaMinVersion(execEnv, computationManager, getVersionCommand(config), DynawoSimulationConfig.DYNAWO_LAUNCHER_PROGRAM_NAME, false);
        DynawoSimulationContext context = new DynawoSimulationContext(network, workingVariantId,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, dsReportNode),
                BlackBoxSupplierUtils.getBlackBoxModelList(eventModelsSupplier, network, dsReportNode),
                curvesSupplier.get(network),
                parameters,
                DynawoSimulationParameters.load(parameters),
                reportNode);

        return computationManager.execute(execEnv, new DynawoSimulationHandler(context, getCommand(config), reportNode));
    }
}
