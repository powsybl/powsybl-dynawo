/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class DynawoSimulationConstants {

    public static final String JOBS_FILENAME = "powsybl_dynawo.jobs";

    public static final String NETWORK_FILENAME = "powsybl_dynawo.xiidm";

    public static final String DYD_FILENAME = "powsybl_dynawo.dyd";

    // TODO use it in dyna algo ?
    public static final String PHASE_2_DYD_FILENAME = "phase_2_powsybl_dynawo.dyd";

    public static final String CRV_FILENAME = "powsybl_dynawo.crv";

    public static final String CURVES_OUTPUT_PATH = "outputs/curves";

    public static final String CURVES_FILENAME = "curves.csv";

    public static final String OUTPUTS_FOLDER = "outputs";

    public static final String FINAL_STATE_FOLDER = "finalState";

    private DynawoSimulationConstants() {
    }

    public static String getSimulationParFile(Network network) {
        return network.getId() + ".par";
    }
}
