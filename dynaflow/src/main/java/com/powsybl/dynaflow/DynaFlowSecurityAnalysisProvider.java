/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.Extension;
import com.powsybl.commons.extensions.ExtensionJsonSerializer;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.SimpleCommandBuilder;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynaflow.json.JsonDynaFlowSaParametersSerializer;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisProvider;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisRunParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONTINGENCIES_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.DYNAFLOW_NAME;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.VERSION_FILENAME;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(SecurityAnalysisProvider.class)
public class DynaFlowSecurityAnalysisProvider implements SecurityAnalysisProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DynaFlowSecurityAnalysisProvider.class);
    private static final String WORKING_DIR_PREFIX = "dynaflow_sa_";
    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowSecurityAnalysisProvider() {
        this(DynaFlowConfig::load);
    }

    public DynaFlowSecurityAnalysisProvider(Supplier<DynaFlowConfig> configSupplier) {
        this.configSupplier = Objects.requireNonNull(configSupplier);
    }

    public static Command getCommand(DynaFlowConfig config) {
        List<String> args = Arrays.asList("--network", NETWORK_FILENAME,
                "--config", CONFIG_FILENAME,
                "--contingencies", CONTINGENCIES_FILENAME);
        return new SimpleCommandBuilder()
                .id("dynaflow_sa")
                .program(config.getProgram())
                .args(args)
                .build();
    }

    @Override
    public CompletableFuture<SecurityAnalysisReport> run(Network network,
                                                         String workingVariantId,
                                                         ContingenciesProvider contingenciesProvider,
                                                         SecurityAnalysisRunParameters runParameters) {
        if (!runParameters.getMonitors().isEmpty()) {
            LOG.error("Monitoring is not possible with Dynaflow implementation. There will not be supplementary information about monitored equipment.");
        }
        if (!runParameters.getOperatorStrategies().isEmpty()) {
            LOG.error("Strategies are not implemented in Dynaflow");
        }
        if (!runParameters.getActions().isEmpty()) {
            LOG.error("Actions are not implemented in Dynaflow");
        }
        if (!runParameters.getLimitReductions().isEmpty()) {
            LOG.error("Limit reductions are not implemented in Dynaflow");
        }

        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment execEnvVersionFolder = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX + VERSION_FILENAME, false);
        DynawoUtil.requireDynaMinVersion(execEnvVersionFolder, runParameters.getComputationManager(), DynaFlowProvider.getVersionCommand(config), DynaFlowConfig.DYNAFLOW_LAUNCHER_PROGRAM_NAME, true);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        ReportNode dfsaReportNode = DynaflowReports.createDynaFlowSecurityAnalysisReportNode(runParameters.getReportNode(), network.getId());

        DynaFlowSecurityAnalysisHandler executionHandler = new DynaFlowSecurityAnalysisHandler(network, workingVariantId, getCommand(config),
                runParameters.getSecurityAnalysisParameters(), contingencies, runParameters.getFilter(), dfsaReportNode);
        ExecutionEnvironment execEnvSimulationFolder = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        return runParameters.getComputationManager().execute(execEnvSimulationFolder, executionHandler);
    }

    @Override
    public String getName() {
        return DYNAFLOW_NAME;
    }

    @Override
    public Optional<String> getLoadFlowProviderName() {
        return Optional.of(DYNAFLOW_NAME);
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    @Override
    public Optional<Extension<SecurityAnalysisParameters>> loadSpecificParameters(PlatformConfig platformConfig) {
        // if not specified, dynaflow sa parameters must be default here
        return Optional.of(DynaFlowSecurityAnalysisParameters.load(platformConfig));
    }

    @Override
    public Optional<Extension<SecurityAnalysisParameters>> loadSpecificParameters(Map<String, String> properties) {
        return Optional.of(DynaFlowSecurityAnalysisParameters.load(properties));
    }

    @Override
    public void updateSpecificParameters(Extension<SecurityAnalysisParameters> extension, Map<String, String> properties) {
        getParametersExt(extension.getExtendable()).update(properties);
    }

    @Override
    public List<String> getSpecificParametersNames() {
        return DynaFlowSecurityAnalysisParameters.SPECIFIC_PARAMETER_NAMES;
    }

    @Override
    public Optional<ExtensionJsonSerializer> getSpecificParametersSerializer() {
        return Optional.of(new JsonDynaFlowSaParametersSerializer());
    }

    static DynaFlowSecurityAnalysisParameters getParametersExt(SecurityAnalysisParameters parameters) {
        DynaFlowSecurityAnalysisParameters parametersExt = parameters.getExtension(DynaFlowSecurityAnalysisParameters.class);
        if (parametersExt == null) {
            return new DynaFlowSecurityAnalysisParameters();
        }
        return parametersExt;
    }
}
