/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import groovy.json.JsonSlurper
import org.apache.groovy.json.internal.LazyMap

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@Singleton
class ModelsSlurper {

    private static final String MODEL_LIB = "lib"
    private static final String MODEL_PREFIX = "prefix"
    private static final String MODEL_PROPERTIES = "properties"

    private final slurper = new JsonSlurper()
    private final Map<URL, LazyMap> libsList = new HashMap<>()

    def getEquipmentConfigs(URL url, String modelTag) {
        libsList.computeIfAbsent(url, u -> slurper.parse(u))[modelTag].collect() {
            new EquipmentConfig(
                    it[MODEL_LIB] as String,
                    it[MODEL_PREFIX] as String,
                    it[MODEL_PROPERTIES] as String[])
        }
    }
}
