/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.models.generators.SignalNGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MixedFreqSynchSignalNXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMoreGenerators();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SignalNGeneratorBuilder.of(network, "GeneratorPQPropSignalN")
                .staticId("GEN")
                .parameterSetId("n")
                .build());
        dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                .staticId("GEN2")
                .parameterSetId("n")
                .build());
    }

    @Override
    protected void setupDynawoContext() {
        // empty to call super in test
    }

    @Test
    void testExceptions() {
        Exception e = assertThrows(PowsyblException.class, super::setupDynawoContext);
        assertEquals("Signal N and frequency synchronized generators cannot be used with one another", e.getMessage());
    }
}
