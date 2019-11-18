/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoCurves {

    private DynawoCurves() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeBusCurve(Bus b, XMLStreamWriter writer) throws XMLStreamException {
        writeCurve(writer, "NETWORK", b.getId() + "_Upu_value");
    }

    public static void writeGeneratorCurve(Generator g, XMLStreamWriter writer) throws XMLStreamException {
        writeCurve(writer, g.getId(), "generator_omegaPu");
        writeCurve(writer, g.getId(), "generator_PGen");
        writeCurve(writer, g.getId(), "generator_QGen");
        writeCurve(writer, g.getId(), "generator_UStatorPu");
        writeCurve(writer, g.getId(), "voltageRegulator_UcEfdPu");
        writeCurve(writer, g.getId(), "voltageRegulator_EfdPu");
    }

    public static void writeLoadCurve(Load l, XMLStreamWriter writer) throws XMLStreamException {
        writeCurve(writer, l.getId(), "load_PPu");
        writeCurve(writer, l.getId(), "load_QPu");
    }

    private static void writeCurve(XMLStreamWriter writer, String model, String variable) throws XMLStreamException {
        writer.writeEmptyElement("curve");
        writer.writeAttribute("model", model);
        writer.writeAttribute("variable", variable);
    }
}
