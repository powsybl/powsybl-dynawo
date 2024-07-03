/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

import com.powsybl.iidm.network.Line

curve {
    dynamicModelId "sm"
    variables "generator_cePu", "generator_PePu", "generator_cmPu", "generator_PmPu", "generator_ufPu", "generator_efdPu", "generator_omegaPu", "generator_theta", "generator_PGenPu", "generator_QGenPu", "generator_PGen", "generator_QGen", "generator_UPu", "generator_IStatorPu", "generator_IRotorPu", "generator_UStatorPu", "generator_QStatorPu", "generator_thetaInternal", "governor_PmRefPu"
}

for (Line line : network.lines) {
    curve {
        dynamicModelId line.id
        variables "line_P1Pu", "line_P2Pu", "line_Q1Pu", "line_Q2Pu"
    }
}

curve {
    dynamicModelId "tfo"
    variables "transformer_terminal1_V_re", "transformer_terminal1_V_im", "transformer_terminal2_V_re", "transformer_terminal2_V_im", "transformer_terminal1_i_re", "transformer_terminal1_i_im", "transformer_terminal2_i_re", "transformer_terminal2_i_im"
}