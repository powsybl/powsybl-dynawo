/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawaltz.models.utils.BlackBoxSupplierUtils;
import com.powsybl.iidm.network.Network;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.JOBS_FILENAME;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynaWaltzProvider implements DynamicSimulationProvider {

    public static final String NAME = "DynaWaltz";
    private static final String DYNAWO_CMD_NAME = "dynawo";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawaltz_";

    private final DynaWaltzConfig dynaWaltzConfig;

    public DynaWaltzProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynaWaltzProvider(PlatformConfig platformConfig) {
        this(DynaWaltzConfig.load(platformConfig));
    }

    public DynaWaltzProvider(DynaWaltzConfig dynawoConfig) {
        this.dynaWaltzConfig = Objects.requireNonNull(dynawoConfig);
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
                .program(getProgram(dynaWaltzConfig))
                .args("jobs", JOBS_FILENAME)
                .add()
                .build();
    }

    public static Command getVersionCommand(DynaWaltzConfig dynaWaltzConfig) {
        List<String> args = Collections.singletonList("version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(getProgram(dynaWaltzConfig))
                .args(args)
                .build();
    }

    private static String getProgram(DynaWaltzConfig dynaWaltzConfig) {
        String extension = SystemUtils.IS_OS_WINDOWS ? ".cmd" : ".sh";
        return Paths.get(dynaWaltzConfig.getHomeDir()).resolve(DYNAWO_CMD_NAME + extension).toString();
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventModelsSupplier, CurvesSupplier curvesSupplier, String workingVariantId,
                                                          ComputationManager computationManager, DynamicSimulationParameters parameters) {
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventModelsSupplier);
        Objects.requireNonNull(curvesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);

        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynaWaltzConfig.isDebug());
        DynawoUtil.requireDynawoMinVersion(execEnv, computationManager, getVersionCommand(dynaWaltzConfig), false);
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network),
                BlackBoxSupplierUtils.getBlackBoxModelList(eventModelsSupplier, network),
                curvesSupplier.get(network),
                parameters,
                DynaWaltzParameters.load(parameters));

        return computationManager.execute(execEnv, new DynaWaltzHandler(context, dynaWaltzConfig));
    }
}
