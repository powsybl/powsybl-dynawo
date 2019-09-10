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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoCurves {

    public DynawoCurves(Network network) {
        this.network = network;
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.crv");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), curves()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.crv");
        }
    }

    private CharSequence curves() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<curvesInput xmlns=\"http://www.rte-france.com/dynawo\">",
            "<!--Curves for scenario-->") + System.lineSeparator());

        for (Bus b : network.getBusBreakerView().getBuses()) {
            loadBusCurve(b, builder);
        }
        for (Generator g : network.getGenerators()) {
            loadGeneratorCurve(g, builder);
        }
        for (Load l : network.getLoads()) {
            loadLoadCurve(l, builder);
        }

        builder.append(String.join(System.lineSeparator(),
            "</curvesInput>") + System.lineSeparator());
        return builder.toString();
    }

    private void loadBusCurve(Bus b, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setCurve("NETWORK", b.getId() + "_Upu_value")) + System.lineSeparator());
    }

    private void loadGeneratorCurve(Generator g, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setCurve(g.getId(), "generator_omegaPu"),
            setCurve(g.getId(), "generator_PGen"),
            setCurve(g.getId(), "generator_QGen"),
            setCurve(g.getId(), "generator_UStatorPu"),
            setCurve(g.getId(), "voltageRegulator_UcEfdPu"),
            setCurve(g.getId(), "voltageRegulator_EfdPu")) + System.lineSeparator());
    }

    private void loadLoadCurve(Load l, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setCurve(l.getId(), "load_PPu"),
            setCurve(l.getId(), "load_QPu")) + System.lineSeparator());
    }

    private String setCurve(String model, String variable) {
        return "  <curve model=\"" + model + "\" variable=\"" + variable + "\"/>";
    }

    private final Network network;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoCurves.class);
}
