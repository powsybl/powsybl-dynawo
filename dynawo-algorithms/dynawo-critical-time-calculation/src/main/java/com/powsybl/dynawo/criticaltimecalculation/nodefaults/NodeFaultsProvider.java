/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.nodefaults;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.algorithms.NodeFaultEventData;
import com.powsybl.dynawo.criticaltimecalculation.json.CriticalTimeCalculationNodeFaultsJsonDeserializer;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public interface NodeFaultsProvider {
    List<NodeFaultEventData> getNodeFaults(Network network, ReportNode reportNode);

    default List<NodeFaultEventData> getNodeFaults(Network network, Map<Class<?>, Object> contextObjects) {
        return getNodeFaults(network, ReportNode.NO_OP);
    }

    default String asScript() {
        throw new UnsupportedOperationException("Serialization not supported for contingencies provider of type " + this.getClass().getName());
    }

    static NodeFaultsProvider getNodeFaultsProviderForJson(Path nodeFaultsPath) {
        return (n, r) -> new SupplierJsonDeserializer<>(
                new CriticalTimeCalculationNodeFaultsJsonDeserializer(() -> new NodeFaultsBuilder(n, r)))
                .deserialize(nodeFaultsPath);
    }

}
