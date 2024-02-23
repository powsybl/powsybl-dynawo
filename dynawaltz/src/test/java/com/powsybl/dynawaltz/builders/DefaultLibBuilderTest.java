/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.dynawaltz.models.automatons.*;
import com.powsybl.dynawaltz.models.automatons.currentlimits.CurrentLimitAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentlimits.CurrentLimitTwoLevelsAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomatonBuilder;
import com.powsybl.dynawaltz.models.buses.*;
import com.powsybl.dynawaltz.models.lines.*;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.dynawaltz.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DefaultLibBuilderTest {

    private static final Network NETWORK = SvcTestCaseFactory.create();

    @Test
    void testDefaultLibAutomatons() {
        assertNotNull(UnderVoltageAutomatonBuilder.of(NETWORK));
        assertNotNull(TapChangerAutomatonBuilder.of(NETWORK));
        assertNotNull(TapChangerBlockingAutomatonBuilder.of(NETWORK));
        assertNotNull(CurrentLimitAutomatonBuilder.of(NETWORK));
        assertNotNull(CurrentLimitTwoLevelsAutomatonBuilder.of(NETWORK));
        assertNotNull(PhaseShifterPAutomatonBuilder.of(NETWORK));
        assertNotNull(PhaseShifterIAutomatonBuilder.of(NETWORK));
    }

    @Test
    void testDefaultLibEquipments() {
        // Load
        assertNotNull(BaseLoadBuilder.of(NETWORK));
        assertNotNull(LoadOneTransformerBuilder.of(NETWORK));
        assertNotNull(LoadOneTransformerTapChangerBuilder.of(NETWORK));
        assertNotNull(LoadTwoTransformersBuilder.of(NETWORK));
        assertNotNull(LoadTwoTransformersTapChangersBuilder.of(NETWORK));
        // Bus
        assertNotNull(StandardBusBuilder.of(NETWORK));
        assertNotNull(InfiniteBusBuilder.of(NETWORK));
        // Transformer
        assertNotNull(TransformerFixedRatioBuilder.of(NETWORK));
        // Line
        assertNotNull(LineBuilder.of(NETWORK));
        // Generator
        assertNotNull(GeneratorFictitiousBuilder.of(NETWORK));
        assertNotNull(SynchronizedGeneratorBuilder.of(NETWORK));
        assertNotNull(SynchronousGeneratorBuilder.of(NETWORK));
        assertNotNull(WeccBuilder.of(NETWORK));
        assertNotNull(GridFormingConverterBuilder.of(NETWORK));
        // HVDC
        assertNotNull(HvdcPBuilder.of(NETWORK));
        assertNotNull(HvdcVscBuilder.of(NETWORK));
        // Static var comp
        assertNotNull(BaseStaticVarCompensatorBuilder.of(NETWORK));
    }

}
