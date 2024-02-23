/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security.xml;

import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.security.ContingencyEventModels;
import com.powsybl.dynawo.security.SecurityAnalysisContext;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_PREFIX;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingenciesParXml {

    private ContingenciesParXml() {
    }

    public static void write(Path workingDir, SecurityAnalysisContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        for (ContingencyEventModels model : context.getContingencyEventModels()) {
            ParametersXml.write(model.eventParameters(), createParFileName(model), workingDir, DYN_PREFIX);
        }
    }

    public static String createParFileName(ContingencyEventModels contingency) {
        return contingency.getId() + ".par";
    }
}
