/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.extensions.AbstractIidmExtensionAdder;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicAutomationSystemInfosAdderImpl extends AbstractIidmExtensionAdder<Network, DynamicAutomationSystemInfos>
        implements DynamicAutomationSystemInfosAdder {

    private List<DynamicAutomationSystemInfo> dynamicAutomationSystemInfos;

    protected DynamicAutomationSystemInfosAdderImpl(Network identifiable) {
        super(identifiable);
    }

    @Override
    public DynamicAutomationSystemInfosAdder setDynamicAutomationSystemInfos(List<DynamicAutomationSystemInfo> infos) {
        this.dynamicAutomationSystemInfos = infos;
        return this;
    }

    @Override
    protected DynamicAutomationSystemInfos createExtension(Network network) {
        return new DynamicAutomationSystemInfosImpl(network, dynamicAutomationSystemInfos);
    }
}
