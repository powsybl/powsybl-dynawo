/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
public final class DynaFlowConstants {

    public static final String DYNAFLOW_NAME = "DynaFlow";
    public static final String CONFIG_FILENAME = "config.json";
    public static final String IIDM_FILENAME = "network.xiidm";
    public static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";
    public static final String OUTPUT_RESULTS_FILENAME = "results.json";
    static final String DYNAFLOW_OUTPUTS_FOLDER = "outputs";
    static final String DYNAFLOW_TIMELINE_FILE = "timeline.xml";


    public enum OutputTypes {
        STEADYSTATE,
        LOSTEQ,
        TIMELINE,
        CONSTRAINTS
    }

    public enum ActivePowerCompensation {
        P,
        TARGET_P,
        PMAX
    }

    public enum StartingPointMode {
        WARM,
        FLAT;

        @JsonCreator
        public static StartingPointMode fromString(String startingPointMode) {
            return startingPointMode == null ? null : StartingPointMode.valueOf(startingPointMode.toUpperCase());
        }

        @JsonValue
        public String getName() {
            return name().toLowerCase();
        }
    }

    private DynaFlowConstants() {
    }

}
