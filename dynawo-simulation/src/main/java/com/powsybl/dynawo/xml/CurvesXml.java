/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.curves.DynawoCurve;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.xml.DynawoSimulationConstants.CRV_FILENAME;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class CurvesXml extends AbstractXmlDynawoSimulationWriter<DynawoSimulationContext> {

    private CurvesXml() {
        super(CRV_FILENAME, "curvesInput");
    }

    public static void write(Path workingDir, DynawoSimulationContext context) throws IOException {
        new CurvesXml().createXmlFileFromDataSupplier(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        for (DynawoCurve dynCurve : context.getCurves()) {
            writer.writeEmptyElement(DYN_URI, "curve");
            writer.writeAttribute("model", dynCurve.getModelId());
            writer.writeAttribute("variable", dynCurve.getVariable());
        }
    }
}
