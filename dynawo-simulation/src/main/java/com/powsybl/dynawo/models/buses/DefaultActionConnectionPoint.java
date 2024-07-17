/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.dynawo.models.defaultmodels.AbstractDefaultModel;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class DefaultActionConnectionPoint extends AbstractDefaultModel implements ActionConnectionPoint {

    public DefaultActionConnectionPoint(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultActionConnectionPoint";
    }

    @Override
    public String getTerminalVarName() {
        return "@NAME@_ACPIN";
    }

    @Override
    public Optional<String> getUImpinVarName() {
        return Optional.of("@NAME@_U");
    }

    @Override
    public Optional<String> getUpuImpinVarName() {
        return Optional.of("@NAME@_Upu");
    }
}
