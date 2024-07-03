/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.dynawaltz.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.overloadmanagments.DynamicTwoLevelsOverloadManagementSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.models.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawaltz.models.lines.LineBuilder;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.dynawaltz.models.shunts.BaseShuntBuilder;
import com.powsybl.dynawaltz.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DefaultLibBuilderTest {

    private static final Network NETWORK = SvcTestCaseFactory.create();
    private static final String WRONG_LIB = "wrongLib";

    @Test
    void testDefaultLibAutomatons() {
        assertNotNull(UnderVoltageAutomationSystemBuilder.of(NETWORK));
        assertNotNull(TapChangerBlockingAutomationSystemBuilder.of(NETWORK));
        assertNotNull(TapChangerAutomationSystemBuilder.of(NETWORK));
        assertNotNull(DynamicOverloadManagementSystemBuilder.of(NETWORK));
        assertNotNull(DynamicTwoLevelsOverloadManagementSystemBuilder.of(NETWORK));
        assertNotNull(PhaseShifterIAutomationSystemBuilder.of(NETWORK));
        assertNotNull(PhaseShifterBlockingIAutomationSystemBuilder.of(NETWORK));
        assertNotNull(PhaseShifterPAutomationSystemBuilder.of(NETWORK));
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
        // Shunt
        assertNotNull(BaseShuntBuilder.of(NETWORK));
        // Static var comp
        assertNotNull(BaseStaticVarCompensatorBuilder.of(NETWORK));
    }

    @Test
    void testWrongLibAutomatons() {
        assertNull(UnderVoltageAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(TapChangerBlockingAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(TapChangerAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(DynamicOverloadManagementSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(DynamicTwoLevelsOverloadManagementSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(PhaseShifterIAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(PhaseShifterPAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
    }

    @Test
    void testWrongLibEquipments() {
        // Load
        assertNull(BaseLoadBuilder.of(NETWORK, WRONG_LIB));
        assertNull(LoadOneTransformerBuilder.of(NETWORK, WRONG_LIB));
        assertNull(LoadOneTransformerTapChangerBuilder.of(NETWORK, WRONG_LIB));
        assertNull(LoadTwoTransformersBuilder.of(NETWORK, WRONG_LIB));
        assertNull(LoadTwoTransformersTapChangersBuilder.of(NETWORK, WRONG_LIB));
        // Bus
        assertNull(StandardBusBuilder.of(NETWORK, WRONG_LIB));
        assertNull(InfiniteBusBuilder.of(NETWORK, WRONG_LIB));
        // Transformer
        assertNull(TransformerFixedRatioBuilder.of(NETWORK, WRONG_LIB));
        // Line
        assertNull(LineBuilder.of(NETWORK, WRONG_LIB));
        // Generator
        assertNull(GeneratorFictitiousBuilder.of(NETWORK, WRONG_LIB));
        assertNull(SynchronizedGeneratorBuilder.of(NETWORK, WRONG_LIB));
        assertNull(SynchronousGeneratorBuilder.of(NETWORK, WRONG_LIB));
        assertNull(WeccBuilder.of(NETWORK, WRONG_LIB));
        assertNull(GridFormingConverterBuilder.of(NETWORK, WRONG_LIB));
        // HVDC
        assertNull(HvdcPBuilder.of(NETWORK, WRONG_LIB));
        assertNull(HvdcVscBuilder.of(NETWORK, WRONG_LIB));
        // Shunt
        assertNull(BaseShuntBuilder.of(NETWORK, WRONG_LIB));
        // Static var comp
        assertNull(BaseStaticVarCompensatorBuilder.of(NETWORK, WRONG_LIB));
    }
}
