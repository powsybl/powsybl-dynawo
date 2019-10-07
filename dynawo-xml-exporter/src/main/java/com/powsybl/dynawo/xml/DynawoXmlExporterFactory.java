/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoExporterFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynawoExporterFactory.class)
public class DynawoXmlExporterFactory implements DynawoExporterFactory {

    @Override
    public DynawoXmlExporter create() {
        return create(PlatformConfig.defaultConfig());
    }

    @Override
    public DynawoXmlExporter create(PlatformConfig platformConfig) {
        return new DynawoXmlExporter(platformConfig);
    }
}
