/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.dynawo.DynawoCurve;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoCurves {

    public DynawoCurves(Network network, DynawoProvider provider) {
        this.curves = provider.getDynawoCurves(network);
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.crv");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), writeCurves()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.crv");
        }
    }

    private String writeCurves() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.writeInputHeader(),
            "<curvesInput xmlns=\"http://www.rte-france.com/dynawo\">",
            "<!--Curves for scenario-->") + System.lineSeparator());

        curves.forEach(curve -> builder.append(String.join(System.lineSeparator(), writeCurve(curve) + System.lineSeparator())));

        builder.append(String.join(System.lineSeparator(),
            "</curvesInput>") + System.lineSeparator());
        return builder.toString();
    }

    private String writeCurve(DynawoCurve curve) {
        String model = curve.getModel();
        String variable = curve.getVariable();
        return writeCurve(model, variable);
    }

    private String writeCurve(String model, String variable) {
        return "  <curve model=\"" + model + "\" variable=\"" + variable + "\"/>";
    }

    private final List<DynawoCurve> curves;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoCurves.class);
}
