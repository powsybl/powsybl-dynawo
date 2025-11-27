/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.iidm.serde.IidmVersion;

import java.io.File;
import java.util.List;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
public final class DynawoConstants {

    private DynawoConstants() {
    }

    /**
     * write the network to XIIDM v1.4 because currently Dynawo does not support versions above
     */
    public static final String IIDM_VERSION = IidmVersion.V_1_4.toString(".");

    public static final DynawoVersion VERSION_MIN = new DynawoVersion(1, 5, 0);

    public static final List<String> IIDM_EXTENSIONS = List.of(
            "activePowerControl",
            "slackTerminal",
            "coordinatedReactiveControl",
            "hvdcAngleDroopActivePowerControl",
            "hvdcOperatorActivePowerRange",
            "standbyAutomaton");

    public static final String NETWORK_FILENAME = "powsybl_dynawo.xiidm";

    public static final String OUTPUTS_FOLDER = "outputs";

    public static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";

    public static final String FINAL_STATE_FOLDER = "finalState";

    public static final String TIMELINE_FOLDER = "timeLine";

    public static final String TIMELINE_FILENAME = "timeline";

    public static final String FINAL_STATE_FOLDER_PATH = String.join(File.separator, OUTPUTS_FOLDER, FINAL_STATE_FOLDER);

    public static final String OUTPUT_IIDM_FILENAME_PATH = String.join(File.separator, OUTPUTS_FOLDER, FINAL_STATE_FOLDER, OUTPUT_IIDM_FILENAME);

}
