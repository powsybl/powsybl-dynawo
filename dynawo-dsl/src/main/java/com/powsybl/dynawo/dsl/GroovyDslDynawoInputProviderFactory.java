/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import java.nio.file.Path;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoInputProviderFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynawoInputProviderFactory.class)
public class GroovyDslDynawoInputProviderFactory implements DynawoInputProviderFactory {

    @Override
    public GroovyDslDynawoInputProvider create() {
        ModuleConfig config = PlatformConfig.defaultConfig().getModuleConfig("groovy-dsl-contingencies");
        Path dslFile = config.getPathProperty("dsl-file");
        return new GroovyDslDynawoInputProvider(dslFile);
    }

}
