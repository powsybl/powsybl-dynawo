/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class DynawoSimulationConstants {

    public static final String JOBS_FILENAME = "powsybl_dynawo.jobs";

    public static final String DYD_FILENAME = "powsybl_dynawo.dyd";

    public static final String CRV_FILENAME = "powsybl_dynawo.crv";

    public static final String FSV_FILENAME = "powsybl_dynawo.fsv";

    public static final String CURVES_OUTPUT_PATH = "curves";

    public static final String CURVES_FILENAME = "curves.csv";

    public static final String FSV_OUTPUT_PATH = "finalStateValues";

    public static final String FSV_OUTPUT_FILENAME = "finalStateValues.csv";

    public static final String MULTIPLE_JOBS_FILENAME = "multiple_jobs.xml";

    public static final String AGGREGATED_RESULTS = "aggregatedResults.xml";

    public static final String LOGS_FOLDER = "logs";

    public static final String LOGS_FILENAME = "dynawo.log";

    private DynawoSimulationConstants() {
    }
}
