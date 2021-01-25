/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.ieee.ieee14;

import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.ieee.AbstractDynaWaltzLocalCommandExecutor;
import com.powsybl.iidm.network.Network;

import static com.powsybl.dynawaltz.xml.DynawaltzConstants.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee14CurrentLimitAutomatonLocalCommandExecutor extends AbstractDynaWaltzLocalCommandExecutor {

    public Ieee14CurrentLimitAutomatonLocalCommandExecutor(FileSystem fileSystem, Network network, DynaWaltzParameters dynaWaltzParameters) {
        super(fileSystem, network, dynaWaltzParameters);
    }

    @Override
    protected void validateInputs(Path workingDir) throws IOException {
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/powsybl_dynawaltz.xiidm"), Files.newInputStream(workingDir.resolve(NETWORK_FILENAME)));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/powsybl_dynawaltz.jobs"), Files.newInputStream(workingDir.resolve(JOBS_FILENAME)));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/powsybl_dynawaltz.dyd"), Files.newInputStream(workingDir.resolve(DYD_FILENAME)));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/models.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynaWaltzParameters.getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/network.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynaWaltzParameters.getNetwork().getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/solvers.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynaWaltzParameters.getSolver().getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/ieee14bus.par"), Files.newInputStream(workingDir.resolve(network.getId() + ".par")));
        compareXml(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-inputs/powsybl_dynawaltz.crv"), Files.newInputStream(workingDir.resolve(CRV_FILENAME)));
    }

    @Override
    protected void copyOutputs(Path workingDir) throws IOException  {
        Path output = Files.createDirectories(workingDir.resolve("outputs/curves").toAbsolutePath());
        Files.copy(getClass().getResourceAsStream("/ieee14-currentlimitautomaton/dynawaltz-outputs/curves.csv"), output.resolve("curves.csv"));
    }

}
