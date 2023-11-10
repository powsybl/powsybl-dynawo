package error

import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Load

for (Generator equipment : network.generators) {
    if (equipment.energySource.name() == "HYDRO") {
        GeneratorSynchronousThreeWindingsGoverNordicVRNordic {
            staticId equipment.id
            parameterSetId  "Nordic" + equipment.id
        }
    } else if (equipment.energySource.name() == "THERMAL") {
    GeneratorSynchronousFourWindingsPmConstVRNordic {
        staticId equipment.id
        parameterSetId  "Nordic" + equipment.id
    }
    } else if (equipment.energySource.name() == "OTHER") {
        GeneratorSynchronousThreeWindingsPmConstVRNordic {
            staticId equipment.id
            parameterSetId  "Nordic" + equipment.id
        }
    }
}

for (Load equipment : network.loads) {
    LoadAlphaBeta {
        staticId equipment.id
        parameterSetId  "LAB"
    }
}
