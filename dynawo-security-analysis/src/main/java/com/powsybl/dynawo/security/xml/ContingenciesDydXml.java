/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security.xml;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.security.ContingencyEventModels;
import com.powsybl.dynawo.security.SecurityAnalysisContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingenciesDydXml {

    private ContingenciesDydXml() {
    }

    public static void write(Path workingDir, SecurityAnalysisContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        for (ContingencyEventModels model : context.getContingencyEventModels()) {
            Path file = workingDir.resolve(createDydFileName(model));
            XmlUtil.write(file, context, "dynamicModelsArchitecture", ContingenciesDydXml::writeEvent, model);
        }
    }

    private static void writeEvent(XMLStreamWriter writer, DynawoSimulationContext context, ContingencyEventModels model) throws XMLStreamException {
        for (BlackBoxModel ev : model.eventModels()) {
            ev.write(writer, ContingenciesParXml.createParFileName(model));
        }
        for (MacroConnector mcr : model.macroConnectorsMap().values()) {
            mcr.write(writer);
        }
        for (MacroConnect mc : model.macroConnectList()) {
            mc.write(writer);
        }
    }

    public static String createDydFileName(ContingencyEventModels contingency) {
        return contingency.getId() + ".dyd";
    }
}
