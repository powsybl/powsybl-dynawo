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

public class DynawoDynamicModels {

    public DynawoDynamicModels(DynawoProvider provider) {
        this.dynamicModels = provider.getDynawoDynamicModels();
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.dyd");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), dynamicsModels()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.dyd");
        }
    }

    private CharSequence dynamicsModels() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<dyn:dynamicModelsArchitecture xmlns:dyn=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());

        dynamicModels.forEach(dynamicModel -> builder.append(String.join(System.lineSeparator(), dynamicModel(dynamicModel)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(),
            "</dyn:dynamicModelsArchitecture>") + System.lineSeparator());
        return builder.toString();
    }

    private String dynamicModel(DynawoDynamicModel dynamicModel) {
        if (dynamicModel.isBlackBoxModel()) {
            return blackBoxModel(dynamicModel);
        } else {
            return connection(dynamicModel);
        }
    }

    private String blackBoxModel(DynawoDynamicModel dynamicModel) {
        String id = dynamicModel.getBlackBoxModelId();
        String lib = dynamicModel.getBlackBoxModelLib();
        String file = dynamicModel.getParametersFile();
        int paramId = dynamicModel.getParametersId();
        String staticId = dynamicModel.getStaticId();
        return setBlackBoxModel(id, lib, file, paramId, staticId);
    }

    private String connection(DynawoDynamicModel dynamicModel) {
        String id1 = dynamicModel.getConnectionId1();
        String var1 = dynamicModel.getConnectionVar1();
        String id2 = dynamicModel.getConnectionId2();
        String var2 = dynamicModel.getConnectionVar2();
        return setConnection(id1, var1, id2, var2);
    }

    private String setBlackBoxModel(String id, String lib, String parFile, int parId, String staticId) {
        if (staticId == null) {
            return "  <dyn:blackBoxModel id=\"" + id + "\" lib=\"" + lib + "\" parFile=\"" + parFile + "\" parId=\""
                + parId
                + "\" />";
        }
        return "  <dyn:blackBoxModel id=\"" + id + "\" lib=\"" + lib + "\" parFile=\"" + parFile + "\" parId=\"" + parId
            + "\" staticId=\"" + staticId + "\" />";
    }

    private String setConnection(String id1, String var1, String id2, String var2) {
        return "  <dyn:connect id1=\"" + id1 + "\" var1=\"" + var1 + "\" id2=\"" + id2 + "\" var2=\"" + var2 + "\"/>";
    }

    private final List<DynawoDynamicModel> dynamicModels;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoDynamicModels.class);
}
