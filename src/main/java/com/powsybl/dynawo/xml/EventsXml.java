/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.events.AbstractBlackBoxEventModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoConstants.DYD_FILENAME;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class EventsXml {

    private EventsXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(DYD_FILENAME);

        XmlUtil.write(file, context, "dynamicModelsArchitecture", EventsXml::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        DynawoXmlContext xmlContext = new DynawoXmlContext(context);

        try {
            for (EventModel model : context.geteventModels()) {
                AbstractBlackBoxEventModel dynawoModel = (AbstractBlackBoxEventModel) model;
                dynawoModel.write(writer, xmlContext);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

}