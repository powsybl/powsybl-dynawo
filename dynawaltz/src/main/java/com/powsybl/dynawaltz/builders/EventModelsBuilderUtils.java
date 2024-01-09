/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.models.events.NodeFaultEventBuilder;
import com.powsybl.iidm.network.Network;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class EventModelsBuilderUtils {

    private EventModelsBuilderUtils() {
    }

    @FunctionalInterface
    public interface EventModelBuilderConstructor {
        ModelBuilder<EventModel> createBuilder(Network network, Reporter reporter);
    }

    public static List<EventModelCategory> getEventModelCategories() {
        return List.of(new EventModelCategory(EventActivePowerVariationBuilder.TAG, EventActivePowerVariationBuilder::new),
                new EventModelCategory(EventDisconnectionBuilder.TAG, EventDisconnectionBuilder::new),
                new EventModelCategory(NodeFaultEventBuilder.TAG, NodeFaultEventBuilder::new));
    }

    public static EventDisconnectionBuilder newEventDisconnectionBuilder(Network network, Reporter reporter) {
        return new EventDisconnectionBuilder(network, reporter);
    }

    public static EventDisconnectionBuilder newEventDisconnectionBuilder(Network network) {
        return newEventDisconnectionBuilder(network, Reporter.NO_OP);
    }

    public static EventActivePowerVariationBuilder newEventActivePowerVariationBuilder(Network network, Reporter reporter) {
        return new EventActivePowerVariationBuilder(network, reporter);
    }

    public static EventActivePowerVariationBuilder newEventActivePowerVariationBuilder(Network network) {
        return newEventActivePowerVariationBuilder(network, Reporter.NO_OP);
    }

    public static NodeFaultEventBuilder newNodeFaultEventBuilder(Network network, Reporter reporter) {
        return new NodeFaultEventBuilder(network, reporter);
    }

    public static NodeFaultEventBuilder newNodeFaultEventBuilder(Network network) {
        return newNodeFaultEventBuilder(network, Reporter.NO_OP);
    }
}
