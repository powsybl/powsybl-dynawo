/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.iidm.network.Identifiable;

/**
 * Resolver for multiple models, used to determine which equipment identifiers
 * should be assigned a specific dynamic model.
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface MultipleModelResolver {

    String getName();

    DynamicModelConfig resolveMultipleModels(Identifiable<?> identifiable, ReportNode reportNode);

    default DynamicModelConfig resolveMultipleModels(Identifiable<?> identifiable) {
        return resolveMultipleModels(identifiable, ReportNode.NO_OP);
    }
}
