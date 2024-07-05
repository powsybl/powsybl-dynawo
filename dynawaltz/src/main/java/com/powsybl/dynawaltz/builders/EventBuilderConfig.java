/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventBuilderConfig {

    @FunctionalInterface
    public interface EventModelBuilderConstructor {
        ModelBuilder<EventModel> createBuilder(Network network, ReportNode reportNode);
    }

    private final EventModelBuilderConstructor builderConstructor;
    private final EventModelInfo eventModelInfo;

    public EventBuilderConfig(EventModelBuilderConstructor builderConstructor, EventModelInfo eventModelInfo) {
        this.builderConstructor = builderConstructor;
        this.eventModelInfo = eventModelInfo;
    }

    public EventModelBuilderConstructor getBuilderConstructor() {
        return builderConstructor;
    }

    public EventModelInfo getEventModelInfo() {
        return eventModelInfo;
    }
}
