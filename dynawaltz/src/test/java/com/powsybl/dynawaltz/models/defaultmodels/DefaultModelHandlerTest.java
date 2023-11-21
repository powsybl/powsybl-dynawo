/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.defaultmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.dynawaltz.models.loads.LoadModel;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DefaultModelHandlerTest {

    protected Network network = EurostagTutorialExample1Factory.create();
    protected DefaultModelsHandler defaultModelHandler = new DefaultModelsHandler();

    @Test
    void getSimpleDefaultModel() {
        Model model = defaultModelHandler.getDefaultModel(network.getGenerator("GEN"), GeneratorModel.class, false);
        assertNotNull(model);
        assertEquals("DefaultGenerator", model.getName());
    }

    @Test
    void getMultipleConfigDefaultModel() {
        Model model = defaultModelHandler.getDefaultModel(network.getBusBreakerView().getBus("NGEN"), EquipmentConnectionPoint.class, false);
        assertNotNull(model);
        assertEquals("DefaultEquipmentConnectionPoint", model.getName());
    }

    @Test
    void noInterfaceImplementationLog() {
        Generator gen = network.getGenerator("GEN");
        Model model =  defaultModelHandler.getDefaultModel(gen, LoadModel.class, false);
        assertNull(model);
    }

    @Test
    void noInterfaceImplementationException() {
        Generator gen = network.getGenerator("GEN");
        PowsyblException pe = assertThrows(PowsyblException.class, () -> defaultModelHandler.getDefaultModel(gen, LoadModel.class, true));
        assertEquals("Default model DefaultGenerator does not implement LoadModel interface", pe.getMessage());
    }

    @Test
    void noDynamicModelException() {
        VoltageLevel vl = network.getVoltageLevel("VLGEN");
        PowsyblException pe = assertThrows(PowsyblException.class, () -> defaultModelHandler.getDefaultModel(vl, LoadModel.class, true));
        assertEquals("No default model configuration for VOLTAGE_LEVEL", pe.getMessage());
    }

    @Test
    void noMultipleDynamicModelException() {
        Bus bus = network.getBusBreakerView().getBus("NGEN");
        PowsyblException pe = assertThrows(PowsyblException.class, () -> defaultModelHandler.getDefaultModel(bus, LoadModel.class, true));
        assertEquals("No default model configuration for BUS - LoadModel", pe.getMessage());
    }
}
