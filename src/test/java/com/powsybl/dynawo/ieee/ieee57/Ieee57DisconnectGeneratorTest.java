/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee.ieee57;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.powsybl.dynawo.ieee.AbstractIeeeTestUtil;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee57DisconnectGeneratorTest extends AbstractIeeeTestUtil {

    @Override
    protected Network loadNetwork() throws IOException {

        Files.copy(getClass().getResourceAsStream("/ieee57-disconnectgenerator/IEEE57.iidm"), fileSystem.getPath("/IEEE57.iidm"));
        return Importers.loadNetwork(fileSystem.getPath("/IEEE57.iidm"));
    }

    @Override
    protected void loadCaseFiles() throws IOException {
        Files.copy(getClass().getResourceAsStream("/ieee57-disconnectgenerator/curves.groovy"), fileSystem.getPath("/curves.groovy"));
        Files.copy(getClass().getResourceAsStream("/ieee57-disconnectgenerator/dynamicModels.groovy"), fileSystem.getPath("/dynamicModels.groovy"));
        Files.copy(getClass().getResourceAsStream("/ieee57-disconnectgenerator/dynawoParameters.json"), fileSystem.getPath("/dynawoParameters.json"));
    }

    @Test
    public void testJob() throws IOException, XMLStreamException {
        validateJob("/ieee57-disconnectgenerator/IEEE57.jobs");
    }

    @Test
    public void testDyd() throws IOException, XMLStreamException {
        validateDyd("/ieee57-disconnectgenerator/IEEE57.dyd");
    }

    @Test
    public void testParameters() throws IOException, XMLStreamException {
        validateParameters("/ieee57-disconnectgenerator/IEEE57.par", "/ieee57-disconnectgenerator/network.par", "/ieee57-disconnectgenerator/solvers.par", "/ieee57-disconnectgenerator/omega_ref.par");
    }

    @Test
    public void testCurves() throws IOException, XMLStreamException {
        validateCurves("/ieee57-disconnectgenerator/IEEE57.crv");
    }

    @Test
    public void testSimulation() throws Exception {
        validateSimulation();
    }
}
