/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.wecc;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.Generator;

import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class Wecc extends AbstractEquipmentBlackBoxModel<Generator> {

    private final List<VarMapping> varsMapping;
    protected final String weccPrefix;

    public Wecc(String dynamicModelId, Generator generator, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, generator, lib);
        //TODO refactor ?
        weccPrefix = lib.length() > 1 && Character.isUpperCase(lib.charAt(1))
                ? lib.substring(0, lib.indexOf("Wecc"))
                : lib.substring(0, lib.indexOf("Wecc")).toLowerCase();
        varsMapping = List.of(
                new VarMapping(weccPrefix + "_measurements_PPuSnRef", "p"),
                new VarMapping(weccPrefix + "_measurements_QPuSnRef", "q"),
                new VarMapping(weccPrefix + "_injector_state", "state"));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(BusUtils.getConnectableBusStaticId(equipment), BusModel.class, this::getVarConnectionsWith, context);
    }

    private List<VarConnection> getVarConnectionsWith(BusModel connected) {
        return List.of(new VarConnection(weccPrefix + "_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varsMapping;
    }
}
