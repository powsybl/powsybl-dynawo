/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.generator;

import com.powsybl.commons.extensions.Extension;
import com.powsybl.iidm.network.Generator;

/**
 * Extension properties for synchronized generators.
 *
 * <p>Attaches metadata to a {@link Generator} used by Dynawo.
 *
 * <p>The {@code type} property selects the generator model/driver to use
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */

public interface SynchronizedGeneratorProperties extends Extension<Generator> {

    String NAME = "synchronizedGeneratorProperties";

    @Override
    default String getName() {
        return NAME;
    }

    String getType();

    void setType(String type);

    RpclType getRpcl();

    boolean isRpcl2();

    void setRpcl(RpclType rpcl2);

}
