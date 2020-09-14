/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee.ieee14;

import java.io.IOException;
import java.nio.file.Files;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.powsybl.dynawo.ieee.AbstractIeeeTestUtil;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee14MacroConnectsTest extends AbstractIeeeTestUtil {

    @Override
    protected Network loadNetwork() throws IOException {

        Files.copy(getClass().getResourceAsStream("/ieee14-macroconnects/IEEE14.iidm"), fileSystem.getPath("/IEEE14.iidm"));
        return Importers.loadNetwork(fileSystem.getPath("/IEEE14.iidm"));
    }

    @Override
    protected void loadCaseFiles() throws IOException {
        Files.copy(getClass().getResourceAsStream("/ieee14-macroconnects/curves.groovy"), fileSystem.getPath("/curves.groovy"));
        Files.copy(getClass().getResourceAsStream("/ieee14-macroconnects/dynamicModels.groovy"), fileSystem.getPath("/dynamicModels.groovy"));
        Files.copy(getClass().getResourceAsStream("/ieee14-macroconnects/dynawoParameters.json"), fileSystem.getPath("/dynawoParameters.json"));
    }

    @Test
    public void testJob() throws IOException, XMLStreamException {
        validateJob("/ieee14-macroconnects/IEEE14.jobs");
    }

    @Test
    public void testDyd() throws IOException, XMLStreamException {
        validateDyd("/ieee14-macroconnects/IEEE14.dyd");
    }

    @Test
    public void testParameters() throws IOException, XMLStreamException {
        validateParameters("/ieee14-macroconnects/IEEE14.par", "/ieee14-macroconnects/network.par", "/ieee14-macroconnects/solvers.par", "/ieee14-macroconnects/omega_ref.par");
    }

    @Test
    public void testCurves() throws IOException, XMLStreamException {
        validateCurves("/ieee14-macroconnects/IEEE14.crv");
    }

    @Test
    public void testSimulation() throws Exception {
        validateSimulation();
    }
}
