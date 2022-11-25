/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import com.powsybl.iidm.xml.IidmXmlVersion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public final class DynaFlowConstants {

    public static final String DYNAFLOW_NAME = "DynaFlow";

    public static final String CONFIG_FILENAME = "config.json";

    public static final String IIDM_FILENAME = "network.xiidm";

    public static final DynaFlowVersion VERSION_MIN = DynaFlowVersion.V_1_3_0;

    public static final DynaFlowVersion VERSION = DynaFlowVersion.V_1_3_1;

    public static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";

    public static final String OUTPUT_RESULTS_FILENAME = "results.json";

    public static final String IIDM_VERSION = IidmXmlVersion.V_1_4.toString(".");

    public enum DynaFlowVersion {
        V_1_3_0(List.of(1, 3, 0)),
        V_1_3_1(List.of(1, 3, 1));

        private final List<Integer> versionArray;
        private static final String DEFAULT_DELIMITER = ".";

        DynaFlowVersion(List<Integer> versionArray) {
            this.versionArray = versionArray;
        }

        public String toString(String separator) {
            return versionArray.stream().map(Object::toString).collect(Collectors.joining(separator));
        }

        @Override
        public String toString() {
            return this.toString(DEFAULT_DELIMITER);
        }

        public static Optional<DynaFlowVersion> of(String version) {
            return of(version, DEFAULT_DELIMITER);
        }

        public static Optional<DynaFlowVersion> of(String version, String separator) {
            return Stream.of(DynaFlowVersion.values())
                    .filter(v -> version.equals(v.toString(separator)))
                    .findFirst(); // there can only be 0 or exactly 1 match
        }
    }

    public enum OutputTypes {
        STEADYSTATE,
        LOSTEQ,
        TIMELINE,
        CONSTRAINTS;
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
