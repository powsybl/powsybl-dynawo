/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;

import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ModelsSubstitutionSimplifier {

    Function<BlackBoxModel, BlackBoxModel> getModelSubstitutionFunction(Network network, DynawoSimulationParameters dynawoSimulationParameters, ReportNode reportNode);
}
