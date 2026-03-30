/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.exportconfiguration;

import com.google.common.base.Suppliers;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Load the list of extension to include during IIDM export
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ExportConfigurationHandler {

    private static final Supplier<List<String>> IIDM_EXTENSION_NAMES_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(ExportedIidmExtensions.class).stream()
                    .flatMap(provider -> provider.get().getIidmExtensionNames().stream())
                    .collect(Collectors.toList()));

    private static final Supplier<List<Consumer<Network>>> NETWORK_MODIFIERS_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(NetworkModifier.class).stream()
                    .map(provider -> provider.get().getNetworkModifier())
                    .collect(Collectors.toList()));

    private final List<String> extensionNames;
    private final List<Consumer<Network>> networkModifiers;

    public ExportConfigurationHandler() {
        this.extensionNames = IIDM_EXTENSION_NAMES_SUPPLIER.get();
        this.networkModifiers = NETWORK_MODIFIERS_SUPPLIER.get();
    }

    public List<String> getExtensionNames() {
        return extensionNames;
    }

    public List<Consumer<Network>> getNetworkModifiers() {
        return networkModifiers;
    }
}
