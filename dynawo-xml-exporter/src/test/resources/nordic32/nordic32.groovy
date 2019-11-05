/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
 job('Nordic 32 - Disconnect Line') {
    solver {
        lib 'libdynawo_SolverIDA'
        file 'solvers.par'
        id 2
    }
    modeler {
        compile 'outputs/compilation'
        useStandardModelsPreCompiledModels true
        useStandardModelsModelicaModels true
        iidm 'dynawoModel.xiidm'
        parameters 'dynawoModel.par'
        parameterId 1
        dyd 'dynawoModel.dyd'
    }
    simulation {
        startTime 0
        stopTime 30
        activeCriteria false
    }
    outputs {
        directory 'outputs'
        curve 'dynawoModel.crv'
        appenders {
            appender {
                tag ''
                file 'dynawo.log'
                lvlFilter 'DEBUG'
            }
            appender {
                tag 'COMPILE'
                file 'dynawoCompiler.log'
                lvlFilter 'DEBUG'
            }
            appender {
                tag 'MODELER'
                file 'dynawoModeler.log'
                lvlFilter 'DEBUG'
            }
        }
    }
}
solverParameterSet (2) {
    parameters {
        parameter {
            name 'order'
            type 'INT'
            value '2'
        }
        parameter {
            name 'initStep'
            type 'DOUBLE'
            value '0.000001'
        }
        parameter {
            name 'minStep'
            type 'DOUBLE'
            value '0.000001'
        }
        parameter {
            name 'maxStep'
            type 'DOUBLE'
            value '10'
        }
        parameter {
            name 'absAccuracy'
            type 'DOUBLE'
            value '1e-4'
        }
        parameter {
            name 'relAccuracy'
            type 'DOUBLE'
            value '1e-4'
        }
    }
}
parameterSet (1) {
    parameters {
        parameter {
            name 'capacitor_no_reclosing_delay'
            type 'DOUBLE'
            value '300'
        }
        parameter {
            name 'dangling_line_currentLimit_maxTimeOperation'
            type 'DOUBLE'
            value '90'
        }
        parameter {
            name 'line_currentLimit_maxTimeOperation'
            type 'DOUBLE'
            value '90'
        }
        parameter {
            name 'load_Tp'
            type 'DOUBLE'
            value '90'
        }
        parameter {
            name 'load_Tq'
            type 'DOUBLE'
            value '90'
        }
        parameter {
            name 'load_alpha'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'load_alphaLong'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'load_beta'
            type 'DOUBLE'
            value '2'
        }
        parameter {
            name 'load_betaLong'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'load_isControllable'
            type 'BOOL'
            value 'false'
        }
        parameter {
            name 'load_isRestorative'
            type 'BOOL'
            value 'false'
        }
        parameter {
            name 'load_zPMax'
            type 'DOUBLE'
            value '100'
        }
        parameter {
            name 'load_zQMax'
            type 'DOUBLE'
            value '100'
        }
        parameter {
            name 'reactance_no_reclosing_delay'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'transformer_currentLimit_maxTimeOperation'
            type 'DOUBLE'
            value '90'
        }
        parameter {
            name 'transformer_t1st_HT'
            type 'DOUBLE'
            value '60'
        }
        parameter {
            name 'transformer_t1st_THT'
            type 'DOUBLE'
            value '30'
        }
        parameter {
            name 'transformer_tNext_HT'
            type 'DOUBLE'
            value '10'
        }
        parameter {
            name 'transformer_tNext_THT'
            type 'DOUBLE'
            value '10'
        }
        parameter {
            name 'transformer_tolV'
            type 'DOUBLE'
            value '0.014999999700000001'
        }
    }
}
parameterSet (2) {
    parameters {
        parameter {
            name 'nbGen'
            type 'INT'
            value '20'
        }
        parameter {
            name 'weight_gen_0'
            type 'DOUBLE'
            value '1211'
        }
        parameter {
            name 'weight_gen_1'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_2'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_3'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_4'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_5'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_6'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_7'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_8'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_9'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_10'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_11'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_12'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_13'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_14'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_15'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_16'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_17'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_18'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'weight_gen_19'
            type 'DOUBLE'
            value '1'
        }
    }
}
parameterSet (3) {
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
parameterSet (4) {
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
parameterSet (5) {
    parameters {
        parameter {
            name 'event_tEvent'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'event_disconnectOrigin'
            type 'BOOL'
            value 'false'
        }
        parameter {
            name 'event_disconnectExtremity'
            type 'BOOL'
            value 'true'
        }
    }
}
blackBoxModel ('OMEGA_REF') {
    lib 'DYNModelOmegaRef'
    parametersFile 'dynawoModel.par'
    parametersId 2
}
blackBoxModel ('_N1011____EC') {
    lib 'LoadAlphaBeta'
    parametersFile 'dynawoModel.par'
    parametersId 3
    staticId '_N1011____EC'
}
blackBoxModel ('_G10______SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'dynawoModel.par'
    parametersId 4
    staticId '_G10______SM'
}
blackBoxModel ('DISCONNECT_LINE'){
    lib 'EventQuadripoleDisconnection'
    parametersFile 'dynawoModel.par'
    parametersId 5
}
connection {
    id1 '_N1011____EC'
    var1 'load_terminal'
    id2 'NETWORK'
    var2 '_N1011____TN_ACPIN'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_0'
    id2 '_G10______SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_0'
    id2 '_G10______SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_0'
    id2 'NETWORK'
    var2 '@_G10______SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_0'
    id2 '_G10______SM'
    var2 'generator_running'
}
connection {
    id1 '_G10______SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_G10______SM@@NODE@_ACPIN'
}
connection {
    id1 '_G10______SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_G10______SM@@NODE@_switchOff'
}
connection {
    id1 'DISCONNECT_LINE'
    var1 'event_state1_value'
    id2 'NETWORK'
    var2 '_N1011___-N1013___-1_AC_state_value'
}
curve {
    model 'NETWORK'
    variable '_N1011____TN_Upu_value'
}
curve {
    model '_G10______SM'
    variable 'generator_omegaPu'
}
curve {
    model '_G10______SM'
    variable 'generator_PGen'
}
curve {
    model '_G10______SM'
    variable 'generator_QGen'
}
curve {
    model '_G10______SM'
    variable 'generator_UStatorPu'
}
curve {
    model '_G10______SM'
    variable 'voltageRegulator_UcEfdPu'
}
curve {
    model '_G10______SM'
    variable 'voltageRegulator_EfdPu'
}
curve {
    model '_N1011____EC'
    variable 'load_PPu'
}
curve {
    model '_N1011____EC'
    variable 'load_QPu'
}