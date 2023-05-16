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
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;

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

        DynaWaltzParameters dynaWaltzParameters = getDynaWaltzSimulationParameters(parameters);
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynaWaltzConfig.isDebug());
        DynawoUtil.requireDynawoMinVersion(execEnv, computationManager, getVersionCommand(dynaWaltzConfig), false);
        DynaWaltzContext context = setupContext(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier, workingVariantId, parameters, dynaWaltzParameters);

        return computationManager.execute(execEnv, new DynaWaltzHandler(context, dynaWaltzConfig));
    }

    private DynaWaltzParameters getDynaWaltzSimulationParameters(DynamicSimulationParameters parameters) {
        DynaWaltzParameters dynaWaltzParameters = parameters.getExtension(DynaWaltzParameters.class);
        if (dynaWaltzParameters == null) {
            dynaWaltzParameters = DynaWaltzParameters.load();
        }
        return dynaWaltzParameters;
    }

    private DynaWaltzContext setupContext(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventsModelsSupplier, CurvesSupplier curvesSupplier,
                                          String workingVariantId, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        List<BlackBoxModel> blackBoxModels = dynamicModelsSupplier.get(network).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
        List<BlackBoxModel> blackBoxEventModels = eventsModelsSupplier.get(network).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
        return new DynaWaltzContext(network, workingVariantId, blackBoxModels, blackBoxEventModels, curvesSupplier.get(network), parameters, dynaWaltzParameters);
    }
}
