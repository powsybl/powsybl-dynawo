package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

public class DynawoCurves {

    public DynawoCurves(Network network) {
        this.network = network;
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.crv");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), curves()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CharSequence curves() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            "<?xml version='1.0' encoding='UTF-8'?>",
            "<!--",
            "    Copyright (c) 2015-2019, RTE (http://www.rte-france.com)",
            "    See AUTHORS.txt",
            "    All rights reserved.",
            "    This Source Code Form is subject to the terms of the Mozilla Public",
            "    License, v. 2.0. If a copy of the MPL was not distributed with this",
            "    file, you can obtain one at http://mozilla.org/MPL/2.0/.",
            "    SPDX-License-Identifier: MPL-2.0",
            "",
            "    This file is part of Dynawo, an hybrid C++/Modelica open source time domain",
            "    simulation tool for power systems.",
            "-->",
            "<curvesInput xmlns=\"http://www.rte-france.com/dynawo\">",
            "<!--Curves for scenario-->",
            System.lineSeparator()));

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
            "</curvesInput>",
            System.lineSeparator()));
        return builder.toString();
    }

    private void loadBusCurve(Bus b, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "  <curve model=\"NETWORK\" variable=\"" + b.getId() + "_Upu_value\"/>",
            System.lineSeparator()));
    }

    private void loadGeneratorCurve(Generator g, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "  <curve model=\"" + g.getId() + "\" variable=\"generator_omegaPu\"/>",
            "  <curve model=\"" + g.getId() + "\" variable=\"generator_PGen\"/>",
            "  <curve model=\"" + g.getId() + "\" variable=\"generator_QGen\"/>",
            "  <curve model=\"" + g.getId() + "\" variable=\"generator_UStatorPu\"/>",
            "  <curve model=\"" + g.getId() + "\" variable=\"voltageRegulator_UcEfdPu\"/>",
            "  <curve model=\"" + g.getId() + "\" variable=\"voltageRegulator_EfdPu\"/>",
            System.lineSeparator()));
    }

    private void loadLoadCurve(Load l, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "  <curve model=\"" + l.getId() + "\" variable=\"load_PPu\"/>",
            "  <curve model=\"" + l.getId() + "\" variable=\"load_QPu\"/>",
            System.lineSeparator()));
    }

    private final Network network;
}
