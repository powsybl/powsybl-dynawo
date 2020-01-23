/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.par.DynawoParameter;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.Solver;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoSolverParameters {

    private DynawoSolverParameters() {
    }

    public static void writeParameterSets(XMLStreamWriter writer, Solver solver, int order,
        List<DynawoParameterSet> parameterSets)
        throws XMLStreamException {
        for (DynawoParameterSet parameterSet : parameterSets) {
            writeParameterSet(writer, solver, order, parameterSet);
        }
    }

    private static void writeParameterSet(XMLStreamWriter writer, Solver solver, int order,
        DynawoParameterSet parameterSet)
        throws XMLStreamException {
        String id = parameterSet.getId();
        writer.writeStartElement("set");
        writer.writeAttribute("id", id);
        if (solver.equals(Solver.IDA)) {
            writer.writeEmptyElement("par");
            writer.writeAttribute("type", "INT");
            writer.writeAttribute("name", "order");
            writer.writeAttribute("value", Integer.toString(order));
        }
        for (Entry<String, DynawoParameter> parameter : parameterSet.getParameters().entrySet()) {
            if (parameter.getValue().getName().equals("order") && solver.equals(Solver.IDA)) {
                continue;
            }
            writeParameter(writer, parameter.getValue());
        }
        writer.writeEndElement();
    }

    private static void writeParameter(XMLStreamWriter writer, DynawoParameter parameter) throws XMLStreamException {
        writer.writeEmptyElement("par");
        writer.writeAttribute("type", parameter.getType());
        writer.writeAttribute("name", parameter.getName());
        writer.writeAttribute("value", parameter.getValue());
    }
}
