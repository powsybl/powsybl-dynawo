/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawaltz.models.utils.BlackBoxSupplierUtils;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawo.commons.dynawologs.CsvLogParser;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.dynawo.commons.timeline.CsvTimeLineParser;
import com.powsybl.dynawo.commons.timeline.TimeLineParser;
import com.powsybl.dynawo.commons.timeline.XmlTimeLineParser;
import com.powsybl.iidm.network.Network;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.JOBS_FILENAME;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynaWaltzProvider implements DynamicSimulationProvider {

    public static final String NAME = "DynaWaltz";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawaltz_";

    private final DynaWaltzConfig config;

    public DynaWaltzProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynaWaltzProvider(PlatformConfig platformConfig) {
        this(DynaWaltzConfig.load(platformConfig));
    }

    public DynaWaltzProvider(DynaWaltzConfig config) {
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

    public static Command getCommand(DynaWaltzConfig dynaWaltzConfig) {
        return new GroupCommandBuilder()
                .id("dyn_fs")
                .subCommand()
                .program(dynaWaltzConfig.getProgram())
                .args("jobs", JOBS_FILENAME)
                .add()
                .build();
    }

    public static Command getVersionCommand(DynaWaltzConfig dynaWaltzConfig) {
        List<String> args = Collections.singletonList("version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(dynaWaltzConfig.getProgram())
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

        ReportNode dsReportNode = DynawaltzReports.createDynaWaltzReportNode(reportNode, network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynaMinVersion(execEnv, computationManager, getVersionCommand(config), DynaWaltzConfig.DYNAWALTZ_LAUNCHER_PROGRAM_NAME, false);
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, dsReportNode),
                BlackBoxSupplierUtils.getBlackBoxModelList(eventModelsSupplier, network, dsReportNode),
                curvesSupplier.get(network),
                parameters,
                DynaWaltzParameters.load(parameters),
                reportNode);

        return computationManager.execute(execEnv, new DynaWaltzHandler(context, getCommand(config), reportNode));
    }
}
