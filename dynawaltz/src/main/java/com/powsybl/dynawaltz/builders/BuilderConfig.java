package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.iidm.network.Network;

import java.util.Collection;
import java.util.function.Supplier;

public class BuilderConfig {

    @FunctionalInterface
    public interface ModelBuilderConstructor {
        ModelBuilder<DynamicModel> createBuilder(Network network, String lib, Reporter reporter);
    }

    private final ModelBuilderConstructor builderConstructor;
    private final Supplier<Collection<String>> libsSupplier;

    public BuilderConfig(ModelBuilderConstructor builderConstructor, Supplier<Collection<String>> libsSupplier) {
        this.builderConstructor = builderConstructor;
        this.libsSupplier = libsSupplier;
    }

    public ModelBuilderConstructor getBuilderConstructor() {
        return builderConstructor;
    }

    public Collection<String> getLibs() {
        return libsSupplier.get();
    }
}
