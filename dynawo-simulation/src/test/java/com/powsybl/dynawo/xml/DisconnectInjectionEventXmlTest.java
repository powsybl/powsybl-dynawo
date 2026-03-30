/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.generators.GridFormingConverterBuilder;
import com.powsybl.dynawo.models.generators.InertialGridBuilder;
import com.powsybl.dynawo.models.generators.WeccBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.NGEN;
import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.VLGEN;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DisconnectInjectionEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithLFResults();
        addGenerator("GEN2");
        addGenerator("GEN3");
        addGenerator("GEN4");
    }

    private void addGenerator(String genId) {
        network.getVoltageLevel(VLGEN).newGenerator()
                .setId(genId)
                .setBus(NGEN)
                .setConnectableBus(NGEN)
                .setMinP(-9999.99)
                .setMaxP(9999.99)
                .setVoltageRegulatorOn(true)
                .setTargetV(24.5)
                .setTargetP(607.0)
                .setTargetQ(301.0)
                .add();
        network.getGenerator(genId).getTerminal()
                .setP(-605.558349609375)
                .setQ(-225.2825164794922);
    }

    @Override
    protected DynawoSimulationContext.Builder setupDynawoContextBuilder() {
        return super.setupDynawoContextBuilder().currentVersion(DynawoVersion.createFromString("1.7.0"));
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .staticId("GEN")
                .build());
        dynamicModels.add(InertialGridBuilder.of(network)
                .staticId("GEN2")
                .build());
        dynamicModels.add(BaseLoadBuilder.of(network)
                .staticId("LOAD")
                .build());
        dynamicModels.add(WeccBuilder.of(network, "WT4AWeccCurrentSource")
                .staticId("GEN3")
                .build());
        dynamicModels.add(GridFormingConverterBuilder.of(network, "GridFormingConverterDroopControl")
                .staticId("GEN4")
                .parameterSetId("GF")
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("GEN2")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("LOAD")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("GEN3")
                .startTime(1)
                .build());
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("GEN4")
                .startTime(1)
                .build());
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "disconnect_injection_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_injection_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
