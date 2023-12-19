/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicModelBuilder {

    protected final Network network;
    protected final Reporter reporter;
    protected boolean isInstantiable = true;

    protected AbstractDynamicModelBuilder(Network network, Reporter reporter) {
        this.network = network;
        this.reporter = reporter;
    }

    protected AbstractDynamicModelBuilder(Network network) {
        this.network = network;
        this.reporter = Reporter.NO_OP;
    }

    protected abstract void checkData();

    protected final boolean isInstantiable() {
        checkData();
        if (isInstantiable) {
            Reporters.reportModelInstantiation(reporter, getModelId());
        } else {
            Reporters.reportModelInstantiationFailure(reporter, getModelId());
        }
        return isInstantiable;
    }

    protected abstract String getModelId();
}
