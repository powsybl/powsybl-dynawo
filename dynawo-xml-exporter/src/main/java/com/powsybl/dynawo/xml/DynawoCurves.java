/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.crv.DynawoCurve;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoCurves {

    private DynawoCurves() {
    }

    public static void writeCurves(XMLStreamWriter writer, List<DynawoCurve> curves) throws XMLStreamException {
        for (DynawoCurve curve : curves) {
            writeCurve(writer, curve);
        }
    }

    private static void writeCurve(XMLStreamWriter writer, DynawoCurve curve) throws XMLStreamException {
        String model = curve.getModel();
        String variable = curve.getVariable();
        writeCurve(writer, model, variable);
    }

    private static void writeCurve(XMLStreamWriter writer, String model, String variable) throws XMLStreamException {
        writer.writeEmptyElement("curve");
        writer.writeAttribute("model", model);
        writer.writeAttribute("variable", variable);
    }
}
