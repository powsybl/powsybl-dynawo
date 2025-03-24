/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionAdderProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionAdderProvider.class)
public class DynamicAutomationSystemInfosAdderImplProvider
        implements ExtensionAdderProvider<Network, DynamicAutomationSystemInfos, DynamicAutomationSystemInfosAdderImpl> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public String getExtensionName() {
        return DynamicAutomationSystemInfos.NAME;
    }

    @Override
    public Class<DynamicAutomationSystemInfosAdderImpl> getAdderClass() {
        return DynamicAutomationSystemInfosAdderImpl.class;
    }

    @Override
    public DynamicAutomationSystemInfosAdderImpl newAdder(Network network) {
        return new DynamicAutomationSystemInfosAdderImpl(network);
    }
}
