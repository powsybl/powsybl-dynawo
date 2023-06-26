package com.powsybl.dynawaltz.dsl

import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType

import java.util.function.Function
import java.util.function.Predicate

class DslVariousEquipment<T extends Identifiable> extends DslEquipment<T> {

    private final Predicate<IdentifiableType> typePredicate

    DslVariousEquipment(String equipmentType, Predicate<IdentifiableType> typePredicate) {
        super(equipmentType)
        this.typePredicate = typePredicate
    }

    @Override
    void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        staticId = equipmentId
        T equipment = equipmentSupplier(staticId)
        if(equipment && typePredicate.test(equipment.type)) {
            this.equipment = equipment
        }
    }
}
