/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.margincalculation.tool;

import com.powsybl.dynawo.margincalculation.tool.MarginCalculationTool;
import com.powsybl.tools.Command;
import com.powsybl.tools.Tool;
import com.powsybl.tools.test.AbstractToolTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MarginCalculationToolTest extends AbstractToolTest {

    private final MarginCalculationTool tool = new MarginCalculationTool();

    @Override
    protected Iterable<Tool> getTools() {
        return Collections.singleton(tool);
    }

    @Override
    @Test
    public void assertCommand() {
        Command command = tool.getCommand();
        assertCommand(command, "margin-calculation", 9, 4);
        assertEquals("Computation", command.getTheme());
        assertEquals("Run margin calculation", command.getDescription());
        assertNull(command.getUsageFooter());
        assertOption(command.getOptions(), "case-file", true, true);
        assertOption(command.getOptions(), "dynamic-models-file", true, true);
        assertOption(command.getOptions(), "contingencies-file", true, true);
        assertOption(command.getOptions(), "load-variations-file", true, true);
        assertOption(command.getOptions(), "parameters-file", false, true);
        assertOption(command.getOptions(), "output-file", false, true);
        assertOption(command.getOptions(), "output-log-file", false, true);
        assertOption(command.getOptions(), "import-parameters", false, true);
        assertOption(command.getOptions(), "I", false, true);
    }
}
