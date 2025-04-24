/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms.xml;

import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_PREFIX;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ContingenciesParXml {

    private ContingenciesParXml() {
    }

    public static void write(Path workingDir, List<ContingencyEventModels> eventModels) {
        Objects.requireNonNull(workingDir);
        for (ContingencyEventModels model : eventModels) {
            ParametersXml.write(model.eventParameters(), createParFileName(model), workingDir, DYN_PREFIX);
        }
    }

    public static String createParFileName(ContingencyEventModels contingency) {
        return contingency.getId() + ".par";
    }
}
