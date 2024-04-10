/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

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
public class TapChangerAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerAutomatonBuilder> {

    public static final String CATEGORY = "TAP_CHANGER";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    protected final BuilderEquipment<Load> load;
    protected TransformerSide side = TransformerSide.NONE;

    public static TapChangerAutomatonBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static TapChangerAutomatonBuilder of(Network network, Reporter reporter) {
        return new TapChangerAutomatonBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static TapChangerAutomatonBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static TapChangerAutomatonBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, TapChangerAutomatonBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new TapChangerAutomatonBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected TapChangerAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
        load = new BuilderEquipment<>(IdentifiableType.LOAD);
    }

    public TapChangerAutomatonBuilder staticId(String staticId) {
        load.addEquipment(staticId, network::getLoad);
        return self();
    }

    public TapChangerAutomatonBuilder side(TransformerSide side) {
        this.side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= load.checkEquipmentData(reporter);
    }

    @Override
    public TapChangerAutomaton build() {
        return isInstantiable() ? new TapChangerAutomaton(dynamicModelId, parameterSetId, load.getEquipment(), side, getLib()) : null;
    }

    @Override
    protected TapChangerAutomatonBuilder self() {
        return this;
    }
}
