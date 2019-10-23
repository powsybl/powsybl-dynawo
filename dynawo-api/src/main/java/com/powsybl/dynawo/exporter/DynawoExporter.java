/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.exporter;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.Versionable;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.util.ServiceLoaderCache;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoExporter {

    private DynawoExporter() {
    }

    private static final Supplier<List<DynawoExporterProvider>> PROVIDERS_SUPPLIERS = Suppliers
        .memoize(() -> new ServiceLoaderCache<>(DynawoExporterProvider.class).getServices());

    public static class Runner implements Versionable {
        private final DynawoExporterProvider provider;

        public Runner(DynawoExporterProvider provider) {
            this.provider = Objects.requireNonNull(provider);
        }

        public String export(Network network, DynawoInputProvider dynawoProvider, Path workingDir) {
            return provider.export(network, dynawoProvider, workingDir);
        }

        @Override
        public String getName() {
            return provider.getName();
        }

        @Override
        public String getVersion() {
            return provider.getVersion();
        }
    }

    public static Runner find(String name) {
        return find(name, PROVIDERS_SUPPLIERS.get(), PlatformConfig.defaultConfig());
    }

    public static Runner find() {
        return find(null);
    }

    public static Runner find(String name, List<DynawoExporterProvider> providers, PlatformConfig platformConfig) {
        Objects.requireNonNull(providers);
        Objects.requireNonNull(platformConfig);

        if (providers.isEmpty()) {
            throw new PowsyblException("No DynawoExporter providers found");
        }

        // if no DynawoExporter implementation name is provided through the API we look
        // for information in platform configuration
        String dynawoExporterName = name != null ? name
            : platformConfig.getOptionalModuleConfig("dynawo-exporter")
                .flatMap(mc -> mc.getOptionalStringProperty("default"))
                .orElse(null);
        DynawoExporterProvider provider;
        if (providers.size() == 1 && dynawoExporterName == null) {
            // no information to select the implementation but only one provider, so we can
            // use it by default (that is be the most common use case)
            provider = providers.get(0);
        } else {
            if (providers.size() > 1 && dynawoExporterName == null) {
                // several providers and no information to select which one to choose, we can
                // only throw an exception
                List<String> dynawoExporterNames = providers.stream().map(DynawoExporterProvider::getName).collect(Collectors.toList());
                throw new PowsyblException("Several DynawoExporter implementations found (" + dynawoExporterNames
                    + "), you must add configuration to select the implementation");
            }
            provider = providers.stream()
                .filter(p -> p.getName().equals(dynawoExporterName))
                .findFirst()
                .orElseThrow(() -> new PowsyblException("DynawoExporter '" + dynawoExporterName + "' not found"));
        }

        return new Runner(provider);
    }

    public static String export(Network network, DynawoInputProvider dynawoProvider, Path workingDir) {
        return find().export(network, dynawoProvider, workingDir);
    }
}
