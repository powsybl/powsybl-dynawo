/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EmptyTapChangerAutomatonXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build());
        dynamicModels.add(TapChangerAutomatonBuilder.of(network)
                .dynamicModelId("BBM_TC")
                .parameterSetId("tc")
                .staticId("LOAD")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "tap_changer_empty_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
        checkReporter("""
                + Test DYD
                  + Dynawaltz models processing
                     TapChangerAutomaton BBM_TC equipment is not a LoadWithTransformers, the automaton will be skipped
                """);
    }
}
