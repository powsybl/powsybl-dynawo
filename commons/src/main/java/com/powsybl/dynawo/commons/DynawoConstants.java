/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.commons;

import com.powsybl.iidm.xml.IidmXmlVersion;

import java.util.List;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class DynawoConstants {

    private DynawoConstants() {
    }

    /**
     * write the network to XIIDM v1.4 because currently Dynawo does not support versions above
     */
    public static final String IIDM_VERSION = IidmXmlVersion.V_1_4.toString(".");

    public static final List<String> IIDM_EXTENSIONS = List.of(
            "activePowerControl",
            "slackTerminal",
            "coordinatedReactiveControl");
}
