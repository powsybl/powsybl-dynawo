/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.defaultmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.buses.DefaultBusModel;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.dynawaltz.models.generators.DefaultGeneratorModel;
import com.powsybl.dynawaltz.models.hvdc.DefaultHvdcModel;
import com.powsybl.dynawaltz.models.hvdc.HvdcModel;
import com.powsybl.dynawaltz.models.lines.DefaultLineModel;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.models.loads.DefaultLoadModel;
import com.powsybl.dynawaltz.models.loads.LoadModel;
import com.powsybl.dynawaltz.models.shunts.DefaultShuntModel;
import com.powsybl.dynawaltz.models.shunts.ShuntModel;
import com.powsybl.dynawaltz.models.svarcs.DefaultStaticVarCompensatorModel;
import com.powsybl.dynawaltz.models.svarcs.StaticVarCompensatorModel;
import com.powsybl.dynawaltz.models.transformers.DefaultTransformerModel;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultModelsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModelsHandler.class);

    private final Map<IdentifiableType, Class<? extends Model>> powSyBlTypeToModel = new EnumMap<>(IdentifiableType.class);
    private final Map<Class<? extends Model>, DefaultModelFactory<? extends Model>> factoryMap;

    public DefaultModelsHandler() {
        factoryMap = Map.of(BusModel.class, new DefaultModelFactory<BusModel>(DefaultBusModel::new),
                GeneratorModel.class, new DefaultModelFactory<GeneratorModel>(DefaultGeneratorModel::new),
                HvdcModel.class, new DefaultModelFactory<HvdcModel>(DefaultHvdcModel::new),
                LineModel.class, new DefaultModelFactory<LineModel>(DefaultLineModel::new),
                LoadModel.class, new DefaultModelFactory<LoadModel>(DefaultLoadModel::new),
                ShuntModel.class, new DefaultModelFactory<ShuntModel>(DefaultShuntModel::new),
                StaticVarCompensatorModel.class, new DefaultModelFactory<StaticVarCompensatorModel>(DefaultStaticVarCompensatorModel::new),
                TransformerModel.class, new DefaultModelFactory<TransformerModel>(DefaultTransformerModel::new));

        powSyBlTypeToModel.put(IdentifiableType.BUS, BusModel.class);
        powSyBlTypeToModel.put(IdentifiableType.GENERATOR, GeneratorModel.class);
        powSyBlTypeToModel.put(IdentifiableType.HVDC_LINE, HvdcModel.class);
        powSyBlTypeToModel.put(IdentifiableType.LINE, LineModel.class);
        powSyBlTypeToModel.put(IdentifiableType.LOAD, LoadModel.class);
        powSyBlTypeToModel.put(IdentifiableType.SHUNT_COMPENSATOR, ShuntModel.class);
        powSyBlTypeToModel.put(IdentifiableType.STATIC_VAR_COMPENSATOR, StaticVarCompensatorModel.class);
        powSyBlTypeToModel.put(IdentifiableType.TWO_WINDINGS_TRANSFORMER, TransformerModel.class);
    }

    public <T extends Model> T getDefaultModel(String staticId, Class<T> clazz) {
        DefaultModelFactory<T> dmf = (DefaultModelFactory<T>) factoryMap.get(clazz);
        if (dmf != null) {
            return dmf.getDefaultModel(staticId);
        }
        throw new PowsyblException("Default model not implemented for " + clazz.getSimpleName());
    }

    public <T extends Model> T getDefaultModel(Identifiable<?> equipment, Class<T> connectableClass) {
        return getDefaultModel(equipment, connectableClass, true);
    }

    public <T extends Model> T getDefaultModel(Identifiable<?> equipment, Class<T> connectableClass, boolean throwException) {

        Class<? extends Model> equipmentClass = powSyBlTypeToModel.get(equipment.getType());
        if (equipmentClass == null) {
            throw new PowsyblException("No dynamic model associated with " + equipment.getType());
        }
        DefaultModelFactory<? extends Model> dmf = factoryMap.get(equipmentClass);
        if (dmf != null) {
            Model defaultModel = dmf.getDefaultModel(equipment.getId());
            if (connectableClass.isInstance(defaultModel)) {
                return connectableClass.cast(defaultModel);
            }
            if (throwException) {
                throw new PowsyblException("Default model " + defaultModel.getClass().getSimpleName() + " does not implement " + connectableClass.getSimpleName() + " interface");
            } else {
                LOGGER.warn("Default model {} does not implement {} interface", defaultModel.getClass().getSimpleName(), connectableClass.getSimpleName());
                return null;
            }
        }
        if (throwException) {
            throw new PowsyblException("Default model not implemented for " + equipmentClass.getSimpleName());
        } else {
            LOGGER.warn("Default model not implemented for {}", equipmentClass.getSimpleName());
            return null;
        }
    }
}
