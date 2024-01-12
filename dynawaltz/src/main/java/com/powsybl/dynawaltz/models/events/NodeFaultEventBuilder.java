/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.events;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuilderEquipment;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class NodeFaultEventBuilder extends AbstractEventModelBuilder<Bus, NodeFaultEventBuilder> {

    public static final String TAG = "NodeFault";

    protected double faultTime;
    protected double rPu;
    protected double xPu;

    public NodeFaultEventBuilder(Network network, Reporter reporter) {
        super(network, new BuilderEquipment<>(IdentifiableType.BUS), TAG, reporter);
    }

    public NodeFaultEventBuilder faultTime(double faultTime) {
        this.faultTime = faultTime;
        return self();
    }

    public NodeFaultEventBuilder rPu(double rPu) {
        this.rPu = rPu;
        return self();
    }

    public NodeFaultEventBuilder xPu(double xPu) {
        this.xPu = xPu;
        return self();
    }

    protected Bus findEquipment(String staticId) {
        return network.getBusBreakerView().getBus(staticId);
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (faultTime <= 0) {
            Reporters.reportCrossThreshold(reporter, "faultTime", faultTime, "strictly positive");
            isInstantiable = false;
        }
        if (rPu < 0) {
            Reporters.reportCrossThreshold(reporter, "rPu", rPu, "positive");
            isInstantiable = false;
        }
        if (xPu < 0) {
            Reporters.reportCrossThreshold(reporter, "xPu", xPu, "positive");
            isInstantiable = false;
        }
    }

    @Override
    public NodeFaultEvent build() {
        return isInstantiable() ? new NodeFaultEvent(builderEquipment.getEquipment(), startTime, faultTime, rPu, xPu) : null;
    }

    protected NodeFaultEventBuilder self() {
        return this;
    }
}
