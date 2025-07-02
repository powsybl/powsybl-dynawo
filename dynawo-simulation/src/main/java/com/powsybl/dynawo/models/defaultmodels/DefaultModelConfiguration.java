/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.defaultmodels;

import com.powsybl.dynawo.models.InjectionModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.buses.ActionConnectionPoint;
import com.powsybl.dynawo.models.buses.DefaultActionConnectionPoint;
import com.powsybl.dynawo.models.buses.DefaultEquipmentConnectionPoint;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.generators.DefaultGenerator;
import com.powsybl.dynawo.models.hvdc.DefaultHvdc;
import com.powsybl.dynawo.models.hvdc.HvdcModel;
import com.powsybl.dynawo.models.lines.DefaultLine;
import com.powsybl.dynawo.models.lines.LineModel;
import com.powsybl.dynawo.models.loads.DefaultLoad;
import com.powsybl.dynawo.models.shunts.DefaultShunt;
import com.powsybl.dynawo.models.shunts.ShuntModel;
import com.powsybl.dynawo.models.svarcs.DefaultStaticVarCompensator;
import com.powsybl.dynawo.models.transformers.DefaultTransformer;
import com.powsybl.dynawo.models.transformers.TransformerModel;
import com.powsybl.iidm.network.IdentifiableType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum DefaultModelConfiguration {

    ACTION_CONNECTION_POINT_BBS(IdentifiableType.BUSBAR_SECTION,
            ActionConnectionPoint.class,
            new DefaultModelFactory<>(DefaultActionConnectionPoint::new)),
    ACTION_CONNECTION_POINT(IdentifiableType.BUS,
            ActionConnectionPoint.class,
            new DefaultModelFactory<>(DefaultActionConnectionPoint::new)),
    EQUIPMENT_CONNECTION_POINT(IdentifiableType.BUS,
            EquipmentConnectionPoint.class,
            staticId -> DefaultEquipmentConnectionPoint.getDefaultModel()),
    GENERATOR(IdentifiableType.GENERATOR,
            InjectionModel.class,
            new DefaultModelFactory<>(DefaultGenerator::new)),
    HVDC(IdentifiableType.HVDC_LINE,
            HvdcModel.class,
            new DefaultModelFactory<>(DefaultHvdc::new)),
    LINE(IdentifiableType.LINE,
            LineModel.class,
            new DefaultModelFactory<>(DefaultLine::new)),
    LOAD(IdentifiableType.LOAD,
            InjectionModel.class,
            new DefaultModelFactory<>(DefaultLoad::new)),
    SHUNT_COMPENSATOR(IdentifiableType.SHUNT_COMPENSATOR,
            ShuntModel.class,
            new DefaultModelFactory<>(DefaultShunt::new)),
    STATIC_VAR_COMPENSATOR(IdentifiableType.STATIC_VAR_COMPENSATOR,
            InjectionModel.class,
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
