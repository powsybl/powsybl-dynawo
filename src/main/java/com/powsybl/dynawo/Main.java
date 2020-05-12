package com.powsybl.dynawo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.commons.PowsyblException;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.computation.GroupCommandBuilder;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.DynamicSimulationResultImpl;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoConfig;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.xml.DynawoConstants;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

public final class Main {

    private static final String DEFAULT_DYNAWO_CMD_NAME = "myEnvDynawo.sh";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";
    private static String networkFile;

    private Main() {
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            LOGGER.info("Usage: com.powsybl.dynawo.Main networkFile.xiidm [parametersFile.json]");
            return;
        }
        networkFile = args[0];
        Network network = Importers.loadNetwork(networkFile);
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        parameters.addExtension(DynawoSimulationParameters.class, DynawoSimulationParameters.load());
        if (args.length > 1) {
            JsonDynamicSimulationParameters.update(parameters, Paths.get(args[1]));
        }

        try (ComputationManager computationManager = new LocalComputationManager(LocalComputationConfig.load())) {
            DynamicSimulationResult result = run(network, computationManager, parameters).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOGGER.error(e.toString());
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            System.exit(1);
        }
        System.exit(0);
    }

    private static CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager,
        DynamicSimulationParameters parameters) {
        DynawoConfig dynawoConfig = DynawoConfig.load();
        return computationManager.execute(
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug()),
            new AbstractExecutionHandler<DynamicSimulationResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) {
                    writeInputFiles(workingDir, network, parameters);
                    Command cmd = createCommand(workingDir.resolve(DynawoConstants.JOBS_FILENAME));
                    LOGGER.info("Command {}", cmd.toString(0));
                    return Collections.singletonList(new CommandExecution(cmd, 1));
                }

                @Override
                public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {
                    super.after(workingDir, report);
                    return new DynamicSimulationResultImpl(true, "");
                }

                private void writeInputFiles(Path workingDir, Network network, DynamicSimulationParameters parameters) {
                    DynawoContext context = new DynawoContext(network, parameters);
                    try {
                        Files.copy(Paths.get(networkFile), workingDir.resolve(DynawoConstants.NETWORK_FILENAME), StandardCopyOption.REPLACE_EXISTING);
                        JobsXml.write(workingDir, context);
                    } catch (IOException | XMLStreamException e) {
                        throw new PowsyblException(e.getMessage());
                    }
                }

                private Command createCommand(Path dynawoJobsFile) {
                    return new GroupCommandBuilder()
                        .id("dyn_fs")
                        .subCommand()
                        .program(getProgram())
                        .args("jobs", dynawoJobsFile.toAbsolutePath().toString())
                        .add()
                        .build();
                }

                private String getProgram() {
                    return dynawoConfig.getHomeDir().endsWith("/") ? dynawoConfig.getHomeDir() + DEFAULT_DYNAWO_CMD_NAME
                        : dynawoConfig.getHomeDir() + "/" + DEFAULT_DYNAWO_CMD_NAME;
                }
            });
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
}
