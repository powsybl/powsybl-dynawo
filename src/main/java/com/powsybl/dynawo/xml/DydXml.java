/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dynamicmodels.AbstractBlackBoxModel;
import com.powsybl.dynawo.events.AbstractBlackBoxEventModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoConstants.DYD_FILENAME;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public final class DydXml {

    private DydXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(DYD_FILENAME);

        XmlUtil.write(file, context, "dynamicModelsArchitecture", DydXml::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        writeDynamicModels(writer, context);
        writeEvents(writer, context);
    }

    private static void writeDynamicModels(XMLStreamWriter writer, DynawoContext context) {
        DynawoXmlContext xmlContext = new DynawoXmlContext(context);

        try {
            for (DynamicModel model : context.getDynamicModels()) {
                AbstractBlackBoxModel dynawoModel = (AbstractBlackBoxModel) model;
                dynawoModel.write(writer, xmlContext);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeEvents(XMLStreamWriter writer, DynawoContext context) {
        DynawoXmlContext xmlContext = new DynawoXmlContext(context);

        try {
            for (EventModel model : context.getEventModels()) {
                AbstractBlackBoxEventModel dynawoModel = (AbstractBlackBoxEventModel) model;
                dynawoModel.write(writer, xmlContext);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
