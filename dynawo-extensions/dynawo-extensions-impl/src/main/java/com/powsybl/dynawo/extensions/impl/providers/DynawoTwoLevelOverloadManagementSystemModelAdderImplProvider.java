/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.providers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionAdderProvider;
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.impl.model.DynawoTwoLevelOverloadManagementSystemModelAdderImpl;
import com.powsybl.iidm.network.Branch;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionAdderProvider.class)
public class DynawoTwoLevelOverloadManagementSystemModelAdderImplProvider<B extends Branch<B>> implements
        ExtensionAdderProvider<B, DynawoTwoLevelOverloadManagementSystemModel<B>, DynawoTwoLevelOverloadManagementSystemModelAdderImpl<B>> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public String getExtensionName() {
        return DynawoTwoLevelOverloadManagementSystemModel.NAME;
    }

    @Override
    public Class<DynawoTwoLevelOverloadManagementSystemModelAdderImpl> getAdderClass() {
        return DynawoTwoLevelOverloadManagementSystemModelAdderImpl.class;
    }

    @Override
    public DynawoTwoLevelOverloadManagementSystemModelAdderImpl<B> newAdder(B extendable) {
        return new DynawoTwoLevelOverloadManagementSystemModelAdderImpl<>(extendable);
    }
}
