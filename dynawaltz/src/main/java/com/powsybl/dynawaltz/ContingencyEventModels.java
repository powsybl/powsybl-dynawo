/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.MacroConnect;
import com.powsybl.dynawaltz.models.MacroConnector;

import java.util.List;
import java.util.Map;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class ContingencyEventModels {

    private final Contingency contingency;
    private final List<BlackBoxModel> eventModels;
    private final Map<String, MacroConnector> macroConnectorsMap;
    private final List<MacroConnect> macroConnectList;

    public ContingencyEventModels(Contingency contingency, List<BlackBoxModel> eventModels, Map<String, MacroConnector> macroConnectorsMap, List<MacroConnect> macroConnectList) {
        this.contingency = contingency;
        this.eventModels = eventModels;
        this.macroConnectorsMap = macroConnectorsMap;
        this.macroConnectList = macroConnectList;
    }

    public ContingencyEventModels(Contingency contingency, List<BlackBoxModel> eventModels) {
        this.contingency = contingency;
        this.eventModels = eventModels;
        this.macroConnectorsMap = null;
        this.macroConnectList = null;
    }

    public String getId() {
        return contingency.getId();
    }

    public Contingency getContingency() {
        return contingency;
    }

    public List<BlackBoxModel> getEventModels() {
        return eventModels;
    }

    public Map<String, MacroConnector> getMacroConnectorsMap() {
        return macroConnectorsMap;
    }

    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }
}
