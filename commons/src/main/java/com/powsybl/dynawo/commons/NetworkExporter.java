/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.dynawo.commons.exportconfiguration.ExportConfigurationHandler;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.AbstractTreeDataExporter;
import com.powsybl.iidm.serde.IidmVersion;
import com.powsybl.iidm.serde.NetworkSerDe;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class NetworkExporter {

    private static final String IIDM_VERSION_1_4 = IidmVersion.V_1_4.toString(".");
    private static final String IIDM_VERSION_1_5 = IidmVersion.V_1_5.toString(".");
    private static final DynawoVersion IIDM_1_5_MIN_DYNAWO_VERSION = new DynawoVersion(1, 7, 0);
    private static final ExportConfigurationHandler CONFIGURATION_HANDLER = new ExportConfigurationHandler();

    private NetworkExporter() {
    }

    public static void writeIidm(Network network, Path file, DynawoVersion version) {
        writeIidm(network, file, version, false);
    }

    public static void writeIidm(Network network, Path file, DynawoVersion version, boolean isMergeLoads) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(file);
        List<Consumer<Network>> networkModifiers = CONFIGURATION_HANDLER.getNetworkModifiers();
        boolean hasNetworkModificators = !networkModifiers.isEmpty();
        Network dynawoInput = isMergeLoads || hasNetworkModificators ? NetworkSerDe.copy(network) : network;
        if (isMergeLoads) {
            LoadsMerger.mergeLoads(dynawoInput, true);
        }
        if (hasNetworkModificators) {
            networkModifiers.forEach(m -> m.accept(dynawoInput));
        }
        Properties params = new Properties();
        params.setProperty(AbstractTreeDataExporter.VERSION,
                version.compareTo(IIDM_1_5_MIN_DYNAWO_VERSION) >= 0 ? IIDM_VERSION_1_5 : IIDM_VERSION_1_4);
        params.setProperty(AbstractTreeDataExporter.EXTENSIONS_INCLUDED_LIST,
                String.join(",", CONFIGURATION_HANDLER.getExtensionNames()));
        dynawoInput.write("XIIDM", params, file);
    }
}
