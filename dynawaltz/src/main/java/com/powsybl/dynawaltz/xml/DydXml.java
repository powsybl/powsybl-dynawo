/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.MacroConnector;
import com.powsybl.dynawaltz.models.Model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.DYD_FILENAME;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public final class DydXml {

    private DydXml() {
    }

    public static void write(Path workingDir, DynaWaltzContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(DYD_FILENAME);

        XmlUtil.write(file, context, "dynamicModelsArchitecture", DydXml::write);
    }

    private static void write(XMLStreamWriter writer, DynaWaltzContext context) {
        writeDynamicModels(writer, context);
        writeEvents(writer, context);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private static void writeDynamicModels(XMLStreamWriter writer, DynaWaltzContext context) {

        try {
            // loop over the values of the map indexed by dynamicIds to write only once objects with the same dynamicId
            for (BlackBoxModel model : context.getBlackBoxModels()) {
                model.write(writer, context);
            }
            for (MacroConnector macroConnector : context.getMacroConnectors().stream().filter(distinctByKey(MacroConnector::getModelsConnectedNames)).collect(Collectors.toList())) {
                macroConnector.write(writer);
            }
            for (MacroStaticReference macroStaticReference : context.getMacroStaticReferences()) {
                macroStaticReference.write(writer);
            }
            List<MacroConnector> usedMacroConnectors = new ArrayList<>();
            for (Map.Entry<BlackBoxModel, List<Model>> bbmMapping : context.getModelsConnections().entrySet()) {
                BlackBoxModel bbm = bbmMapping.getKey();
                for (Model connected : bbmMapping.getValue()) {
                    MacroConnector macroConnector = context.getMacroConnector(bbm, connected);
                    if (!usedMacroConnectors.contains(macroConnector)) {
                        bbm.writeMacroConnect(writer, context, macroConnector, connected);
                        usedMacroConnectors.add(macroConnector);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeEvents(XMLStreamWriter writer, DynaWaltzContext context) {

        try {
            for (BlackBoxModel model : context.getBlackBoxEventModels()) {
                model.write(writer, context);
            }
            for (MacroConnector macroConnector : context.getEventMacroConnectors()) {
                macroConnector.write(writer);
            }
            for (Map.Entry<BlackBoxModel, List<Model>> bbmMapping : context.getEventModelsConnections().entrySet()) {
                BlackBoxModel event = bbmMapping.getKey();
                for (Model connected : bbmMapping.getValue()) {
                    event.writeMacroConnect(writer, context, context.getEventMacroConnector(event, connected), connected);
                }
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
