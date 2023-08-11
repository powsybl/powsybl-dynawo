/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;
import com.powsybl.loadflow.json.LoadFlowResultDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.DynaFlowConstants.OUTPUT_RESULTS_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.OUTPUT_IIDM_FILENAME;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DynaFlowHandler extends AbstractExecutionHandler<LoadFlowResult> {
    private final Network network;
    private final Network dynawoInput;
    private final String workingStateId;
    private final DynaFlowParameters dynaFlowParameters;
    private final LoadFlowParameters loadFlowParameters;
    private final DynaFlowConfig config;

    public DynaFlowHandler(Network network, String workingStateId, DynaFlowParameters dynaFlowParameters, LoadFlowParameters loadFlowParameters, DynaFlowConfig config) {
        this.network = network;
        this.workingStateId = workingStateId;
        this.dynaFlowParameters = dynaFlowParameters;
        this.loadFlowParameters = loadFlowParameters;
        this.config = config;
        this.dynawoInput = this.dynaFlowParameters.isMergeLoads() ? LoadsMerger.mergeLoads(this.network) : this.network;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        network.getVariantManager().setWorkingVariant(workingStateId);
        DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(IIDM_FILENAME));
        DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, Path.of("."), workingDir.resolve(CONFIG_FILENAME));
        return Collections.singletonList(createCommandExecution(config));
    }

    private static CommandExecution createCommandExecution(DynaFlowConfig config) {
        Command cmd = DynaFlowProvider.getCommand(config);
        return new CommandExecution(cmd, 1, 0);
    }

    @Override
    public LoadFlowResult after(Path workingDir, ExecutionReport report) {
        report.log();
        network.getVariantManager().setWorkingVariant(workingStateId);
        boolean status = true;
        Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME);
        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(network, NetworkXml.read(outputNetworkFile), dynaFlowParameters.isMergeLoads());
        } else {
            status = false;
        }
        Path resultsPath = workingDir.resolve(OUTPUT_RESULTS_FILENAME);
        if (!Files.exists(resultsPath)) {
            Map<String, String> metrics = new HashMap<>();
            List<LoadFlowResult.ComponentResult> componentResults = new ArrayList<>(1);
            componentResults.add(new LoadFlowResultImpl.ComponentResultImpl(0,
                    0,
                    status ? LoadFlowResult.ComponentResult.Status.CONVERGED : LoadFlowResult.ComponentResult.Status.FAILED,
                    0,
                    "not-found",
                    0.,
                    Double.NaN));
            return new LoadFlowResultImpl(status, metrics, null, componentResults);
        }
        return LoadFlowResultDeserializer.read(resultsPath);
    }
}
