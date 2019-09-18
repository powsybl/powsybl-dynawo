/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.dynawo.DynawoDynamicModel;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

import static java.lang.Math.toIntExact;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoDynamicModels {

    public DynawoDynamicModels(Network network, DynawoProvider provider) {
        this.network = network;
        this.dynamicModels = provider.getDynawoDynamicModels(network);
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.dyd");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), writeDynamicModels()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.dyd");
        }
    }

    private String writeDynamicModels() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.writeInputHeader(),
            "<dyn:dynamicModelsArchitecture xmlns:dyn=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());

        builder.append(String.join(System.lineSeparator(), defaultOmegaRef()) + System.lineSeparator());
        generators = toIntExact(dynamicModels.stream().filter(dynamicModel -> dynamicModel.getConnectionVar2() != null && dynamicModel.getConnectionVar2().equals("generator_omegaPu")).count());
        dynamicModels.forEach(dynamicModel -> builder
            .append(String.join(System.lineSeparator(), writeDynamicModel(dynamicModel)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(), writeDefaultDynamicModels()) + System.lineSeparator());
        builder.append(String.join(System.lineSeparator(),
            "</dyn:dynamicModelsArchitecture>") + System.lineSeparator());
        return builder.toString();
    }

    private boolean definedDynamicModel(String id) {
        return dynamicModels.stream().anyMatch(
            dynamicModel -> dynamicModel.getBlackBoxModelId() != null && dynamicModel.getBlackBoxModelId().equals(id));
    }

    private String defaultOmegaRef() {
        StringBuilder builder = new StringBuilder();
        if (!definedDynamicModel(DynawoInput.OMEGA_REF)) {
            builder.append(String.join(System.lineSeparator(),
                writeDefaultOmegaRef()) + System.lineSeparator());
        }
        return builder.toString();
    }

    private String writeDefaultOmegaRef() {
        return writeDynamicModel(new DynawoDynamicModel(DynawoInput.OMEGA_REF, "DYNModelOmegaRef", DynawoInput.DYANWO_PAR, 1));
    }

    private String writeDefaultDynamicModels() {
        StringBuilder builder = new StringBuilder();
        network.getLoads().forEach(load -> {
            if (!definedDynamicModel(load.getId())) {
                builder.append(String.join(System.lineSeparator(),
                    writeDefaultLoad(load)) + System.lineSeparator());
            }
        });
        network.getGenerators().forEach(generator -> {
            if (!definedDynamicModel(generator.getId())) {
                builder.append(String.join(System.lineSeparator(), writeDefaultGenerator(generator))
                    + System.lineSeparator());
                generators++;
            }
        });
        return builder.toString();
    }

    private String writeDefaultLoad(Load load) {
        return String.join(System.lineSeparator(),
            writeDefaultLoadBlackBoxModel(load),
            writeDefaultLoadConnection(load));
    }

    private String writeDefaultLoadBlackBoxModel(Load load) {
        return writeDynamicModel(
            new DynawoDynamicModel(load.getId(), "LoadAlphaBeta", DynawoInput.DYANWO_PAR, maxParId + 1, load.getId()));
    }

    private String writeDefaultLoadConnection(Load load) {
        if (load.getTerminal().getBusBreakerView() != null) {
            return writeDynamicModel(
                new DynawoDynamicModel(load.getId(), "load_terminal", DynawoInput.NETWORK,
                    load.getTerminal().getBusBreakerView().getBus().getId() + "_ACPIN"));
        }
        return writeDynamicModel(
            new DynawoDynamicModel(load.getId(), "load_terminal", DynawoInput.NETWORK,
                load.getTerminal().getBusView().getBus().getId() + "_ACPIN"));
    }

    private String writeDefaultGenerator(Generator generator) {
        return String.join(System.lineSeparator(),
            writeDefaultGeneratorBlackBoxModel(generator),
            writeDefaultGeneratorConnection(generator));
    }

    private String writeDefaultGeneratorBlackBoxModel(Generator generator) {
        return writeDynamicModel(new DynawoDynamicModel(generator.getId(),
            "GeneratorSynchronousFourWindingsProportionalRegulations", DynawoInput.DYANWO_PAR, maxParId + 1,
            generator.getId()));
    }

    private String writeDefaultGeneratorConnection(Generator generator) {
        return String.join(System.lineSeparator(),
            writeDynamicModel(
                new DynawoDynamicModel(DynawoInput.OMEGA_REF, "omega_grp_" + generators, generator.getId(), "generator_omegaPu")),
            writeDynamicModel(new DynawoDynamicModel(DynawoInput.OMEGA_REF, "omegaRef_grp_" + generators, generator.getId(),
                "generator_omegaRefPu")),
            writeDynamicModel(new DynawoDynamicModel(DynawoInput.OMEGA_REF, "numcc_node_" + generators, DynawoInput.NETWORK,
                "@" + generator.getId() + "@@NODE@_numcc")),
            writeDynamicModel(
                new DynawoDynamicModel(DynawoInput.OMEGA_REF, "running_grp_" + generators, generator.getId(),
                    "generator_running")),
            writeDynamicModel(new DynawoDynamicModel(generator.getId(), "generator_terminal", DynawoInput.NETWORK,
                "@" + generator.getId() + "@@NODE@_ACPIN")),
            writeDynamicModel(new DynawoDynamicModel(generator.getId(), "generator_switchOffSignal1", DynawoInput.NETWORK,
                "@" + generator.getId() + "@@NODE@_switchOff")));
    }

    private String writeDynamicModel(DynawoDynamicModel dynamicModel) {
        if (dynamicModel.isBlackBoxModel()) {
            return writeBlackBoxModel(dynamicModel);
        } else {
            return writeConnection(dynamicModel);
        }
    }

    private String writeBlackBoxModel(DynawoDynamicModel dynamicModel) {
        String id = dynamicModel.getBlackBoxModelId();
        String lib = dynamicModel.getBlackBoxModelLib();
        String file = dynamicModel.getParametersFile();
        int paramId = dynamicModel.getParametersId();
        String staticId = dynamicModel.getStaticId();
        return writeBlackBoxModel(id, lib, file, paramId, staticId);
    }

    private String writeConnection(DynawoDynamicModel dynamicModel) {
        String id1 = dynamicModel.getConnectionId1();
        String var1 = dynamicModel.getConnectionVar1();
        String id2 = dynamicModel.getConnectionId2();
        String var2 = dynamicModel.getConnectionVar2();
        return writeConnection(id1, var1, id2, var2);
    }

    private String writeBlackBoxModel(String id, String lib, String parFile, int parId, String staticId) {
        if (parId > maxParId) {
            maxParId = parId;
        }
        if (staticId == null) {
            return "  <dyn:blackBoxModel id=\"" + id + "\" lib=\"" + lib + "\" parFile=\"" + parFile + "\" parId=\""
                + parId
                + "\" />";
        }
        return "  <dyn:blackBoxModel id=\"" + id + "\" lib=\"" + lib + "\" parFile=\"" + parFile + "\" parId=\"" + parId
            + "\" staticId=\"" + staticId + "\" />";
    }

    private String writeConnection(String id1, String var1, String id2, String var2) {
        return "  <dyn:connect id1=\"" + id1 + "\" var1=\"" + var1 + "\" id2=\"" + id2 + "\" var2=\"" + var2 + "\"/>";
    }

    private final Network network;
    private final List<DynawoDynamicModel> dynamicModels;
    private int maxParId = 0;
    private int generators = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoDynamicModels.class);
}
