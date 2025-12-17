/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.generator.connection;

import com.powsybl.commons.extensions.Extension;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public interface GeneratorConnectionLevel extends Extension<Generator> {

    String NAME = "generatorConnectionLevel";

    enum GeneratorConnectionLevelType {
        TSO,
        DSO
    }

    @Override
    default String getName() {
        return NAME;
    }

    GeneratorConnectionLevelType getLevel();

    void setLevel(GeneratorConnectionLevelType level);

}
