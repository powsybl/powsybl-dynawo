/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public interface DynawoData {

    default List<BlackBoxModel> getBlackBoxDynamicModels() {
        return Collections.emptyList();
    }

    default List<BlackBoxModel> getBlackBoxEventModels() {
        return Collections.emptyList();
    }

    Collection<MacroConnector> getMacroConnectors();

    default Collection<MacroStaticReference> getMacroStaticReferences() {
        return Collections.emptySet();
    }

    List<MacroConnect> getMacroConnectList();

    default String getParFileName() {
        return null;
    }
}
