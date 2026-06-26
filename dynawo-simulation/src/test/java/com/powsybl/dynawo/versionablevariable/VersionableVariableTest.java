/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.versionablevariable;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.versionablevariable.VersionableVariable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class VersionableVariableTest {

    @Test
    void testVariableVersion() {
        VersionableVariable variable = new VersionableVariable(
                new VersionableVariable.VariableStep(DynawoVersion.createFromString("1.5.0"), "variable1"),
                new VersionableVariable.VariableStep(DynawoVersion.createFromString("1.7.0"), "variable2"),
                new VersionableVariable.VariableStep(DynawoVersion.createFromString("1.8.0"), "variable3")
        );
        variable.setCurrentValue(DynawoVersion.createFromString("1.6.0"));
        assertEquals("variable1", variable.getCurrentValue());
    }

    @Test
    void testVersionNotFound() {
        VersionableVariable variable = new VersionableVariable(
                new VersionableVariable.VariableStep(DynawoVersion.createFromString("1.5.0"), "variable1"),
                new VersionableVariable.VariableStep(DynawoVersion.createFromString("1.8.0"), "variable2")
        );
        DynawoVersion lowVersion = DynawoVersion.createFromString("1.4.0");
        assertThatThrownBy(() -> variable.setCurrentValue(lowVersion))
            .isInstanceOf(PowsyblException.class)
            .hasMessageContaining("No VersionableVariable value found for Dynawo version 1.4.0");
    }
}
