/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.DynawoSimulationConstants.DYD_FILENAME;

/**
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
public final class DydXml extends AbstractXmlDynawoSimulationWriter {

    private DydXml() {
        super(DYD_FILENAME, "dynamicModelsArchitecture");
    }

    public static void write(Path workingDir, DynawoSimulationContext context) throws IOException {
        new DydXml().createXmlFileFromContext(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        // loop over the values of the map indexed by dynamicIds to write only once objects with the same dynamicId
        for (BlackBoxModel model : context.getBlackBoxDynamicModels()) {
            model.write(writer, context);
        }
        for (BlackBoxModel model : context.getBlackBoxEventModels()) {
            model.write(writer, context);
        }
        for (MacroConnector macroConnector : context.getMacroConnectors()) {
            macroConnector.write(writer);
        }
        for (MacroStaticReference macroStaticReference : context.getMacroStaticReferences()) {
            macroStaticReference.write(writer);
        }
        for (MacroConnect macroConnect : context.getMacroConnectList()) {
            macroConnect.write(writer);
        }
    }
}
