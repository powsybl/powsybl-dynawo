/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.simulator.DynawoCurve;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CurvesXmlTest extends DynawoTestUtil {

    @Test
    public void writeCurve() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();

        Network network = Network.create("test", "test");

        List<Curve> curves = Arrays.asList(new DynawoCurve("NETWORK", "busId_Upu_value"),
                                            new DynawoCurve("genId", "generator_omegaPu"),
                                            new DynawoCurve("genId", "generator_PGen"),
                                            new DynawoCurve("genId", "generator_UStatorPu"),
                                            new DynawoCurve("genId", "voltageRegulator_UcEfdP"),
                                            new DynawoCurve("genId", "voltageRegulator_EfdPu"));
        DynawoContext context = new DynawoContext(network, curves, parameters, dynawoParameters);

        CurvesXml.write(tmpDir, context);
        validate(tmpDir.resolve(DynawoConstants.CRV_FILENAME), "curvesInput");
    }

}
