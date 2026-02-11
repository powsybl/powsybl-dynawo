/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.exportedextensions;

import com.google.common.base.Suppliers;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Load the list of extension to include during IIDM export
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ExportedIidmExtensionsHandler {

    private static final Supplier<List<String>> IIDM_EXTENSION_NAMES_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(ExportedIidmExtensions.class).stream()
                    .flatMap(provider -> provider.get().getIidmExtensionNames().stream())
                    .collect(Collectors.toList()));

    private final List<String> extensionNames;

    public ExportedIidmExtensionsHandler() {
        this.extensionNames = IIDM_EXTENSION_NAMES_SUPPLIER.get();
    }

    public List<String> getExtensionNames() {
        return extensionNames;
    }
}
