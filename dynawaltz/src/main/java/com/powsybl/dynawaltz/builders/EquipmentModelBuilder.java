package com.powsybl.dynawaltz.builders;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface EquipmentModelBuilder<T, R extends EquipmentModelBuilder<T, R>> extends ModelBuilder<T> {

    //TODO quid de l'utilisation de cette interface
    R staticId(String staticId);

    R dynamicModelId(String dynamicModelId);

    R parameterSetId(String parameterSetId);

    T build();
}
