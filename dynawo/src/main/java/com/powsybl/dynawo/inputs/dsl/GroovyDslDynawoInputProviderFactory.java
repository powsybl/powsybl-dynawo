/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl;

import java.nio.file.Path;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.inputs.model.DynawoInputsProviderFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynawoInputsProviderFactory.class)
public class GroovyDslDynawoInputProviderFactory implements DynawoInputsProviderFactory {

    @Override
    public GroovyDslDynawoInputProvider create() {
        ModuleConfig config = PlatformConfig.defaultConfig().getModuleConfig("groovy-dsl-dynawo");
        Path dslFile = config.getPathProperty("dsl-file");
        return new GroovyDslDynawoInputProvider(dslFile);
    }

    @Override
    public GroovyDslDynawoInputProvider create(Path dslFile) {
        return new GroovyDslDynawoInputProvider(dslFile);
    }
}
