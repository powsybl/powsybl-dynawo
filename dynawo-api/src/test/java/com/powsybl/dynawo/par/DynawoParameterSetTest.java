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

        DynawoParameter parameter1 = new DynawoParameter("name", "type", "value");
        DynawoParameter parameter2 = new DynawoParameter("name", "type", "origData", "origName");
        parameterSet.addParameters(Arrays.asList(parameter1, parameter2));

        DynawoParameterTable parameterTable = new DynawoParameterTable("type", "name");
        DynawoParameterRow row = new DynawoParameterRow(0, 0, "value");
        parameterTable.add(row);
        parameterSet.add(parameterTable);

        assertEquals("1", parameterSet.getId());
        assertNotNull(parameterSet.getParameters());
        assertNotNull(parameterSet.getParameterTables());

        assertEquals(2, parameterSet.getParameters().size());
        assertTrue(!parameterSet.getParameters().get(0).isReference());
        assertEquals("name", parameterSet.getParameters().get(0).getName());
        assertEquals("type", parameterSet.getParameters().get(0).getType());
        assertEquals("value", parameterSet.getParameters().get(0).getValue());
        assertTrue(parameterSet.getParameters().get(1).isReference());
        assertEquals("name", parameterSet.getParameters().get(1).getName());
        assertEquals("type", parameterSet.getParameters().get(1).getType());
        assertEquals("origData", parameterSet.getParameters().get(1).getOrigData());
        assertEquals("origName", parameterSet.getParameters().get(1).getOrigName());

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
