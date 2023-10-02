/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.macroconnections;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public record MacroConnectAttribute(String name, String value) {

    public static MacroConnectAttribute of(String name, String value) {
        return new MacroConnectAttribute(name, value);
    }

    public static MacroConnectAttribute ofIndex1(int index) {
        return new MacroConnectAttribute("index1", String.valueOf(index));
    }
}
