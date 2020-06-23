/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.powsybl.dynawo.DynawoContext;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoXmlContext {

    private final String parFile;

    private final Map<String, AtomicInteger> counters = new HashMap<>();

    public DynawoXmlContext(DynawoContext context) {
        Objects.requireNonNull(context);
        this.parFile = Paths.get(context.getDynawoParameters().getParametersFile()).getFileName().toString();
    }

    public String getParFile() {
        return parFile;
    }

    public int getIndex(String modelType, boolean increment) {
        AtomicInteger counter = counters.computeIfAbsent(modelType, k -> new AtomicInteger());
        return increment ? counter.getAndIncrement() : counter.get();
    }

    /*











    private DynawoXmlContext() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(DYD_FILENAME);

        XmlUtil.write(file, context, "dynamicModelsArchitecture", DynawoXmlContext::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        DydXmlWriterContext dydXmlWriterContext = new DydXmlWriterContext(context);
        try {
            // First write all dynamic models
            for (DynamicModel dynamicModel : context.getDynamicModels()) {
                // All dynamic models must be Dynawo abstract dynamic models
                AbstractDynawoDynamicModel dynawoDynamicModel = (AbstractDynawoDynamicModel) dynamicModel;
                dynawoDynamicModel.write(writer, dydXmlWriterContext);
            }

            // Write macro... objects referenced by the dynamic models written
            for (String macroConnectorId : dydXmlWriterContext.macroConnectorsUsed) {
                writeMacroConnector(writer, context.getMacroConnector(macroConnectorId));
            }
            for (String macroStaticReferenceId : dydXmlWriterContext.macroStaticReferencesUsed) {
                writeMacroStaticReference(writer, context.getMacroStaticReference(macroStaticReferenceId));
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeMacroConnector(XMLStreamWriter writer, MacroConnector macroConnector) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", macroConnector.getId());
        for (Connect connect : macroConnector.getConnections()) {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("var1", connect.getVar1());
            writer.writeAttribute("var2", connect.getVar2());
        }
        writer.writeEndElement();
    }

    private static void writeMacroStaticReference(XMLStreamWriter writer, MacroStaticReference macroStaticReference) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroStaticReference");
        writer.writeAttribute("id", macroStaticReference.getId());
        for (StaticRef staticRef : macroStaticReference.getStaticRefs()) {
            writer.writeEmptyElement(DYN_URI, "staticRef");
            writer.writeAttribute("var", staticRef.getVar());
            writer.writeAttribute("staticVar", staticRef.getStaticVar());
        }
        writer.writeEndElement();
    }

    public static class DydXmlWriterContext {
        DydXmlWriterContext(DynawoContext context) {
            parametersFile = Paths.get(context.getDynawoParameters().getParametersFile());
            parFile = parametersFile.getFileName().toString();
        }

        public void addMacroConnectorsUsed(String macroConnectorId) {
            macroConnectorsUsed.add(macroConnectorId);
        }

        public void addMacroStaticReferencesUsed(String macroStaticReferenceId) {
            macroStaticReferencesUsed.add(macroStaticReferenceId);
        }

        public String getParFile() {
            return parFile;
        }

        final Path parametersFile;
        final String parFile;
        final Set<String> macroConnectorsUsed = new HashSet<>();
        final Set<String> macroStaticReferencesUsed = new HashSet<>();
    }
     */
}
