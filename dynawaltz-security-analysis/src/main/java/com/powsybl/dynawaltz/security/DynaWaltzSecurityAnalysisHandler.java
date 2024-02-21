/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.security;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynaflow.ContingencyResultsUtils;
import com.powsybl.dynawaltz.security.xml.ContingenciesDydXml;
import com.powsybl.dynawaltz.security.xml.ContingenciesParXml;
import com.powsybl.dynawaltz.security.xml.MultipleJobsXml;
import com.powsybl.dynawaltz.xml.DydXml;
import com.powsybl.dynawaltz.xml.DynaWaltzConstants;
import com.powsybl.dynawaltz.xml.JobsXml;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.commons.SimpleDynawoConfig;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynaflow.SecurityAnalysisConstants.DYNAWO_CONSTRAINTS_FOLDER;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynaWaltzSecurityAnalysisHandler extends AbstractExecutionHandler<SecurityAnalysisReport> {

    private final SecurityAnalysisContext context;
    private final SimpleDynawoConfig config;
    private final Network network;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;

    public DynaWaltzSecurityAnalysisHandler(SecurityAnalysisContext context, SimpleDynawoConfig config,
                                            LimitViolationFilter violationFilter, List<SecurityAnalysisInterceptor> interceptors) {
        this.context = context;
        this.network = context.getNetwork();
        this.config = config;
        this.violationFilter = violationFilter;
        this.interceptors = interceptors;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        network.getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(DynawoConstants.OUTPUT_IIDM_FILENAME);
        if (Files.exists(outputNetworkFile)) {
            Files.delete(outputNetworkFile);
        }
        writeInputFiles(workingDir);
        return Collections.singletonList(new CommandExecution(DynaWaltzSecurityAnalysisProvider.getCommand(config), 1, 0));
    }

    @Override
    public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {

        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(DynawoConstants.OUTPUT_IIDM_FILENAME);
        if (Files.exists(outputNetworkFile)) {
            //TODO handle merge load
            NetworkResultsUpdater.update(context.getNetwork(), NetworkSerDe.read(outputNetworkFile), false);
        }

        return new SecurityAnalysisReport(
                new SecurityAnalysisResult(
                        ContingencyResultsUtils.getPreContingencyResult(network, violationFilter),
                        ContingencyResultsUtils.getPostContingencyResults(network, violationFilter, workingDir.resolve(DYNAWO_CONSTRAINTS_FOLDER), context.getContingencies()),
                        Collections.emptyList())
        );
    }

    private void writeInputFiles(Path workingDir) {
        try {
            DynawoUtil.writeIidm(network, workingDir.resolve(DynaWaltzConstants.NETWORK_FILENAME));
            JobsXml.write(workingDir, context);
            DydXml.write(workingDir, context);
            // TODO handle Security Analysis parameters
            ParametersXml.write(workingDir, context);
            MultipleJobsXml.write(workingDir, context);
            ContingenciesDydXml.write(workingDir, context);
            ContingenciesParXml.write(workingDir, context);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
