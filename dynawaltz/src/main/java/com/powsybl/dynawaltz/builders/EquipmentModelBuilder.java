package com.powsybl.dynawaltz.builders;

import com.powsybl.dynamicsimulation.DynamicModel;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface EquipmentModelBuilder<R extends EquipmentModelBuilder<R>> extends ModelBuilder<DynamicModel> {

    //TODO quid de l'utilisation de cette interface
    R staticId(String staticId);

    R dynamicModelId(String dynamicModelId);

    R parameterSetId(String parameterSetId);
}
