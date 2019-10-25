/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.exporter;

import java.nio.file.Path;

import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoExporterProviderMock implements DynawoExporterProvider {

    @Override
    public String getName() {
        return "DynawoExporterProviderMock";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String export(Network network, DynawoInputProvider dynawoProvider, Path workingDir) {
        return "export";
    }

}
