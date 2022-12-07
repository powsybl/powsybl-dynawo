/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.dynaflow.DynaFlowConfig;
import com.powsybl.dynaflow.DynaFlowProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class DynaFlowTest extends AbstractDynawoTest {

    @Test
    @Ignore
    public void test() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14-disconnectline", "IEEE14.iidm")));
        DynaFlowConfig config = new DynaFlowConfig(Path.of("/dynawo"), false);
        DynaFlowProvider provider = new DynaFlowProvider(() -> config);
        LoadFlowParameters parameters = new LoadFlowParameters();
        LoadFlowResult result = provider.run(network, computationManager, VariantManagerConstants.INITIAL_VARIANT_ID, parameters)
                .join();
        assertTrue(result.isOk());
    }
}
