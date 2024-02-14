/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerAutomationSystemBuilder extends AbstractAutomationSystemModelBuilder<TapChangerAutomationSystemBuilder> {

    private static final String CATEGORY = "tapChangers";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Load> load;
    protected TransformerSide side = TransformerSide.NONE;

    public static TapChangerAutomationSystemBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static TapChangerAutomationSystemBuilder of(Network network, Reporter reporter) {
        return new TapChangerAutomationSystemBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static TapChangerAutomationSystemBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static TapChangerAutomationSystemBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, TapChangerAutomationSystemBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new TapChangerAutomationSystemBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected TapChangerAutomationSystemBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
        load = new BuilderEquipment<>(IdentifiableType.LOAD);
    }

    public TapChangerAutomationSystemBuilder staticId(String staticId) {
        load.addEquipment(staticId, network::getLoad);
        return self();
    }

    public TapChangerAutomationSystemBuilder side(TransformerSide side) {
        this.side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= load.checkEquipmentData(reporter);
    }

    @Override
    public TapChangerAutomationSystem build() {
        return isInstantiable() ? new TapChangerAutomationSystem(dynamicModelId, parameterSetId, load.getEquipment(), side, getLib()) : null;
    }

    @Override
    protected TapChangerAutomationSystemBuilder self() {
        return this;
    }
}
