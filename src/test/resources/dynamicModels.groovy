/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Generator

for (Load load : network.loads) {
    LoadAlphaBeta {
        staticId load.id
        // dynamicModelId "BBM_" + load.id (dynamicModelId could be optional and equal to staticId)
        parameterSetId "default"
       // the parameterSetId is a string that points to the requested entry in the aggregated par file defined in config.yml
        parameters {
            load_alpha 1.5
            load_beta 2.5
        }
    }
}

for (Generator gen : network.generators) {
    GeneratorSynchronousFourWindingsProportionalRegulations {
        staticId gen.id
        dynamicModelId "BBM_" + gen.id
        parameterSetId "default"
        parameters {
            generator_ExcitationPu 1
            generator_MdPuEfd 0
            generator_DPu 0
            generator_H 5.4000000000000004
            generator_RaPu 0.0027959999999999999
            generator_XlPu 0.20200000000000001
            generator_XdPu 2.2200000000000002
            generator_XpdPu 0.38400000000000001
            generator_XppdPu 0.26400000000000001
            generator_Tpd0 8.0939999999999994
            generator_Tppd0 0.080000000000000002
            generator_XqPu 2.2200000000000002
            generator_XpqPu 0.39300000000000002
            generator_XppqPu 0.26200000000000001
            generator_Tpq0 1.5720000000000001
            generator_Tppq0 0.084000000000000005
            generator_UNom 24
            generator_SNom 1211
            generator_PNomTurb 1090
            generator_PNomAlt 1090
            generator_SnTfo 1211
            generator_UNomHV 69
            generator_UNomLV 24
            generator_UBaseHV 69
            generator_UBaseLV 24
            generator_XTfPu 0.1
            voltageRegulator_LagEfdMax 0
            voltageRegulator_LagEfdMin 0
            voltageRegulator_EfdMinPu -5
            voltageRegulator_EfdMaxPu 5
            voltageRegulator_UsRefMinPu 0.8
            voltageRegulator_UsRefMaxPu 1.2
            voltageRegulator_Gain 20
            governor_KGover 5
            governor_PMin 0
            governor_PMax 1090
            governor_PNom 1090
            URef_ValueIn 0
            Pm_ValueIn 0
        }
    }
    OmegaRef {
        generatorDynamicModelId "BBM_" + gen.id
    }
}
