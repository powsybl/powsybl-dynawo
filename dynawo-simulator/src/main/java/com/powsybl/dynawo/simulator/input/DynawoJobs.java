package com.powsybl.dynawo.simulator.input;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.dynawo.DynawoJob;
import com.powsybl.dynawo.DynawoModeler;
import com.powsybl.dynawo.DynawoOutputs;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.DynawoSimulation;
import com.powsybl.dynawo.DynawoSolver;

public class DynawoJobs {

    public DynawoJobs(DynawoProvider provider) {
        this.jobs = provider.getDynawoJob();
    }

    public void prepareFile(Path workingDir) {
        Path jobFile = workingDir.resolve("dynawoModel.jobs");
        try (Writer writer = Files.newBufferedWriter(jobFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), jobs()));

        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.jobs");
        }
    }

    private CharSequence jobs() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<dyn:jobs xmlns:dyn=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());
        jobs.forEach(job -> builder.append(String.join(System.lineSeparator(), job(job) + System.lineSeparator())));
        builder.append(String.join(System.lineSeparator(), "</dyn:jobs>") + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence job(DynawoJob job) {
        StringBuilder builder = new StringBuilder();
        String jobName = job.getName();
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:job name=\"" + jobName + "\">",
            solver(job.getSolver()),
            modeler(job.getModeler()),
            simulation(job.getSimulation()),
            output(job.getOutputs()),
            "  </dyn:job>") + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence solver(DynawoSolver solver) {
        StringBuilder builder = new StringBuilder();
        String solverLib = solver.getLib();
        String solverParams = solver.getFile();
        int solverParamsId = solver.getId();
        builder.append(String.join(System.lineSeparator(),
            "    <dyn:solver lib=\"" + solverLib + "\" parFile=\"" + solverParams + "\" parId=\"" + solverParamsId
                + "\"/>")
            + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence modeler(DynawoModeler modeler) {
        StringBuilder builder = new StringBuilder();
        String compileDir = modeler.getCompile();
        String iidmFile = modeler.getIidm();
        String parFile = modeler.getParameters();
        int parId = modeler.getParameterId();
        String dydFile = modeler.getDyd();
        builder.append(String.join(System.lineSeparator(),
            "    <dyn:modeler compileDir=\"" + compileDir + "\">",
            "      <dyn:network iidmFile=\"" + iidmFile + "\" parFile=\"" + parFile + "\" parId=\"" + parId + "\"/>",
            "      <dyn:dynModels dydFile=\"" + dydFile + "\"/>",
            "      <dyn:precompiledModels useStandardModels=\"true\"/>",
            "      <dyn:modelicaModels useStandardModels=\"true\"/>",
            "    </dyn:modeler>") + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence simulation(DynawoSimulation simulation) {
        StringBuilder builder = new StringBuilder();
        int startTime = simulation.getStartTime();
        int stopTime = simulation.getStopTime();
        boolean activeCriteria = simulation.isActiveCriteria();
        builder.append(String.join(System.lineSeparator(),
            "    <dyn:simulation startTime=\"" + startTime + "\" stopTime=\"" + stopTime + "\" activateCriteria=\""
                + Boolean.toString(activeCriteria) + "\"/>")
            + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence output(DynawoOutputs outputs) {
        StringBuilder builder = new StringBuilder();
        String outputDir = outputs.getDirectory();
        String curvesFile = outputs.getCurve();
        builder.append(String.join(System.lineSeparator(),
            "    <dyn:outputs directory=\"" + outputDir + "\">",
            "      <dyn:dumpInitValues local=\"true\" global=\"true\"/>",
            "      <dyn:curves inputFile=\"" + curvesFile + "\" exportMode=\"CSV\"/>",
            "      <dyn:timeline exportMode=\"TXT\"/>",
            "      <dyn:logs>",
            "        <dyn:appender tag=\"\" file=\"dynawo.log\" lvlFilter=\"DEBUG\"/>",
            "        <dyn:appender tag=\"COMPILE\" file=\"dynawoCompiler.log\" lvlFilter=\"DEBUG\"/>",
            "        <dyn:appender tag=\"MODELER\" file=\"dynawoModeler.log\" lvlFilter=\"DEBUG\"/>",
            "      </dyn:logs>",
            "    </dyn:outputs>") + System.lineSeparator());
        return builder.toString();
    }

    private final List<DynawoJob> jobs;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoJobs.class);
}
