/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModelWithDynamicId;
import com.powsybl.dynawaltz.dynamicmodels.utils.MacroConnector;
import com.powsybl.dynawaltz.dynamicmodels.events.BlackBoxEventModel;

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
            for (BlackBoxModelWithDynamicId model : context.getBlackBoxModels()) {
                model.write(writer, context);
            }
            for (MacroConnector macroConnector : context.getMacroConnectors().stream().filter(distinctByKey(MacroConnector::getLibCouple)).collect(Collectors.toList())) {
                macroConnector.write(writer);
            }
            for (MacroStaticReference macroStaticReference : context.getMacroStaticReferences()) {
                macroStaticReference.write(writer);
            }
            List<MacroConnector> usedMacroConnectors = new ArrayList<>();
            for (Map.Entry<BlackBoxModelWithDynamicId, List<BlackBoxModel>> bbmMapping : context.getModelsConnections().entrySet()) {
                BlackBoxModelWithDynamicId bbm = bbmMapping.getKey();
                for (BlackBoxModel connectedBbm : bbmMapping.getValue()) {
                    MacroConnector macroConnector = context.getMacroConnector(bbm, connectedBbm);
                    if (!usedMacroConnectors.contains(macroConnector)) {
                        bbm.writeMacroConnect(writer, context, macroConnector, connectedBbm);
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
            for (BlackBoxEventModel model : context.getBlackBoxEventModels()) {
                model.write(writer, context);
            }
            for (MacroConnector macroConnector : context.getEventMacroConnectors()) {
                macroConnector.write(writer);
            }
            for (Map.Entry<BlackBoxEventModel, List<BlackBoxModel>> bbemMapping : context.getEventModelsConnections().entrySet()) {
                BlackBoxEventModel bbem = bbemMapping.getKey();
                for (BlackBoxModel connectedBbm : bbemMapping.getValue()) {
                    bbem.writeMacroConnect(writer, context, context.getEventMacroConnector(bbem, connectedBbm), connectedBbm);
                }
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
