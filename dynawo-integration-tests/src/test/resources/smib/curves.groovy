/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package smib

import com.powsybl.iidm.network.Line

for (Line line : network.lines) {
    curve {
        id line.id
        variables "line_P1Pu", "line_P2Pu", "line_Q1Pu", "line_Q2Pu"
    }
}

curve {
    id "TR"
    variables "transformer_terminal1_V_re", "transformer_terminal1_V_im", "transformer_terminal2_V_re", "transformer_terminal2_V_im", "transformer_terminal1_i_re", "transformer_terminal1_i_im", "transformer_terminal2_i_re", "transformer_terminal2_i_im"
}