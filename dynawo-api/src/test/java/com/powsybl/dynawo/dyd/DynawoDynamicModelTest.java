/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoDynamicModelTest {

    @Test
    public void testBlackBoxModel() {

        BlackBoxModel model = new BlackBoxModel("id", "lib", "parametersFile", 1);

        StaticRef staticRef = new StaticRef("var", "staticVar");
        model.add(staticRef);

        DydComponent macroStaticRef = new DydComponent("id");
        model.add(macroStaticRef);

        assertEquals("id", model.getId());
        assertEquals("lib", model.getLib());
        assertEquals("parametersFile", model.getParametersFile());
        assertEquals(1, model.getParametersId());
        assertNull(model.getStaticId());

        assertNotNull(model.getStaticRefs());
        assertNotNull(model.getMacroStaticRefs());

        assertEquals(1, model.getStaticRefs().size());
        assertEquals("var", model.getStaticRefs().get(0).getVar());
        assertEquals("staticVar", model.getStaticRefs().get(0).getStaticVar());

        assertEquals(1, model.getMacroStaticRefs().size());
        assertEquals("id", model.getMacroStaticRefs().get(0).getId());
    }

    @Test
    public void testModelicaModel() {

        ModelicaModel model = new ModelicaModel("id");

        StaticRef staticRef = new StaticRef("var", "staticVar");
        model.add(staticRef);

        DydComponent macroStaticRef = new DydComponent("id");
        model.add(macroStaticRef);

        UnitDynamicModel unitDynamicModel = new UnitDynamicModel("id", "name", "initName", "parametersFile", 1);
        model.add(unitDynamicModel);

        Connection connection = new Connection("id1", "var1", "id2", "var2");
        model.add(connection);

        InitConnection initConnection = new InitConnection("id1", "var1", "id2", "var2");
        model.add(initConnection);

        assertEquals("id", model.getId());
        assertNull(model.getStaticId());

        assertNotNull(model.getStaticRefs());
        assertNotNull(model.getMacroStaticRefs());
        assertNotNull(model.getUnitDynamicModels());
        assertNotNull(model.getConnections());
        assertNotNull(model.getInitConnections());

        assertEquals(1, model.getStaticRefs().size());
        assertEquals("var", model.getStaticRefs().get(0).getVar());
        assertEquals("staticVar", model.getStaticRefs().get(0).getStaticVar());

        assertEquals(1, model.getMacroStaticRefs().size());
        assertEquals("id", model.getMacroStaticRefs().get(0).getId());

        assertEquals(1, model.getUnitDynamicModels().size());
        assertEquals("id", model.getUnitDynamicModels().get(0).getId());
        assertEquals("name", model.getUnitDynamicModels().get(0).getName());
        assertNull(model.getUnitDynamicModels().get(0).getMoFile());
        assertEquals("initName", model.getUnitDynamicModels().get(0).getInitName());
        assertEquals("parametersFile", model.getUnitDynamicModels().get(0).getParametersFile());
        assertEquals(1, model.getUnitDynamicModels().get(0).getParametersId());

        assertEquals(1, model.getConnections().size());
        assertEquals("id1", model.getConnections().get(0).getId1());
        assertEquals("var1", model.getConnections().get(0).getVar1());
        assertEquals("id2", model.getConnections().get(0).getId2());
        assertEquals("var2", model.getConnections().get(0).getVar2());

        assertEquals(1, model.getInitConnections().size());
        assertEquals("id1", model.getInitConnections().get(0).getId1());
        assertEquals("var1", model.getInitConnections().get(0).getVar1());
        assertEquals("id2", model.getInitConnections().get(0).getId2());
        assertEquals("var2", model.getInitConnections().get(0).getVar2());
    }

    @Test
    public void testModelTemplate() {

        ModelTemplate model = new ModelTemplate("id");

        UnitDynamicModel unitDynamicModel = new UnitDynamicModel("id", "name", "initName", "parametersFile", 1);
        model.add(unitDynamicModel);

        Connection connection = new Connection("id1", "var1", "id2", "var2");
        model.add(connection);

        InitConnection initConnection = new InitConnection("id1", "var1", "id2", "var2");
        model.add(initConnection);

        assertEquals("id", model.getId());
        assertNotNull(model.getUnitDynamicModels());
        assertNotNull(model.getConnections());
        assertNotNull(model.getInitConnections());

        assertEquals(1, model.getUnitDynamicModels().size());
        assertEquals("id", model.getUnitDynamicModels().get(0).getId());
        assertEquals("name", model.getUnitDynamicModels().get(0).getName());
        assertNull(model.getUnitDynamicModels().get(0).getMoFile());
        assertEquals("initName", model.getUnitDynamicModels().get(0).getInitName());
        assertEquals("parametersFile", model.getUnitDynamicModels().get(0).getParametersFile());
        assertEquals(1, model.getUnitDynamicModels().get(0).getParametersId());

        assertEquals(1, model.getConnections().size());
        assertEquals("id1", model.getConnections().get(0).getId1());
        assertEquals("var1", model.getConnections().get(0).getVar1());
        assertEquals("id2", model.getConnections().get(0).getId2());
        assertEquals("var2", model.getConnections().get(0).getVar2());

        assertEquals(1, model.getInitConnections().size());
        assertEquals("id1", model.getInitConnections().get(0).getId1());
        assertEquals("var1", model.getInitConnections().get(0).getVar1());
        assertEquals("id2", model.getInitConnections().get(0).getId2());
        assertEquals("var2", model.getInitConnections().get(0).getVar2());
    }

    @Test
    public void testModelTemplateExpansion() {

        ModelTemplateExpansion model = new ModelTemplateExpansion("id", "templateId", "parametersFile", 1);

        assertEquals("id", model.getId());
        assertEquals("templateId", model.getTemplateId());
        assertEquals("parametersFile", model.getParametersFile());
        assertEquals(1, model.getParametersId());
    }

    @Test
    public void testMacroConnection() {

        MacroConnection connection = new MacroConnection("connector", "id1", "id2");

        assertEquals("connector", connection.getConnector());
        assertEquals("id1", connection.getId1());
        assertEquals("id2", connection.getId2());
    }

    @Test
    public void testMacroConnector() {

        MacroConnector connector = new MacroConnector("id");

        DydConnection connection = new DydConnection("var1", "var2");
        connector.add(connection);

        assertEquals("id", connector.getId());

        assertNotNull(connector.getConnections());

        assertEquals(1, connector.getConnections().size());
        assertEquals("var1", connector.getConnections().get(0).getVar1());
        assertEquals("var2", connector.getConnections().get(0).getVar2());
    }

    @Test
    public void testMacroStaticReference() {

        MacroStaticReference reference = new MacroStaticReference("id");

        StaticRef staticRef = new StaticRef("var", "staticVar");
        reference.add(staticRef);

        assertEquals("id", reference.getId());

        assertNotNull(reference.getStaticRefs());

        assertEquals(1, reference.getStaticRefs().size());
        assertEquals("var", reference.getStaticRefs().get(0).getVar());
        assertEquals("staticVar", reference.getStaticRefs().get(0).getStaticVar());
    }
}
