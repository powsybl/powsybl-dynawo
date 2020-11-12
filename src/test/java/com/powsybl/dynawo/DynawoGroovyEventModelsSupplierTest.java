/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyEventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.events.AbstractBlackBoxEventModel;
import com.powsybl.dynawo.events.EventQuadripoleDisconnection;
import com.powsybl.dynawo.events.EventQuadripoleDisconnectionGroovyExtension;
import com.powsybl.dynawo.events.EventSetPointBooleanGroovyExtension;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoGroovyEventModelsSupplierTest {

    private FileSystem fileSystem;
    private Network network;

    @Before
    public void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        network = createEurostagTutorialExample1WithMoreGens();

        Files.copy(getClass().getResourceAsStream("/powsybl-inputs/eventModels.groovy"), fileSystem.getPath("/eventModels.groovy"));
        Files.copy(getClass().getResourceAsStream("/config/models.par"), fileSystem.getPath("/models.par"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() {

        List<EventModelGroovyExtension> extensions = GroovyExtension.find(EventModelGroovyExtension.class, DynawoProvider.NAME);
        assertEquals(2, extensions.size());
        extensions.forEach(DynawoGroovyEventModelsSupplierTest::validateExtension);

        EventModelsSupplier supplier = new GroovyEventModelsSupplier(fileSystem.getPath("/eventModels.groovy"), extensions);

        List<EventModel> eventModels = supplier.get(network);
        int numLines = network.getLineCount();
        int expectedEventModelsSize =  numLines;
        assertEquals(expectedEventModelsSize, eventModels.size());
        eventModels.forEach(this::validateModel);
    }

    private static Network createEurostagTutorialExample1WithMoreGens() {
        Network network = EurostagTutorialExample1Factory.create(NetworkFactory.findDefault());

        return network;
    }

    private static boolean validateExtension(EventModelGroovyExtension extension) {
        boolean isEventQuadripoleDisconnectionExtension = extension instanceof EventQuadripoleDisconnectionGroovyExtension;
        boolean isEventSetPointExtension = extension instanceof EventSetPointBooleanGroovyExtension;

        return isEventQuadripoleDisconnectionExtension || isEventSetPointExtension;
    }

    private void validateModel(EventModel eventModel) {
        assertTrue(eventModel instanceof AbstractBlackBoxEventModel);
        AbstractBlackBoxEventModel blackBoxEventModel = (AbstractBlackBoxEventModel) eventModel;
        if (blackBoxEventModel instanceof EventQuadripoleDisconnection) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxEventModel.getStaticId());
            assertEquals("EM_" + identifiable.getId(), blackBoxEventModel.getEventModelId());
            assertEquals("EQD", blackBoxEventModel.getParameterSetId());
            assertTrue(identifiable instanceof Line);
        }
    }
}
