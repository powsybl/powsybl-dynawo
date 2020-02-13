/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import java.util.List;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.inputs.model.crv.Curve;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CurvesXml implements DynawoCollectionXmlFile {

    @Override
    public String getFilename() {
        return DynawoConstants.CRV_FILENAME;
    }

    @Override
    public String getNamespacePrefix() {
        return DynawoXmlConstants.EMPTY_PREFIX;
    }

    @Override
    public String getCollectionTag() {
        return "curvesInput";
    }

    @Override
    public void writeCollection(XMLStreamWriter writer, List<?> curves) throws XMLStreamException {
        Objects.requireNonNull(writer);
        for (Object curve : curves) {
            assert curve instanceof Curve;
            writeCurve(writer, (Curve) curve);
        }
    }

    private static void writeCurve(XMLStreamWriter writer, Curve curve) throws XMLStreamException {
        writer.writeEmptyElement("curve");
        writer.writeAttribute("model", curve.getModel());
        writer.writeAttribute("variable", curve.getVariable());
    }

}
