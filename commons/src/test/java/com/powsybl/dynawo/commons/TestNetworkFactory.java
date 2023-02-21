/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.iidm.network.*;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public final class TestNetworkFactory {

    private TestNetworkFactory() {
    }

    static Network createMultiBusesVoltageLevelNetwork() {
        Network network = Network.create("multiBusesVl", "test");
        Substation s = network.newSubstation().setId("substation").add();

        VoltageLevel vl1 = s.newVoltageLevel().setId("vl1").setNominalV(250).setTopologyKind(TopologyKind.NODE_BREAKER).add();
        vl1.getNodeBreakerView().newBusbarSection().setId("Busbar1").setNode(0).add();
        vl1.getNodeBreakerView().newBusbarSection().setId("Busbar2").setNode(4).add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(1).setId("d1").add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(2).setId("d2").add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(3).setId("d3").add();
        vl1.getNodeBreakerView().newBreaker().setNode1(0).setNode2(4).setId("coupler").setOpen(true).add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(5).setId("d4").add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(4).setNode2(6).setId("d5").add();
        vl1.newLoad().setId("load1").setP0(10.0).setQ0(5.0).setNode(1).add()
                .getTerminal().setP(10.1).setQ(4.0);
        vl1.newLoad().setId("load2").setP0(12.0).setQ0(1.0).setNode(2).add()
                .getTerminal().setP(12.3).setQ(2.0);
        vl1.newLoad().setId("load3").setP0(22.0).setQ0(3.0).setNode(3).add()
                .getTerminal().setP(22.5).setQ(4.0);
        vl1.newLoad().setId("load4").setP0(-2.0).setQ0(-1.0).setNode(6).add()
                .getTerminal().setP(-1.1).setQ(0.0);

        VoltageLevel vl2 = s.newVoltageLevel().setId("vl2").setNominalV(250).setTopologyKind(TopologyKind.BUS_BREAKER).add();
        Bus b1 = vl2.getBusBreakerView().newBus().setId("b1").add();
        Bus b2 = vl2.getBusBreakerView().newBus().setId("b2").add();
        Bus b3 = vl2.getBusBreakerView().newBus().setId("b3").add();
        vl2.getBusBreakerView().newSwitch().setId("c1").setBus1(b1.getId()).setBus2(b2.getId()).add();
        vl2.getBusBreakerView().newSwitch().setId("c2").setBus1(b2.getId()).setBus2(b3.getId()).add();
        vl2.newGenerator().setId("g1").setBus(b1.getId()).setTargetP(101).setTargetV(390).setMinP(0).setMaxP(150).setVoltageRegulatorOn(true).add();
        vl2.newLoad().setId("load5").setP0(37.0).setQ0(1.0).setBus(b2.getId()).add()
                .getTerminal().setP(36.1).setQ(4.0);
        vl2.newLoad().setId("load6").setP0(13.0).setQ0(6.0).setBus(b2.getId()).add()
                .getTerminal().setP(10.1).setQ(7.0);
        vl2.newLoad().setId("load7").setP0(7.0).setQ0(-4.0).setBus(b3.getId()).add()
                .getTerminal().setP(7.1).setQ(-4.1);

        network.newLine().setId("l1").setVoltageLevel1(vl1.getId()).setNode1(5).setVoltageLevel2(vl2.getId()).setBus2(b1.getId())
                .setR(1).setX(3).setG1(0).setG2(0).setB1(0).setB2(0).add();
        return network;
    }
}
