/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;

import java.util.stream.Stream;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public interface ModelsSimplifier {

    //TODO replace with SimplifierContext and restraint direct action on Dynawaltz data
    Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> models, Network network, DynaWaltzParameters dynaWaltzParameters, Reporter reporter);
}
