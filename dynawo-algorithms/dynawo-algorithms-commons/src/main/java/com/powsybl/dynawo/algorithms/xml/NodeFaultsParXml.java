/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms.xml;

import com.powsybl.dynawo.algorithms.NodeFaultEventModels;
import com.powsybl.dynawo.xml.ParametersXml;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_PREFIX;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class NodeFaultsParXml {
    private NodeFaultsParXml() {
    }

    public static void write(Path workingDir, List<NodeFaultEventModels> eventModels) {
        Objects.requireNonNull(workingDir);
        for (NodeFaultEventModels model : eventModels) {
            ParametersXml.write(model.eventParameters(), createParFileName(model), workingDir, DYN_PREFIX);
        }
    }

    public static String createParFileName(NodeFaultEventModels model) {
        return model.getId() + ".par";
    }
}
