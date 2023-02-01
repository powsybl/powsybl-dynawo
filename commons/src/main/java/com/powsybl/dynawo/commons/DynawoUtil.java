/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static com.powsybl.dynawo.commons.DynawoConstants.IIDM_EXTENSIONS;
import static com.powsybl.dynawo.commons.DynawoConstants.IIDM_VERSION;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class DynawoUtil {

    private DynawoUtil() {
    }

    public static void writeIidm(Network network, Path file) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(file);
        Properties params = new Properties();
        params.setProperty(XMLExporter.VERSION, IIDM_VERSION);
        params.setProperty(XMLExporter.EXTENSIONS_LIST, String.join(",", IIDM_EXTENSIONS));
        network.write("XIIDM", params, file);
    }
}
