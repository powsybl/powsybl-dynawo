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
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventActivePowerVariationBuilder extends AbstractEventModelBuilder<Injection<?>, EventActivePowerVariationBuilder> {

    public static final String TAG = "Step";

    protected Double deltaP;

    public static EventActivePowerVariationBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static EventActivePowerVariationBuilder of(Network network, Reporter reporter) {
        return new EventActivePowerVariationBuilder(network, reporter);
    }

    EventActivePowerVariationBuilder(Network network, Reporter reporter) {
        super(network, new BuilderEquipment<>("GENERATOR/LOAD"), reporter);
    }

    public EventActivePowerVariationBuilder deltaP(double deltaP) {
        this.deltaP = deltaP;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (deltaP == null) {
            Reporters.reportFieldNotSet(reporter, "deltaP");
            isInstantiable = false;
        }
    }

    @Override
    protected Injection<?> findEquipment(String staticId) {
        Injection<?> equipment = network.getLoad(staticId);
        return equipment != null ? equipment : network.getGenerator(staticId);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public EventActivePowerVariation build() {
        return isInstantiable() ? new EventActivePowerVariation(eventId, builderEquipment.getEquipment(), startTime, deltaP) : null;
    }

    @Override
    protected EventActivePowerVariationBuilder self() {
        return this;
    }
}
