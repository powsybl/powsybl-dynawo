/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.DynamicSimulation;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyCurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.DynawoParameters;
import com.powsybl.dynawo.DynawoProviderTest.EvenModelsSupplierMock;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.xml.CurvesXml;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.DynawoConstants;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractIeeeTestUtil extends AbstractConverterTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Network network;
    private DynamicSimulationParameters parameters;
    private DynamicModelsSupplier dynamicModelsSupplier;
    private EventModelsSupplier eventModelsSupplier;
    private CurvesSupplier curvesSupplier;

    @Before
    public void setup() throws IOException {

        network = loadNetwork();
        loadCaseFiles();

        List<CurveGroovyExtension> curveGroovyExtensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
        curvesSupplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves.groovy"), curveGroovyExtensions);

        List<DynamicModelGroovyExtension> dynamicModelGroovyExtensions = GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoProvider.NAME);
        dynamicModelsSupplier = new GroovyDynamicModelsSupplier(fileSystem.getPath("/dynamicModels.groovy"), dynamicModelGroovyExtensions);

        eventModelsSupplier = new EvenModelsSupplierMock();

        parameters = JsonDynamicSimulationParameters.read(fileSystem.getPath("/dynawoParameters.json"));
    }

    protected abstract Network loadNetwork() throws IOException;

    protected abstract void loadCaseFiles() throws IOException;

    private DynawoParameters getDynawoSimulationParameters(DynamicSimulationParameters parameters) {
        DynawoParameters dynawoParameters = parameters.getExtension(DynawoParameters.class);
        if (dynawoParameters == null) {
            dynawoParameters = DynawoParameters.load();
        }
        return dynawoParameters;
    }

    public void validate(String expectedResourceName, Path xmlFile) throws IOException {
        compareXml(getClass().getResourceAsStream(expectedResourceName), Files.newInputStream(xmlFile));
    }

    public void validateJob(String expectedResourceName) throws IOException, XMLStreamException {
        DynawoParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        DynawoContext context = new DynawoContext(network, dynamicModelsSupplier.get(network), eventModelsSupplier.get(network), curvesSupplier.get(network), parameters, dynawoParameters);

        JobsXml.write(tmpDir, context);
        validate(expectedResourceName, tmpDir.resolve(DynawoConstants.JOBS_FILENAME));
    }

    public void validateDyd(String expectedResourceName) throws IOException, XMLStreamException {
        DynawoParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        DynawoContext context = new DynawoContext(network, dynamicModelsSupplier.get(network), eventModelsSupplier.get(network), curvesSupplier.get(network), parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate(expectedResourceName, tmpDir.resolve(DynawoConstants.DYD_FILENAME));
    }

    public void validateParameters(String expectedParameters, String expectedNetworkParameters, String expectedSolverParameters, String expectedOmegaRefParameters) throws IOException, XMLStreamException {
        DynawoParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        DynawoContext context = new DynawoContext(network, dynamicModelsSupplier.get(network), eventModelsSupplier.get(network), curvesSupplier.get(network), parameters, dynawoParameters);

        ParametersXml.write(tmpDir, context);
        validate(expectedParameters, tmpDir.resolve(fileSystem.getPath(dynawoParameters.getParametersFile()).getFileName().toString()));
        validate(expectedNetworkParameters, tmpDir.resolve(fileSystem.getPath(dynawoParameters.getNetwork().getParametersFile()).getFileName().toString()));
        validate(expectedSolverParameters, tmpDir.resolve(fileSystem.getPath(dynawoParameters.getSolver().getParametersFile()).getFileName().toString()));
        validate(expectedOmegaRefParameters, tmpDir.resolve(context.getNetwork().getId() + ".par"));
    }

    public void validateCurves(String expectedResourceName) throws IOException, XMLStreamException {
        DynawoParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        DynawoContext context = new DynawoContext(network, dynamicModelsSupplier.get(network), eventModelsSupplier.get(network), curvesSupplier.get(network), parameters, dynawoParameters);

        CurvesXml.write(tmpDir, context);
        validate(expectedResourceName, tmpDir.resolve(DynawoConstants.CRV_FILENAME));
    }

    public void validateSimulation() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Path localDir = fs.getPath("/tmp");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1));
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynawoProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, dynamicModelsSupplier, eventModelsSupplier,
                                                                  curvesSupplier, network.getVariantManager().getWorkingVariantId(),
                                                                  computationManager, parameters);
            assertNotNull(result);
        }
    }
}
