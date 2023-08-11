/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.DynamicSimulationResultImpl;
import com.powsybl.dynawaltz.xml.CurvesXml;
import com.powsybl.dynawaltz.xml.DydXml;
import com.powsybl.dynawaltz.xml.JobsXml;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeriesCsvConfig;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.OUTPUT_IIDM_FILENAME;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynaWaltzHandler extends AbstractExecutionHandler<DynamicSimulationResult> {

    private final DynaWaltzContext context;
    private final Network dynawoInput;
    private final DynaWaltzConfig dynaWaltzConfig;

    public DynaWaltzHandler(DynaWaltzContext context, DynaWaltzConfig dynaWaltzConfig) {
        this.context = context;
        this.dynawoInput = context.getDynaWaltzParameters().isMergeLoads()
                ? LoadsMerger.mergeLoads(context.getNetwork())
                : context.getNetwork();
        this.dynaWaltzConfig = dynaWaltzConfig;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME);
        if (Files.exists(outputNetworkFile)) {
            Files.delete(outputNetworkFile);
        }
        Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
        if (Files.exists(curvesPath)) {
            Files.delete(curvesPath);
        }
        writeInputFiles(workingDir);
        Command cmd = DynaWaltzProvider.getCommand(dynaWaltzConfig);
        return Collections.singletonList(new CommandExecution(cmd, 1));
    }

    @Override
    public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        boolean status = true;
        Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME);
        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(context.getNetwork(), NetworkXml.read(outputNetworkFile), context.getDynaWaltzParameters().isMergeLoads());
        } else {
            status = false;
        }
        Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
        Map<String, TimeSeries> curves = new HashMap<>();
        if (Files.exists(curvesPath)) {
            Map<Integer, List<TimeSeries>> curvesPerVersion = TimeSeries.parseCsv(curvesPath, new TimeSeriesCsvConfig(TimeSeriesConstants.DEFAULT_SEPARATOR, false, TimeSeries.TimeFormat.FRACTIONS_OF_SECOND));
            curvesPerVersion.values().forEach(l -> l.forEach(curve -> curves.put(curve.getMetadata().getName(), curve)));
        } else {
            if (context.withCurves()) {
                status = false;
            }
        }
        return new DynamicSimulationResultImpl(status, null, curves, DynamicSimulationResult.emptyTimeLine());
    }

    private void writeInputFiles(Path workingDir) {
        try {
            DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(NETWORK_FILENAME));
            JobsXml.write(workingDir, context);
            DydXml.write(workingDir, context);
            ParametersXml.write(workingDir, context);
            if (context.withCurves()) {
                CurvesXml.write(workingDir, context);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
