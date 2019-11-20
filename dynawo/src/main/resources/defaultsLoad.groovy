/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
parameterSet ('1') {
    parameters {
        parameter {
            name 'load_alpha'
            type 'DOUBLE'
            value '1.5'
        }
        parameter {
            name 'load_beta'
            type 'DOUBLE'
            value '2.5'
        }
        parameter {
            name 'load_P0Pu'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'p_pu'
        }
        parameter {
            name 'load_Q0Pu'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'q_pu'
        }
        parameter {
            name 'load_U0Pu'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'v_pu'
        }
        parameter {
            name 'load_UPhase0'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'angle_pu'
        }
    }
}
blackBoxModel ('#ID#'){
    lib 'LoadAlphaBeta'
    parametersFile 'dynawoModel.par'
    parametersId '0'
    staticId '#ID#'
}
connection {
    id1 '#ID#'
    var1 'load_terminal'
    id2 'NETWORK'
    var2 '#IDBUS#_ACPIN'
}