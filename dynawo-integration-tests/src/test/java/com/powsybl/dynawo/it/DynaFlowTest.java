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
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.dynaflow.DynaFlowProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class DynaFlowTest extends AbstractDynawoTest {

    private DynaFlowProvider provider;

    private LoadFlowParameters parameters;

    private DynaFlowParameters dynaFlowParameters;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DynaFlowConfig config = new DynaFlowConfig(Path.of("/dynaflow-launcher"), false);
        provider = new DynaFlowProvider(() -> config);
        parameters = new LoadFlowParameters();
        dynaFlowParameters = new DynaFlowParameters();
        parameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);
    }

    @Test
    public void test() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14-disconnectline", "IEEE14.iidm")));
        LoadFlowResult result = provider.run(network, computationManager, VariantManagerConstants.INITIAL_VARIANT_ID, parameters)
                .join();
        assertFalse(result.isOk()); // FIXME
        assertEquals(1, result.getComponentResults().size());
        LoadFlowResult.ComponentResult componentResult = result.getComponentResults().get(0);
        assertEquals(LoadFlowResult.ComponentResult.Status.FAILED, componentResult.getStatus());
    }
}
