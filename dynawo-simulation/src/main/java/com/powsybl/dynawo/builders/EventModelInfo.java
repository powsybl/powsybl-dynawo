/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

/**
 * @param name Model name
 * @param info Definition of the event model
 * @param version Dynawo version range where the model can be used
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record EventModelInfo(String name, String info, VersionInterval version) {

    public EventModelInfo(String name, String info) {
        this(name, info, VersionInterval.createDefaultVersion());
    }

    /**
     * Concatenation of name, doc and version bound
     */
    public String formattedInfo() {
        return String.format("%s: %s (%s)", name, info, version);
    }
}
