/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.frequencysynchronizers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorModel;
import com.powsybl.dynawaltz.xml.ParametersXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * Special generators' frequency synchronizer used when an Infinite Bus is present in the model.
 *
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SetPoint extends AbstractFrequencySynchronizer {

    public SetPoint(List<SynchronizedGeneratorModel> synchronizedGenerators) {
        super(synchronizedGenerators);
    }

    @Override
    public String getLib() {
        return "SetPoint";
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "set");
        writer.writeAttribute("id", getParameterSetId());
        ParametersXml.writeParameter(writer, DOUBLE, "setPoint_Value0", Double.toString(1));
        writer.writeEndElement();
    }

    private List<VarConnection> getVarConnectionsWithSynchronizedGenerator(SynchronizedGeneratorModel connected) {
        return connected.getSetPointVarConnections();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) throws PowsyblException {
        for (SynchronizedGeneratorModel gen : synchronizedGenerators) {
            createMacroConnections(gen, getVarConnectionsWithSynchronizedGenerator(gen), context);
        }
    }
}
