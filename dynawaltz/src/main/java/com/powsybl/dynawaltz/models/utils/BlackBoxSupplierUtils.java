/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.utils;

import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class BlackBoxSupplierUtils {

    private BlackBoxSupplierUtils() {
    }

    public static List<BlackBoxModel> getBlackBoxModelList(DynamicModelsSupplier dynamicModelsSupplier, Network network) {
        return dynamicModelsSupplier.get(network).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
    }

    public static List<BlackBoxModel> getBlackBoxModelList(EventModelsSupplier eventModelsSupplier, Network network) {
        return eventModelsSupplier.get(network).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
    }
}
