/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.defaultmodels;

import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.buses.*;
import com.powsybl.dynawaltz.models.generators.DefaultGenerator;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.dynawaltz.models.hvdc.DefaultHvdc;
import com.powsybl.dynawaltz.models.hvdc.HvdcModel;
import com.powsybl.dynawaltz.models.lines.DefaultLine;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.models.loads.DefaultLoad;
import com.powsybl.dynawaltz.models.loads.LoadModel;
import com.powsybl.dynawaltz.models.shunts.DefaultShunt;
import com.powsybl.dynawaltz.models.shunts.ShuntModel;
import com.powsybl.dynawaltz.models.svarcs.DefaultStaticVarCompensator;
import com.powsybl.dynawaltz.models.svarcs.StaticVarCompensatorModel;
import com.powsybl.dynawaltz.models.transformers.DefaultTransformer;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.IdentifiableType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum DefaultModelConfiguration {

    ACTION_CONNECTION_POINT(IdentifiableType.BUS,
            ActionConnectionPoint.class,
            new DefaultModelFactory<>(DefaultActionConnectionPoint::new)),
    EQUIPMENT_CONNECTION_POINT(IdentifiableType.BUS,
            EquipmentConnectionPoint.class,
            DefaultEquipmentConnectionPoint::getDefaultModel),
    GENERATOR(IdentifiableType.GENERATOR,
            GeneratorModel.class,
            new DefaultModelFactory<>(DefaultGenerator::new)),
    HVDC(IdentifiableType.HVDC_LINE,
            HvdcModel.class,
            new DefaultModelFactory<>(DefaultHvdc::new)),
    LINE(IdentifiableType.LINE,
            LineModel.class,
            new DefaultModelFactory<>(DefaultLine::new)),
    LOAD(IdentifiableType.LOAD,
            LoadModel.class,
            new DefaultModelFactory<>(DefaultLoad::new)),
    SHUNT_COMPENSATOR(IdentifiableType.SHUNT_COMPENSATOR,
            ShuntModel.class,
            new DefaultModelFactory<>(DefaultShunt::new)),
    STATIC_VAR_COMPENSATOR(IdentifiableType.STATIC_VAR_COMPENSATOR,
            StaticVarCompensatorModel.class,
            new DefaultModelFactory<>(DefaultStaticVarCompensator::new)),
    TWO_WINDINGS_TRANSFORMER(IdentifiableType.TWO_WINDINGS_TRANSFORMER,
            TransformerModel.class,
            new DefaultModelFactory<>(DefaultTransformer::new));

    private final IdentifiableType identifiableType;
    private final Class<? extends Model> equipmentClass;
    private final DefaultModelFactoryInterface<? extends Model> factory;

    DefaultModelConfiguration(IdentifiableType identifiableType, Class<? extends Model> equipmentClass, DefaultModelFactoryInterface<? extends Model> factory) {
        this.identifiableType = identifiableType;
        this.equipmentClass = equipmentClass;
        this.factory = factory;
    }

    public IdentifiableType getIdentifiableType() {
        return identifiableType;
    }

    public Class<? extends Model> getEquipmentClass() {
        return equipmentClass;
    }

    public Model getDefaultModel(String staticId) {
        return factory.getDefaultModel(staticId);
    }
}
