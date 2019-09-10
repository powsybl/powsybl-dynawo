/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoJobs {

    public DynawoJobs(Network network) {
        this.network = network;
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.jobs");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(),
                DynawoInput.setInputHeader(),
                "<dyn:jobs xmlns:dyn=\"http://www.rte-france.com/dynawo\">",
                "  <dyn:job name=\"IEEE14 - Disconnect Line\">",
                "    <dyn:solver lib=\"libdynawo_SolverIDA\" parFile=\"solvers.par\" parId=\"2\"/>",
                "    <dyn:modeler compileDir=\"outputs/compilation\">",
                "      <dyn:network iidmFile=\"dynawoModel.iidm\" parFile=\"dynawoModel.par\" parId=\"1\"/>",
                "      <dyn:dynModels dydFile=\"dynawoModel.dyd\"/>",
                "      <dyn:precompiledModels useStandardModels=\"true\"/>",
                "      <dyn:modelicaModels useStandardModels=\"true\"/>",
                "    </dyn:modeler>",
                "    <dyn:simulation startTime=\"0\" stopTime=\"30\" activateCriteria=\"false\"/>",
                "    <dyn:outputs directory=\"outputs\">",
                "      <dyn:dumpInitValues local=\"true\" global=\"true\"/>",
                "      <dyn:curves inputFile=\"dynawoModel.crv\" exportMode=\"CSV\"/>",
                "      <dyn:timeline exportMode=\"TXT\"/>",
                "      <dyn:logs>",
                "        <dyn:appender tag=\"\" file=\"dynawo.log\" lvlFilter=\"DEBUG\"/>",
                "        <dyn:appender tag=\"COMPILE\" file=\"dynawoCompiler.log\" lvlFilter=\"DEBUG\"/>",
                "        <dyn:appender tag=\"MODELER\" file=\"dynawoModeler.log\" lvlFilter=\"DEBUG\"/>",
                "      </dyn:logs>",
                "    </dyn:outputs>",
                "  </dyn:job>",
                "</dyn:jobs>"));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.jobs");
        }
    }

    private final Network network;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoJobs.class);
}
