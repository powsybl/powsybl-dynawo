package com.powsybl.dynawaltz.builders;

import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Same as DslEquipment but the equipment type could be different subtypes of the specified T type
 * (for example a load OR a generator with T as an Injection)
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DslFilteredEquipment<T extends Identifiable<?>> extends DslEquipment<T> {

    private final Predicate<IdentifiableType> typePredicate;

    public DslFilteredEquipment(String equipmentType, Predicate<IdentifiableType> typePredicate) {
        super(equipmentType);
        this.typePredicate = typePredicate;
    }

    @Override
    public void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        staticId = equipmentId;
        T equipment = equipmentSupplier.apply(staticId);
        if (equipment != null && typePredicate.test(equipment.getType())) {
            this.equipment = equipment;
        }
    }
}
