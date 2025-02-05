/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.dynawo.xml.MacroStaticReference;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class FinalStepModels implements DydDataSupplier {

    private final List<BlackBoxModel> dynamicModels;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final List<MacroConnect> macroConnectList = new ArrayList<>();
    private final Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();

    public FinalStepModels(DynawoSimulationContext context, List<BlackBoxModel> dynamicModels,
                           Predicate<BlackBoxModel> macroStaticDuplicatePredicate,
                           Predicate<String> macroConnectorDuplicatePredicate,
                           Consumer<ParametersSet> parametersAdder) {
        this.dynamicModels = dynamicModels;
        MacroConnectionsAdder macroConnectionsAdder = MacroConnectionsAdder.createFrom(context, macroConnectList::add,
                (n, f) -> {
                    if (macroConnectorDuplicatePredicate.test(n)) {
                        macroConnectorsMap.computeIfAbsent(n, f);
                    }
                });
        // Write macro connection
        for (BlackBoxModel bbm : dynamicModels) {
            if (macroStaticDuplicatePredicate.test(bbm)) {
                macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            }
            bbm.createMacroConnections(macroConnectionsAdder);
            bbm.createDynamicModelParameters(parametersAdder);
        }
    }

    @Override
    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return dynamicModels;
    }

    @Override
    public Collection<MacroConnector> getMacroConnectors() {
        return macroConnectorsMap.values();
    }

    @Override
    public Collection<MacroStaticReference> getMacroStaticReferences() {
        return macroStaticReferences.values();
    }

    @Override
    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }
}
