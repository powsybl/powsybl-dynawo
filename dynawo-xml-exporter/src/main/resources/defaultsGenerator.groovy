/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
parameterSet (1) {
    parameters {
        parameter {
            name 'generator_ExcitationPu'
            type 'INT'
            value '1'
        }
        parameter {
            name 'generator_DPu'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'generator_H'
            type 'DOUBLE'
            value '5.4000000000000004'
        }
        parameter {
            name 'generator_RaPu'
            type 'DOUBLE'
            value '0.0027959999999999999'
        }
        parameter {
            name 'generator_XlPu'
            type 'DOUBLE'
            value '0.20200000000000001'
        }
        parameter {
            name 'generator_XdPu'
            type 'DOUBLE'
            value '2.2200000000000002'
        }
        parameter {
            name 'generator_XpdPu'
            type 'DOUBLE'
            value '0.38400000000000001'
        }
        parameter {
            name 'generator_XppdPu'
            type 'DOUBLE'
            value '0.26400000000000001'
        }
        parameter {
            name 'generator_Tpd0'
            type 'DOUBLE'
            value '8.0939999999999994'
        }
        parameter {
            name 'generator_Tppd0'
            type 'DOUBLE'
            value '0.080000000000000002'
        }
        parameter {
            name 'generator_XqPu'
            type 'DOUBLE'
            value '2.2200000000000002'
        }
        parameter {
            name 'generator_XpqPu'
            type 'DOUBLE'
            value '0.39300000000000002'
        }
        parameter {
            name 'generator_XppqPu'
            type 'DOUBLE'
            value '0.26200000000000001'
        }
        parameter {
            name 'generator_Tpq0'
            type 'DOUBLE'
            value '1.5720000000000001'
        }
        parameter {
            name 'generator_Tppq0'
            type 'DOUBLE'
            value '0.084000000000000005'
        }
        parameter {
            name 'generator_UNom'
            type 'DOUBLE'
            value '24'
        }
        parameter {
            name 'generator_SNom'
            type 'DOUBLE'
            value '1211'
        }
        parameter {
            name 'generator_PNom'
            type 'DOUBLE'
            value '1090'
        }
        parameter {
            name 'generator_SnTfo'
            type 'DOUBLE'
            value '1211'
        }
        parameter {
            name 'generator_UNomHV'
            type 'DOUBLE'
            value '69'
        }
        parameter {
            name 'generator_UNomLV'
            type 'DOUBLE'
            value '24'
        }
        parameter {
            name 'generator_UBaseHV'
            type 'DOUBLE'
            value '69'
        }
        parameter {
            name 'generator_UBaseLV'
            type 'DOUBLE'
            value '24'
        }
        parameter {
            name 'generator_RTfPu'
            type 'DOUBLE'
            value '0.0'
        }
        parameter {
            name 'generator_XTfPu'
            type 'DOUBLE'
            value '0.1'
        }
        parameter {
            name 'voltageRegulator_LagEfdMax'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'voltageRegulator_LagEfdMin'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'voltageRegulator_EfdMinPu'
            type 'DOUBLE'
            value '-5'
        }
        parameter {
            name 'voltageRegulator_EfdMaxPu'
            type 'DOUBLE'
            value '5'
        }
        parameter {
            name 'voltageRegulator_UsRefMinPu'
            type 'DOUBLE'
            value '0.8'
        }
        parameter {
            name 'voltageRegulator_UsRefMaxPu'
            type 'DOUBLE'
            value '1.2'
        }
        parameter {
            name 'voltageRegulator_Gain'
            type 'DOUBLE'
            value '20'
        }
        parameter {
            name 'governor_KGover'
            type 'DOUBLE'
            value '5'
        }
        parameter {
            name 'governor_PMin'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'governor_PMax'
            type 'DOUBLE'
            value '1090'
        }
        parameter {
            name 'governor_PNom'
            type 'DOUBLE'
            value '1090'
        }
        parameter {
            name 'URef_ValueIn'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'Pm_ValueIn'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'p_pu'
        }
        parameter {
            name 'generator_Q0Pu'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'q_pu'
        }
        parameter {
            name 'generator_U0Pu'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'v_pu'
        }
        parameter {
            name 'generator_UPhase0'
            type 'DOUBLE'
            origData 'IIDM'
            origName 'angle_pu'
        }
    }
}
blackBoxModel ('#ID#'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'dynawoModel.par'
    parametersId 0
    staticId '#ID#'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_#NUM#'
    id2 '#ID#'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_#NUM#'
    id2 '#ID#'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_#NUM#'
    id2 'NETWORK'
    var2 '@#ID#@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_#NUM#'
    id2 '#ID#'
    var2 'generator_running'
}
connection {
    id1 '#ID#'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@#ID#@@NODE@_ACPIN'
}
connection {
    id1 '#ID#'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@#ID#@@NODE@_switchOff'
}