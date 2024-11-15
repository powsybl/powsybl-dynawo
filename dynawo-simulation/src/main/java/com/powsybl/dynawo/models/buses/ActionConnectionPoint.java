/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.models.Model;

import java.util.Optional;

/**
 * Interface for buses and busbar sections used by automatons for measure or event for various actions
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ActionConnectionPoint extends Model {

    String getTerminalVarName();

    Optional<String> getUImpinVarName();

    Optional<String> getUpuImpinVarName();

    Optional<String> getStateValueVarName();
}
