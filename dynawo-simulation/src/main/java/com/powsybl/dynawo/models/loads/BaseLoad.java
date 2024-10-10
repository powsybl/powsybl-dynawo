/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.xml.MacroStaticReference;
import com.powsybl.iidm.network.Load;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseLoad extends AbstractLoad {

    private static final MacroStaticReference MACRO_STATIC_REFERENCE = MacroStaticReference.of("load",
            new VarMapping("load_PPu", "p"),
            new VarMapping("load_QPu", "q"),
            new VarMapping("load_state", "state"));

    protected BaseLoad(String dynamicModelId, Load load, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, load, parameterSetId, modelConfig, "load_terminal");
    }

    @Override
    public Optional<MacroStaticReference> getMacroStaticReference() {
        return Optional.of(MACRO_STATIC_REFERENCE);
    }

    @Override
    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection("load_switchOffSignal1", switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }
}
