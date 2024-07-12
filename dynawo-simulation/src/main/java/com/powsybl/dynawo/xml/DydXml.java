/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.xml.DynawoSimulationConstants.DYD_FILENAME;

/**
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
public final class DydXml extends AbstractXmlDynawoSimulationWriter<DydDataSupplier> {

    private DydXml(String fileName) {
        super(fileName, "dynamicModelsArchitecture");
    }

    private DydXml() {
        this(DYD_FILENAME);
    }

    public static void write(Path workingDir, DydDataSupplier dataSupplier) throws IOException {
        new DydXml().createXmlFileFromDataSupplier(workingDir, dataSupplier);
    }

    public static void write(Path workingDir, String fileName, DydDataSupplier dataSupplier) throws IOException {
        new DydXml(fileName).createXmlFileFromDataSupplier(workingDir, dataSupplier);
    }

    @Override
    public void write(XMLStreamWriter writer, DydDataSupplier dataSupplier) throws XMLStreamException {
        // loop over the values of the map indexed by dynamicIds to write only once objects with the same dynamicId
        String parFileName = dataSupplier.getParFileName();
        //TODO merge all models in one parameter file ?
        if (parFileName == null || parFileName.isEmpty()) {
            for (BlackBoxModel model : dataSupplier.getBlackBoxDynamicModels()) {
                model.write(writer);
            }
            for (BlackBoxModel model : dataSupplier.getBlackBoxEventModels()) {
                model.write(writer);
            }
        } else {
            for (BlackBoxModel model : dataSupplier.getBlackBoxDynamicModels()) {
                model.write(writer, dataSupplier.getParFileName());
            }
            for (BlackBoxModel model : dataSupplier.getBlackBoxEventModels()) {
                model.write(writer, dataSupplier.getParFileName());
            }
        }
        for (MacroConnector macroConnector : dataSupplier.getMacroConnectors()) {
            macroConnector.write(writer);
        }
        for (MacroStaticReference macroStaticReference : dataSupplier.getMacroStaticReferences()) {
            macroStaticReference.write(writer);
        }
        for (MacroConnect macroConnect : dataSupplier.getMacroConnectList()) {
            macroConnect.write(writer);
        }
    }
}
