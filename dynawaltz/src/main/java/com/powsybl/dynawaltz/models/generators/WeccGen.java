/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.ConnectionPoint;
import com.powsybl.iidm.network.Generator;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class WeccGen extends AbstractEquipmentBlackBoxModel<Generator> {

    private final List<VarMapping> varsMapping;
    protected final String weccPrefix;

    public WeccGen(String dynamicModelId, Generator generator, String parameterSetId, String lib, String weccPrefix) {
        super(dynamicModelId, parameterSetId, generator, lib);
        this.weccPrefix = Objects.requireNonNull(weccPrefix);
        varsMapping = List.of(
                new VarMapping(weccPrefix + "_measurements_PPuSnRef", "p"),
                new VarMapping(weccPrefix + "_measurements_QPuSnRef", "q"),
                new VarMapping(weccPrefix + "_injector_state", "state"));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createTerminalMacroConnections(equipment, this::getVarConnectionsWith, context);
    }

    private List<VarConnection> getVarConnectionsWith(ConnectionPoint connected) {
        return List.of(new VarConnection(weccPrefix + "_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varsMapping;
    }
}
