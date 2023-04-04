/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.buses.DefaultBusModel;
import com.powsybl.dynawaltz.models.lines.DefaultLineModel;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.models.shunts.DefaultShuntModel;
import com.powsybl.dynawaltz.models.shunts.ShuntModel;
import com.powsybl.dynawaltz.models.transformers.DefaultTransformerModel;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.IdentifiableType;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class NetworkModel {

    private final Map<IdentifiableType, Class<? extends Model>> powSyBlTypeToModel = new EnumMap<>(IdentifiableType.class);
    private final Map<Class<? extends Model>, DefaultModelFactory<? extends Model>> factoryMap;

    public NetworkModel() {
        factoryMap = Map.of(BusModel.class, new DefaultModelFactory<BusModel>(DefaultBusModel::new),
                LineModel.class, new DefaultModelFactory<LineModel>(DefaultLineModel::new),
                ShuntModel.class, new DefaultModelFactory<ShuntModel>(DefaultShuntModel::new),
                TransformerModel.class, new DefaultModelFactory<TransformerModel>(DefaultTransformerModel::new));

        //TODO use a static method in EquipmentModel interface instead ?
        powSyBlTypeToModel.put(IdentifiableType.BUS, BusModel.class);
        powSyBlTypeToModel.put(IdentifiableType.LINE, LineModel.class);
        powSyBlTypeToModel.put(IdentifiableType.SHUNT_COMPENSATOR, ShuntModel.class);
        powSyBlTypeToModel.put(IdentifiableType.TWO_WINDINGS_TRANSFORMER, TransformerModel.class);
    }

    public <T extends Model> T getDefaultModel(String staticId, Class<T> clazz) {
        DefaultModelFactory<T> dmf = (DefaultModelFactory<T>) factoryMap.get(clazz);
        if (dmf != null) {
            return dmf.getDefaultModel(staticId);
        }
        throw new PowsyblException("Default model not implemented for " + clazz.getSimpleName());
    }

    public <T extends Model> T getDefaultModel(String staticId, IdentifiableType equipmentType, Class<T> connectableClass) {

        Class<? extends Model> equipmentClass = powSyBlTypeToModel.get(equipmentType);
        DefaultModelFactory<? extends Model> dmf = factoryMap.get(equipmentClass);
        if (dmf != null) {
            Model defaultModel = dmf.getDefaultModel(staticId);
            if (connectableClass.isInstance(defaultModel)) {
                return connectableClass.cast(defaultModel);
            }
            throw new PowsyblException("Default model does not implement " + connectableClass.getSimpleName() + " interface");
        }
        throw new PowsyblException("Default model not implemented for " + equipmentClass.getSimpleName());
    }
}
