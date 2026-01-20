/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.desc;

import com.powsybl.dynawo.parameters.ParameterType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DescriptionXmlTest {

    @Test
    public void loadDescription() {
        ModelDescription description = FilteredDescriptionXml.load(DescriptionXmlTest.class.getResourceAsStream("/Model.desc.xml"));
        ModelDescription expected = new ModelDescription("Lib1",
                List.of(new ModifiableParameter("param1", ParameterType.INT, Cardinality.ONE),
                        new ModifiableParameter("param2", ParameterType.STRING, Cardinality.ANY)),
                List.of(new Variable("var1", ParameterType.DOUBLE),
                        new Variable("var2", ParameterType.BOOL)));
        assertEquals(expected, description);
    }
}
