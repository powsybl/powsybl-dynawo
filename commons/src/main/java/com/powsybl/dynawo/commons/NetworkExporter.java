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
import com.powsybl.iidm.network.TopologyLevel;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.serde.AbstractTreeDataExporter;
import com.powsybl.iidm.serde.IidmVersion;
import com.powsybl.iidm.serde.NetworkSerDe;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class NetworkExporter {

    @FunctionalInterface
    public interface VoltageLevelFinder {
        void findVoltageLevels(Network network, Consumer<VoltageLevel> voltageLevelConsumer);
    }

    /**
     * write the network to XIIDM v1.4 because currently Dynawo does not support versions above
     */
    private static final String IIDM_VERSION = IidmVersion.V_1_4.toString(".");

    private static final ExportConfigurationHandler CONFIGURATION_HANDLER = new ExportConfigurationHandler();

    private NetworkExporter() {
    }

    public static void writeIidm(Network network, Path file) {
        writeIidm(network, file, false);
    }

    public static void writeIidm(Network network, Path file, boolean isMergeLoads, VoltageLevelFinder... voltageLevelFinders) {
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
        dynawoInput.write("XIIDM", createProperties(network, voltageLevelFinders), file);
    }

    private static Properties createProperties(Network network, VoltageLevelFinder... voltageLevelFinders) {
        Properties params = new Properties();
        params.setProperty(AbstractTreeDataExporter.VERSION, IIDM_VERSION);
        params.setProperty(AbstractTreeDataExporter.EXTENSIONS_INCLUDED_LIST,
                String.join(",", CONFIGURATION_HANDLER.getExtensionNames()));
        params.setProperty(AbstractTreeDataExporter.THROW_EXCEPTION_IF_EXTENSION_NOT_FOUND, "true");
        params.setProperty(AbstractTreeDataExporter.TOPOLOGY_LEVEL, TopologyLevel.BUS_BRANCH.toString());
        params.setProperty(AbstractTreeDataExporter.BUS_BRANCH_VOLTAGE_LEVEL_INCOMPATIBILITY_BEHAVIOR, "KEEP_ORIGINAL_TOPOLOGY");

        Set<VoltageLevel> voltageLevels = new HashSet<>();
        Stream.of(voltageLevelFinders).forEach(finder -> finder.findVoltageLevels(network, voltageLevels::add));
        if (!voltageLevels.isEmpty()) {
            params.setProperty(AbstractTreeDataExporter.VOLTAGE_LEVELS_NODE_BREAKER,
                    voltageLevels.stream().map(VoltageLevel::getId).collect(Collectors.joining(",")));
        }
        return params;
    }
}
