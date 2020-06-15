/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.simulator.DynawoCurve;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoConstants.CRV_FILENAME;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class CurvesXml {

    private CurvesXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(CRV_FILENAME);

        XmlUtil.write(file, context, "curvesInput", CurvesXml::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        try {
            for (Curve curve : context.getCurves()) {
                DynawoCurve dynCurve = (DynawoCurve) curve;
                writer.writeEmptyElement(DYN_URI, "curve");
                writer.writeAttribute("model", dynCurve.getModelId());
                writer.writeAttribute("variable", dynCurve.getVariable());
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
