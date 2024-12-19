/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationContext;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
@FunctionalInterface
public interface XmlDynawoSimulationWriter {
    void createXmlFileFromContext(Path workingDir, DynawoSimulationContext context) throws IOException;
}
