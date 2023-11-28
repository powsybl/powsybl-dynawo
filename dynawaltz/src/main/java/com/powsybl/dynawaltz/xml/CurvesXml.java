/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzCurve;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.CRV_FILENAME;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class CurvesXml extends AbstractXmlDynawaltzWriter {

    private CurvesXml() {
        super(CRV_FILENAME, "curvesInput");
    }

    public static void write(Path workingDir, DynaWaltzContext context) throws IOException {
        new CurvesXml().createXmlFileFromContext(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        for (Curve curve : context.getCurves()) {
            DynaWaltzCurve dynCurve = (DynaWaltzCurve) curve;
            writer.writeEmptyElement(DYN_URI, "curve");
            writer.writeAttribute("model", dynCurve.getModelId());
            writer.writeAttribute("variable", dynCurve.getVariable());
        }
    }
}
