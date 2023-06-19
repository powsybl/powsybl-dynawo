package com.powsybl.dynawaltz.dsl.models.builders

import com.powsybl.dsl.DslException
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.ShuntCompensator
import com.powsybl.iidm.network.TwoWindingsTransformer

final class BuildersUtil {

    private BuildersUtil() {
    }

    static ShuntCompensator getShuntCompensator(Network network, String shuntId) {
        def shunt = network.getShuntCompensator(shuntId)
        if (!shunt) {
            throw new DslException("Shunt static id unknown: $shuntId")
        }
        shunt
    }

    static TwoWindingsTransformer getTwoWindingsTransformerorThrows(Network network, String transformerStaticId) {
        def transformer = network.getTwoWindingsTransformer(transformerStaticId)
        if (!transformer) {
            throw new DslException("Transformer static id unknown: $transformerStaticId")
        }
        transformer
    }
    static TwoWindingsTransformer getTwoWindingsTransformer(Network network, String transformerStaticId) {
        network.getTwoWindingsTransformer(transformerStaticId)
    }


    static Bus getBus(Network network, String busStaticId) {
        def bus = network.getBusBreakerView().getBus(busStaticId)
        if (!bus) {
            throw new DslException("Bus static id unknown: $busStaticId")
        }
        bus
    }
}
