/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameterSetTest {

    @Test
    public void test() {

        DynawoParameterSet parameterSet = new DynawoParameterSet("1");

        DynawoParameter parameter1 = new DynawoParameter("name1", "type1", "value1");
        DynawoParameter parameter2 = new DynawoParameter("name2", "type2", "origData2", "origName2", "componentId2");
        parameterSet.addParameters(Arrays.asList(parameter1, parameter2));

        DynawoParameterTable parameterTable = new DynawoParameterTable("type", "name");
        DynawoParameterRow row = new DynawoParameterRow(0, 0, "value");
        parameterTable.add(row);
        parameterSet.add(parameterTable);

        assertEquals("1", parameterSet.getId());
        assertNotNull(parameterSet.getParameters());
        assertNotNull(parameterSet.getParameterTables());

        String key1 = "name1";
        assertEquals(2, parameterSet.getParameters().size());
        assertTrue(!parameterSet.getParameters().get(key1).isReference());
        assertEquals("name1", parameterSet.getParameters().get(key1).getName());
        assertEquals("type1", parameterSet.getParameters().get(key1).getType());
        assertEquals("value1", parameterSet.getParameters().get(key1).getValue());
        String key2 = "name2";
        assertTrue(parameterSet.getParameters().get(key2).isReference());
        assertEquals("name2", parameterSet.getParameters().get(key2).getName());
        assertEquals("type2", parameterSet.getParameters().get(key2).getType());
        assertEquals("origData2", parameterSet.getParameters().get(key2).getOrigData());
        assertEquals("origName2", parameterSet.getParameters().get(key2).getOrigName());
        assertEquals("componentId2", parameterSet.getParameters().get(key2).getComponentId());

        assertEquals(1, parameterSet.getParameterTables().size());
        assertEquals("type", parameterSet.getParameterTables().get(0).getType());
        assertEquals("name", parameterSet.getParameterTables().get(0).getName());

        assertNotNull(parameterSet.getParameterTables().get(0).getParameterRows());
        assertEquals(1, parameterSet.getParameterTables().get(0).getParameterRows().size());
        assertEquals(0, parameterSet.getParameterTables().get(0).getParameterRows().get(0).getRow());
        assertEquals(0, parameterSet.getParameterTables().get(0).getParameterRows().get(0).getColumn());
        assertEquals("value", parameterSet.getParameterTables().get(0).getParameterRows().get(0).getValue());
    }
}
