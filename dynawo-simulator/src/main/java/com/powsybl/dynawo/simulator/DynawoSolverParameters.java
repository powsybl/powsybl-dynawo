package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.powsybl.iidm.network.Network;

public class DynawoSolverParameters {

    public DynawoSolverParameters(Network network, DynawoConfig config) {
        this.network = network;
        this.config = config;
    }

    public void prepareFile() {
        Path parFile = config.getWorkingDir().resolve("solvers.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), parameters()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CharSequence parameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            "<parametersSet xmlns=\"http://www.ret-france.com/dynawo\">",
            "<!-- IDA order 1 solver -->",
            "<set id=\"1\">",
            "<par type=\"INT\" name=\"order\" value=\"1\"/>",
            "<par type=\"DOUBLE\" name=\"initStep\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"minStep\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"maxStep\" value=\"10\"/>",
            "<par type=\"DOUBLE\" name=\"absAccuracy\" value=\"1e-4\"/>",
            "<par type=\"DOUBLE\" name=\"relAccuracy\" value=\"1e-4\"/>",
            "</set>",
            "<!-- IDA order 2 solver -->",
            "<set id=\"2\">",
            "<par type=\"INT\" name=\"order\" value=\"2\"/>",
            "<par type=\"DOUBLE\" name=\"initStep\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"minStep\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"maxStep\" value=\"10\"/>",
            "<par type=\"DOUBLE\" name=\"absAccuracy\" value=\"1e-4\"/>",
            "<par type=\"DOUBLE\" name=\"relAccuracy\" value=\"1e-4\"/>",
            "</set>",
            "<!-- Simplified solver without step recalculation -->",
            "<set id=\"3\">",
            "<par type=\"DOUBLE\" name=\"hMin\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"hMax\" value=\"1\"/>",
            "<par type=\"DOUBLE\" name=\"kReduceStep\" value=\"0.5\"/>",
            "<par type=\"INT\" name=\"nEff\" value=\"10\"/>",
            "<par type=\"INT\" name=\"nDeadband\" value=\"2\"/>",
            "<par type=\"INT\" name=\"maxRootRestart\" value=\"3\"/>",
            "<par type=\"INT\" name=\"maxNewtonTry\" value=\"10\"/>",
            "<par type=\"STRING\" name=\"linearSolverName\" value=\"KLU\"/>",
            "<par type=\"BOOL\" name=\"recalculateStep\" value=\"false\"/>",
            "</set>",
            "<!-- Simplified solver with step recalculation -->",
            "<set id=\"4\">",
            "<par type=\"DOUBLE\" name=\"hMin\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"hMax\" value=\"1\"/>",
            "<par type=\"DOUBLE\" name=\"kReduceStep\" value=\"0.5\"/>",
            "<par type=\"INT\" name=\"nEff\" value=\"10\"/>",
            "<par type=\"INT\" name=\"nDeadband\" value=\"2\"/>",
            "<par type=\"INT\" name=\"maxRootRestart\" value=\"3\"/>",
            "<par type=\"INT\" name=\"maxNewtonTry\" value=\"10\"/>",
            "<par type=\"STRING\" name=\"linearSolverName\" value=\"KLU\"/>",
            "<par type=\"BOOL\" name=\"recalculateStep\" value=\"true\"/>",
            "</set>",
            "<!-- IDA order 2 solver with higher accuracy requirements -->",
            "<set id=\"5\">",
            "<par type=\"INT\" name=\"order\" value=\"2\"/>",
            "<par type=\"DOUBLE\" name=\"initStep\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"minStep\" value=\"0.000001\"/>",
            "<par type=\"DOUBLE\" name=\"maxStep\" value=\"10\"/>",
            "<par type=\"DOUBLE\" name=\"absAccuracy\" value=\"1e-6\"/>",
            "<par type=\"DOUBLE\" name=\"relAccuracy\" value=\"1e-6\"/>",
            "</set>",
            "</parametersSet>"));
        return builder.toString();
    }

    private final Network network;
    private final DynawoConfig config;
}
