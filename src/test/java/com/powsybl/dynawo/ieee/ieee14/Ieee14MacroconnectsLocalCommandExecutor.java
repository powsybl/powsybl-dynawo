/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee.ieee14;

import com.powsybl.dynawo.DynawoParameters;
import com.powsybl.dynawo.ieee.AbstractDynawoLocalCommandExecutor;
import com.powsybl.iidm.network.Network;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.powsybl.dynawo.xml.DynawoConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee14MacroconnectsLocalCommandExecutor extends AbstractDynawoLocalCommandExecutor {

    public Ieee14MacroconnectsLocalCommandExecutor(FileSystem fileSystem, Network network, DynawoParameters dynawoParameters) {
        super(fileSystem, network, dynawoParameters);
    }

    @Override
    protected void validateInputs(Path workingDir) throws IOException {
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/powsybl_dynawo.xiidm"), Files.newInputStream(workingDir.resolve(NETWORK_FILENAME)));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/powsybl_dynawo.jobs"), Files.newInputStream(workingDir.resolve(JOBS_FILENAME)));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/powsybl_dynawo.dyd"), Files.newInputStream(workingDir.resolve(DYD_FILENAME)));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/models.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynawoParameters.getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/network.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynawoParameters.getNetwork().getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/solvers.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynawoParameters.getSolver().getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/ieee14bus.par"), Files.newInputStream(workingDir.resolve(network.getId() + ".par")));
        compareXml(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-inputs/powsybl_dynawo.crv"), Files.newInputStream(workingDir.resolve(CRV_FILENAME)));
    }

    @Override
    protected void copyOutputs(Path workingDir) throws IOException  {
        Path output = Files.createDirectories(workingDir.resolve("outputs/curves").toAbsolutePath());
        Files.copy(getClass().getResourceAsStream("/ieee14-macroconnects/dynawo-outputs/curves.csv"), output.resolve("curves.csv"));
    }

}
