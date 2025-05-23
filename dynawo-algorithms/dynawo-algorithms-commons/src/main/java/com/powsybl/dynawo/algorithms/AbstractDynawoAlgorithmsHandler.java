/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.dynawo.DynawoFilesUtils;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.iidm.network.Network;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import static com.powsybl.dynawo.commons.DynawoConstants.*;
import static com.powsybl.dynawo.commons.DynawoUtil.getCommandExecutions;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynawoAlgorithmsHandler<R, S extends DynawoSimulationContext> extends AbstractExecutionHandler<R> {

    protected final S context;
    protected final Command command;
    protected final Network network;
    protected final ReportNode reportNode;

    protected AbstractDynawoAlgorithmsHandler(S context, Command command, ReportNode reportNode) {
        this.context = context;
        this.command = command;
        this.network = context.getNetwork();
        this.reportNode = reportNode;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        network.getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        DynawoFilesUtils.deleteExistingFile(workingDir.resolve(OUTPUTS_FOLDER), FINAL_STATE_FOLDER, OUTPUT_IIDM_FILENAME);
        writeInputFiles(workingDir);
        return getCommandExecutions(command);
    }

    private void writeInputFiles(Path workingDir) {
        try {
            DynawoUtil.writeIidm(network, workingDir.resolve(NETWORK_FILENAME));
            DynawoFilesUtils.writeInputFiles(workingDir, context);
            writeMultipleJobs(workingDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    protected abstract void writeMultipleJobs(Path workingDir) throws XMLStreamException, IOException;
}
