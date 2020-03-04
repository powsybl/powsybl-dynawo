/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
job('IEEE57 - Disconnect line 6-8 - IDA Solver') {
    solver {
        lib 'dynawo_SolverIDA'
        file 'powsybl_dynawo_simulation_params.par'
        id '2'
    }
    modeler {
        compile 'outputs/compilation'
        useStandardModelsPreCompiledModels true
        useStandardModelsModelicaModels true
        iidm 'powsybl_network.xiidm'
        parameters 'powsybl_dynawo.par'
        parameterId '51'
        dyd 'powsybl_dynawo.dyd'
    }
    simulation {
        startTime 0
        stopTime 30
        activeCriteria false
    }
    outputs {
        directory 'outputs'
        curve 'powsybl_dynawo.crv'
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
job('IEEE57 - Disconnect line 6-8 - Simplified solver') {
    solver {
        lib 'dynawo_SolverSIM'
        file 'powsybl_dynawo_simulation_params.par'
        id '3'
    }
    modeler {
        compile 'outputsSIM/compilation'
        useStandardModelsPreCompiledModels true
        useStandardModelsModelicaModels true
        iidm 'powsybl_network.xiidm'
        parameters 'powsybl_dynawo.par'
        parameterId '51'
        dyd 'powsybl_dynawo.dyd'
    }
    simulation {
        startTime 0
        stopTime 30
        activeCriteria false
    }
    outputs {
        directory 'outputsSIM'
        curve 'powsybl_dynawo.crv'
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
solverParameterSet ('2') {
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
solverParameterSet ('3') {
    parameters {
        parameter {
            name 'hMin'
            type 'DOUBLE'
            value '0.000001'
        }
        parameter {
            name 'hMax'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'kReduceStep'
            type 'DOUBLE'
            value '0.5'
        }
        parameter {
            name 'nEff'
            type 'INT'
            value '10'
        }
        parameter {
            name 'nDeadband'
            type 'INT'
            value '2'
        }
        parameter {
            name 'maxRootRestart'
            type 'INT'
            value '3'
        }
        parameter {
            name 'maxNewtonTry'
            type 'INT'
            value '10'
        }
        parameter {
            name 'linearSolverName'
            type 'STRING'
            value 'KLU'
        }
        parameter {
            name 'recalculateStep'
            type 'BOOL'
            value 'false'
        }
    }
}
parameterSet ('51') {
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
            value '1.5'
        }
        parameter {
            name 'load_alphaLong'
            type 'DOUBLE'
            value '0'
        }
        parameter {
            name 'load_beta'
            type 'DOUBLE'
            value '2.5'
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
parameterSet ('43') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.4000000000000004'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.00357'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.219'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.57'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.407'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.3'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '9.651'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.058'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.57'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.454'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.301'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.0009'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.06'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1120'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1008'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1008'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1120'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1008'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1008'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('44') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.625'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.00316'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.256'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.81'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.509'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.354'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '10.041'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.065'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.62'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.601'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.377'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.21'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.094'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1650'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1485'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1485'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1650'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1008'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1008'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('45') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.112'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.003275'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.265'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.91'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.527'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.367'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '10.041'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.065'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.72'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.623'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.391'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.22'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.094'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1710'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1710'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('46') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.112'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.003275'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.265'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.91'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.527'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.367'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '10.041'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.065'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.72'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.623'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.391'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.22'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.094'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1710'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1710'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1539'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('47') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.4'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.002796'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.202'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.22'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.384'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.264'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '8.094'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.08'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.22'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.262'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.26'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.572'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.084'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1211'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1211'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('48') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.4'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.002796'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.202'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.22'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.384'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.264'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '8.094'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.08'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.22'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.262'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.26'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.572'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.084'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1211'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1211'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '24'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1090'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('49') {
    parameters {
        parameter {
            type 'INT'
            name 'generator_ExcitationPu'
            value '1'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_MdPuEfd'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_DPu'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_H'
            value '5.625'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RaPu'
            value '0.00316'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XlPu'
            value '0.256'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XdPu'
            value '2.81'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpdPu'
            value '0.509'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppdPu'
            value '0.354'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpd0'
            value '10.041'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppd0'
            value '0.065'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XqPu'
            value '2.62'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XpqPu'
            value '0.601'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XppqPu'
            value '0.377'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tpq0'
            value '1.21'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_Tppq0'
            value '0.094'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNom'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SNom'
            value '1650'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomTurb'
            value '1485'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_PNomAlt'
            value '1485'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_SnTfo'
            value '1650'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UNomLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseHV'
            value '69'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_UBaseLV'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_RTfPu'
            value '0.0'
        }
        parameter {
            type 'DOUBLE'
            name 'generator_XTfPu'
            value '0.1'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_KGover'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PMax'
            value '1485'
        }
        parameter {
            type 'DOUBLE'
            name 'governor_PNom'
            value '1485'
        }
        parameter {
            type 'DOUBLE'
            name 'Pm_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'URef_ValueIn'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMinPu'
            value '-5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_EfdMaxPu'
            value '5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMinPu'
            value '0.8'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_UsRefMaxPu'
            value '1.5'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_Gain'
            value '20'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMin'
            value '0'
        }
        parameter {
            type 'DOUBLE'
            name 'voltageRegulator_LagEfdMax'
            value '0'
        }
        parameter {
            name 'generator_P0Pu'
            origData 'IIDM'
            origName 'p_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_Q0Pu'
            origData 'IIDM'
            origName 'q_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_U0Pu'
            origData 'IIDM'
            origName 'v_pu'
            type 'DOUBLE'
        }
        parameter {
            name 'generator_UPhase0'
            origData 'IIDM'
            origName 'angle_pu'
            type 'DOUBLE'
        }
    }
}
parameterSet ('50') {
    parameters {
        parameter {
            type 'INT'
            name 'nbGen'
            value '7'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_0'
            value '6048'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_1'
            value '9281.25'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_2'
            value '8755.2'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_3'
            value '8755.2'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_4'
            value '6539.4'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_5'
            value '6539.4'
        }
        parameter {
            type 'DOUBLE'
            name 'weight_gen_6'
            value '9281.25'
        }
    }
}
parameterSet ('1') {
    parameters {
        parameter {
            name 'event_tEvent'
            type 'DOUBLE'
            value '1'
        }
        parameter {
            name 'event_disconnectOrigin'
            type 'BOOL'
            value 'true'
        }
        parameter {
            name 'event_disconnectExtremity'
            type 'BOOL'
            value 'false'
        }
    }
}
blackBoxModel ('GEN____6_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '43'
    staticId '_GEN____6_SM'
}
blackBoxModel ('GEN____9_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '44'
    staticId '_GEN____9_SM'
}
blackBoxModel ('GEN____1_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '45'
    staticId '_GEN____1_SM'
}
blackBoxModel ('GEN___12_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '46'
    staticId '_GEN___12_SM'
}
blackBoxModel ('GEN____3_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '47'
    staticId '_GEN____3_SM'
}
blackBoxModel ('GEN____8_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '48'
    staticId '_GEN____8_SM'
}
blackBoxModel ('GEN____2_SM'){
    lib 'GeneratorSynchronousFourWindingsProportionalRegulations'
    parametersFile 'powsybl_dynawo.par'
    parametersId '49'
    staticId '_GEN____2_SM'
}
blackBoxModel ('DisconnectLine'){
    lib 'EventQuadripoleDisconnection'
    parametersFile 'powsybl_dynawo.par'
    parametersId '1'
}
blackBoxModel ('OMEGA_REF'){
    lib 'DYNModelOmegaRef'
    parametersFile 'powsybl_dynawo.par'
    parametersId '50'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_0'
    id2 'GEN____6_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_0'
    id2 'GEN____6_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_0'
    id2 'NETWORK'
    var2 '@_GEN____6_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_0'
    id2 'GEN____6_SM'
    var2 'generator_running'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_1'
    id2 'GEN____9_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_1'
    id2 'GEN____9_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_1'
    id2 'NETWORK'
    var2 '@_GEN____9_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_1'
    id2 'GEN____9_SM'
    var2 'generator_running'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_2'
    id2 'GEN____1_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_2'
    id2 'GEN____1_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_2'
    id2 'NETWORK'
    var2 '@_GEN____1_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_2'
    id2 'GEN____1_SM'
    var2 'generator_running'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_3'
    id2 'GEN___12_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_3'
    id2 'GEN___12_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_3'
    id2 'NETWORK'
    var2 '@_GEN___12_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_3'
    id2 'GEN___12_SM'
    var2 'generator_running'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_4'
    id2 'GEN____3_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_4'
    id2 'GEN____3_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_4'
    id2 'NETWORK'
    var2 '@_GEN____3_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_4'
    id2 'GEN____3_SM'
    var2 'generator_running'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_5'
    id2 'GEN____8_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_5'
    id2 'GEN____8_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_5'
    id2 'NETWORK'
    var2 '@_GEN____8_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_5'
    id2 'GEN____8_SM'
    var2 'generator_running'
}
connection {
    id1 'OMEGA_REF'
    var1 'omega_grp_6'
    id2 'GEN____2_SM'
    var2 'generator_omegaPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'omegaRef_grp_6'
    id2 'GEN____2_SM'
    var2 'generator_omegaRefPu'
}
connection {
    id1 'OMEGA_REF'
    var1 'numcc_node_6'
    id2 'NETWORK'
    var2 '@_GEN____2_SM@@NODE@_numcc'
}
connection {
    id1 'OMEGA_REF'
    var1 'running_grp_6'
    id2 'GEN____2_SM'
    var2 'generator_running'
}
connection {
    id1 'GEN____6_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN____6_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN____6_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN____6_SM@@NODE@_switchOff'
}
connection {
    id1 'GEN____9_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN____9_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN____9_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN____9_SM@@NODE@_switchOff'
}
connection {
    id1 'GEN____1_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN____1_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN____1_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN____1_SM@@NODE@_switchOff'
}
connection {
    id1 'GEN___12_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN___12_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN___12_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN___12_SM@@NODE@_switchOff'
}
connection {
    id1 'GEN____3_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN____3_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN____3_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN____3_SM@@NODE@_switchOff'
}
connection {
    id1 'GEN____8_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN____8_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN____8_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN____8_SM@@NODE@_switchOff'
}
connection {
    id1 'GEN____2_SM'
    var1 'generator_terminal'
    id2 'NETWORK'
    var2 '@_GEN____2_SM@@NODE@_ACPIN'
}
connection {
    id1 'GEN____2_SM'
    var1 'generator_switchOffSignal1'
    id2 'NETWORK'
    var2 '@_GEN____2_SM@@NODE@_switchOff'
}
connection {
    id1 'DisconnectLine'
    var1 'event_state1'
    id2 'NETWORK'
    var2 '_BEAV___6-CLIN___8-1_AC_state'
}
curve {
    model 'NETWORK'
    variable '_KANA___1_TN_Upu_value'
}
curve {
    model 'NETWORK'
    variable '_TURN___2_TN_Upu_value'
}
curve {
    model 'NETWORK'
    variable '_LOGA___3_TN_Upu_value'
}
curve {
    model 'NETWORK'
    variable '_BEAV___6_TN_Upu_value'
}
curve {
    model 'NETWORK'
    variable '_CLIN___8_TN_Upu_value'
}
curve {
    model 'NETWORK'
    variable '_SALT___9_TN_Upu_value'
}
curve {
    model 'NETWORK'
    variable '_GLEN__12_TN_Upu_value'
}
curve {
    model 'GEN____1_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN____2_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN____3_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN____6_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN____8_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN____9_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN___12_SM'
    variable 'generator_PGen'
}
curve {
    model 'GEN____1_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN____2_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN____3_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN____6_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN____8_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN____9_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN___12_SM'
    variable 'generator_QGen'
}
curve {
    model 'GEN____1_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN____2_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN____3_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN____6_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN____8_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN____9_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN___12_SM'
    variable 'generator_omegaPu'
}
curve {
    model 'GEN____1_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'GEN____2_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'GEN____3_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'GEN____6_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'GEN____8_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'GEN____9_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'GEN___12_SM'
    variable 'generator_UStatorPu'
}
curve {
    model 'NETWORK'
    variable '_BEAV___6-BUS____7-1_AC_P2_value'
}
curve {
    model 'NETWORK'
    variable '_BUS____7-CLIN___8-1_AC_P1_value'
}
