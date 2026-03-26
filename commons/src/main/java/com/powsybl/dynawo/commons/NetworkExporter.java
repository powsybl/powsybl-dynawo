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
import com.powsybl.iidm.serde.ExportOptions;
import com.powsybl.iidm.serde.IidmVersion;
import com.powsybl.iidm.serde.NetworkSerDe;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class NetworkExporter {

    @FunctionalInterface
    public interface VoltageLevelFinder {
        void findVoltageLevels(Network network, Consumer<VoltageLevel> voltageLevelConsumer);
    }

    private static final String IIDM_VERSION_1_4 = IidmVersion.V_1_4.toString(".");
    private static final String IIDM_VERSION_1_5 = IidmVersion.V_1_5.toString(".");
    private static final DynawoVersion IIDM_1_5_MIN_DYNAWO_VERSION = new DynawoVersion(1, 7, 0);
    private static final ExportConfigurationHandler CONFIGURATION_HANDLER = new ExportConfigurationHandler();

    private NetworkExporter() {
    }

    public static void writeIidm(Network network, Path file, DynawoVersion version, boolean isMergeLoads, VoltageLevelFinder... voltageLevelFinders) {
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
        NetworkSerDe.write(dynawoInput, createExportOptions(network, version, voltageLevelFinders), file);
    }

    private static ExportOptions createExportOptions(Network network, DynawoVersion version, VoltageLevelFinder... voltageLevelFinders) {

        ExportOptions exportOptions = new ExportOptions()
                .setVersion(version.compareTo(IIDM_1_5_MIN_DYNAWO_VERSION) >= 0 ? IIDM_VERSION_1_5 : IIDM_VERSION_1_4)
                .setIncludedExtensions(CONFIGURATION_HANDLER.getExtensionNames())
                .setThrowExceptionIfExtensionNotFound(true)
                .setTopologyLevel(TopologyLevel.NODE_BREAKER)
                .setBusBranchVoltageLevelIncompatibilityBehavior(ExportOptions.BusBranchVoltageLevelIncompatibilityBehavior.KEEP_ORIGINAL_TOPOLOGY);

        // TODO handle bus breaker
        Stream.of(voltageLevelFinders).forEach(finder -> finder.findVoltageLevels(network,
                vl -> exportOptions.addVoltageLevelTopologyLevel(vl.getId(), TopologyLevel.NODE_BREAKER)));
        return exportOptions;
    }
}
