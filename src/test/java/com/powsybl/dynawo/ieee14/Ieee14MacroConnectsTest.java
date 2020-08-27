/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee14;

import java.io.IOException;
import java.nio.file.Files;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee14MacroConnectsTest extends IeeeTestUtil {

    public Network loadNetwork() throws IOException {

        Files.copy(getClass().getResourceAsStream("/ieee14-macroconnects/IEEE14.iidm"), fileSystem.getPath("/IEEE14.iidm"));
        return Importers.loadNetwork(fileSystem.getPath("/IEEE14.iidm"));
    }

    @Test
    public void writeJob() throws IOException, XMLStreamException {
        writeJob(loadNetwork(), "/ieee14-macroconnects/IEEE14.jobs");
    }

    @Test
    public void writeDyd() throws IOException, XMLStreamException {
        writeDyd(loadNetwork(), "/ieee14-macroconnects/IEEE14.dyd");
    }

    @Test
    public void writeParameters() throws IOException, XMLStreamException {
        writeParameters(loadNetwork(), "/ieee14-macroconnects/IEEE14.par", "/ieee14-macroconnects/network.par", "/ieee14-macroconnects/solvers.par", "/ieee14-macroconnects/omega_ref.par");
    }

    @Test
    public void writeCurves() throws IOException, XMLStreamException {
        writeCurves(loadNetwork(), "/ieee14-macroconnects/IEEE14.crv");
    }

    @Test
    public void testSimulation() throws Exception {
        testSimulation(loadNetwork());
    }
}
