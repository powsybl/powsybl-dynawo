/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

/**
 * @param name Model name
 * @param info Definition of the event model
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record EventModelInfo(String name, String info) {

    /**
     * Concatenation of name and doc
     */
    public String formattedInfo() {
        return String.format("%s: %s", name, info);
    }
}
