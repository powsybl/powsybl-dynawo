package com.powsybl.dynawo.simulator.input;

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

public class DynawoCurves {

    public DynawoCurves(DynawoProvider provider) {
        this.curves = provider.getDynawoCurves();
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

        curves.forEach(curve -> builder.append(String.join(System.lineSeparator(), curve(curve) + System.lineSeparator())));

        builder.append(String.join(System.lineSeparator(),
            "</curvesInput>") + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence curve(DynawoCurve curve) {
        String model = curve.getModel();
        String variable = curve.getVariable();
        return setCurve(model, variable);
    }

    private String setCurve(String model, String variable) {
        return "  <curve model=\"" + model + "\" variable=\"" + variable + "\"/>";
    }

    private final List<DynawoCurve> curves;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoCurves.class);
}
