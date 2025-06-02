/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.utils;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BlackBoxSupplierUtils {

    private BlackBoxSupplierUtils() {
    }

    public static List<BlackBoxModel> getBlackBoxModelList(DynamicModelsSupplier dynamicModelsSupplier, Network network, ReportNode reporter) {
        return dynamicModelsSupplier.get(network, reporter).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
    }

    public static List<BlackBoxModel> getBlackBoxModelList(EventModelsSupplier eventModelsSupplier, Network network, ReportNode reporter) {
        return eventModelsSupplier.get(network, reporter).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
    }
}
