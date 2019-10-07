/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.Objects;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.util.ServiceLoaderCache;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoExporterFactory {

    DynawoExporter create();

    DynawoExporter create(PlatformConfig platformConfig);

    static DynawoExporterFactory find(String name) {
        Objects.requireNonNull(name);
        return new ServiceLoaderCache<>(DynawoExporterFactoryService.class).getServices().stream()
            .filter(s -> s.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new PowsyblException("'" + name + "' Dynawo Exporter implementation not found"))
            .createDynawoExporterFactory();
    }

    static DynawoExporterFactory findDefault() {
        return find("Default");
    }

}
