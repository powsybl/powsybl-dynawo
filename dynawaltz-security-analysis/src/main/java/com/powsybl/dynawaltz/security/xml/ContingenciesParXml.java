/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.security.xml;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.security.ContingencyEventModels;
import com.powsybl.dynawaltz.security.SecurityAnalysisContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.ParametersXml.PARAMETERS_SET_ELEMENT_NAME;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingenciesParXml {

    private ContingenciesParXml() {
    }

    public static void write(Path workingDir, SecurityAnalysisContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        for (ContingencyEventModels model : context.getContingencyEventModels()) {
            Path file = workingDir.resolve(createParFileName(model.getContingency()));
            XmlUtil.write(file, context, PARAMETERS_SET_ELEMENT_NAME, ContingenciesParXml::writeEvent, model);
        }
    }

    private static void writeEvent(XMLStreamWriter writer, DynaWaltzContext context, ContingencyEventModels model) throws XMLStreamException {
        for (BlackBoxModel ev : model.getEventModels()) {
            ev.writeParameters(writer, context);
        }
    }

    public static String createParFileName(Contingency contingency) {
        return contingency.getId() + ".par";
    }
}
