/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.suppliers.Property;
import com.powsybl.dynawo.suppliers.SetGroupType;
import com.powsybl.iidm.network.Network;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ModelResolver {

    String getName();

    DynamicModelConfig resolveAlternativeModels(Network network, List<AlternativeModelConfig> alternativeModelConfigs,
                                                SetGroupType groupType, List<Property> properties, ReportNode reportNode);

    default DynamicModelConfig resolveAlternativeModels(Network network, List<AlternativeModelConfig> alternativeModelConfigs,
                                                SetGroupType groupType, List<Property> properties) {
        return resolveAlternativeModels(network, alternativeModelConfigs, groupType, properties, ReportNode.NO_OP);
    }
}
