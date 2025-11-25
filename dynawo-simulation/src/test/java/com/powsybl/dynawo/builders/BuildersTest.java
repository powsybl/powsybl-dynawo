/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.events.EventReactivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.NodeFaultEventBuilder;
import com.powsybl.dynawo.models.generators.*;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.lines.LineBuilder;
import com.powsybl.dynawo.models.loads.*;
import com.powsybl.dynawo.models.shunts.BaseShuntBuilder;
import com.powsybl.dynawo.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class BuildersTest {

    private static final Network NETWORK = SvcTestCaseFactory.create();
    private static final String WRONG_LIB = "wrongLib";

    @Test
    void testDefaultLibAutomationSystems() {
        assertNotNull(UnderVoltageAutomationSystemBuilder.of(NETWORK));
        assertNotNull(TapChangerBlockingAutomationSystemBuilder.of(NETWORK));
        assertNotNull(TapChangerAutomationSystemBuilder.of(NETWORK));
        assertNotNull(DynamicOverloadManagementSystemBuilder.of(NETWORK));
        assertNotNull(DynamicTwoLevelOverloadManagementSystemBuilder.of(NETWORK));
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
        assertNotNull(BaseGeneratorBuilder.of(NETWORK));
        assertNotNull(SynchronizedGeneratorBuilder.of(NETWORK));
        assertNotNull(SynchronousGeneratorBuilder.of(NETWORK));
        assertNotNull(InertialGridBuilder.of(NETWORK));
        assertNotNull(WeccBuilder.of(NETWORK));
        assertNotNull(GridFormingConverterBuilder.of(NETWORK));
        assertNotNull(SignalNGeneratorBuilder.of(NETWORK));
        // HVDC
        assertNotNull(HvdcPBuilder.of(NETWORK));
        assertNotNull(HvdcVscBuilder.of(NETWORK));
        // Shunt
        assertNotNull(BaseShuntBuilder.of(NETWORK));
        // Static var comp
        assertNotNull(BaseStaticVarCompensatorBuilder.of(NETWORK));
    }

    @Test
    void testDefaultModelGetterAutomationSystems() {
        assertNotNull(UnderVoltageAutomationSystemBuilder.getDefaultModelInfo());
        assertNotNull(TapChangerBlockingAutomationSystemBuilder.getDefaultModelInfo());
        assertNotNull(TapChangerAutomationSystemBuilder.getDefaultModelInfo());
        assertNotNull(DynamicOverloadManagementSystemBuilder.getDefaultModelInfo());
        assertNotNull(DynamicTwoLevelOverloadManagementSystemBuilder.getDefaultModelInfo());
        assertNotNull(PhaseShifterIAutomationSystemBuilder.getDefaultModelInfo());
        assertNotNull(PhaseShifterBlockingIAutomationSystemBuilder.getDefaultModelInfo());
        assertNotNull(PhaseShifterPAutomationSystemBuilder.getDefaultModelInfo());
    }

    @Test
    void testDefaultModelGetterEquipments() {
        // Load
        assertNotNull(BaseLoadBuilder.getDefaultModelInfo());
        assertNotNull(LoadOneTransformerBuilder.getDefaultModelInfo());
        assertNotNull(LoadOneTransformerTapChangerBuilder.getDefaultModelInfo());
        assertNotNull(LoadTwoTransformersBuilder.getDefaultModelInfo());
        assertNotNull(LoadTwoTransformersTapChangersBuilder.getDefaultModelInfo());
        // Bus
        assertNotNull(StandardBusBuilder.getDefaultModelInfo());
        assertNotNull(InfiniteBusBuilder.getDefaultModelInfo());
        // Transformer
        assertNotNull(TransformerFixedRatioBuilder.getDefaultModelInfo());
        // Line
        assertNotNull(LineBuilder.getDefaultModelInfo());
        // Generator
        assertNotNull(BaseGeneratorBuilder.getDefaultModelInfo());
        assertNotNull(SynchronizedGeneratorBuilder.getDefaultModelInfo());
        assertNotNull(SynchronousGeneratorBuilder.getDefaultModelInfo());
        assertNotNull(InertialGridBuilder.getDefaultModelInfo());
        assertNotNull(WeccBuilder.getDefaultModelInfo());
        assertNotNull(GridFormingConverterBuilder.getDefaultModelInfo());
        assertNotNull(SignalNGeneratorBuilder.getDefaultModelInfo());
        // HVDC
        assertNotNull(HvdcPBuilder.getDefaultModelInfo());
        assertNotNull(HvdcVscBuilder.getDefaultModelInfo());
        // Shunt
        assertNotNull(BaseShuntBuilder.getDefaultModelInfo());
        // Static var comp
        assertNotNull(BaseStaticVarCompensatorBuilder.getDefaultModelInfo());
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
        assertNull(BaseGeneratorBuilder.of(NETWORK, WRONG_LIB));
        assertNull(SynchronizedGeneratorBuilder.of(NETWORK, WRONG_LIB));
        assertNull(SynchronousGeneratorBuilder.of(NETWORK, WRONG_LIB));
        assertNull(InertialGridBuilder.of(NETWORK, WRONG_LIB));
        assertNull(WeccBuilder.of(NETWORK, WRONG_LIB));
        assertNull(GridFormingConverterBuilder.of(NETWORK, WRONG_LIB));
        assertNull(SignalNGeneratorBuilder.of(NETWORK, WRONG_LIB));
        // HVDC
        assertNull(HvdcPBuilder.of(NETWORK, WRONG_LIB));
        assertNull(HvdcVscBuilder.of(NETWORK, WRONG_LIB));
        // Shunt
        assertNull(BaseShuntBuilder.of(NETWORK, WRONG_LIB));
        // Static var comp
        assertNull(BaseStaticVarCompensatorBuilder.of(NETWORK, WRONG_LIB));
    }

    @Test
    void testWrongLibAutomationSystem() {
        assertNull(UnderVoltageAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(TapChangerBlockingAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(TapChangerAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(DynamicOverloadManagementSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(DynamicTwoLevelOverloadManagementSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(PhaseShifterIAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
        assertNull(PhaseShifterPAutomationSystemBuilder.of(NETWORK, WRONG_LIB));
    }

    @Test
    void testNotInstantiableEquipment() {
        // Load
        assertNull(BaseLoadBuilder.of(NETWORK).build());
        assertNull(LoadOneTransformerBuilder.of(NETWORK).build());
        assertNull(LoadOneTransformerTapChangerBuilder.of(NETWORK).build());
        assertNull(LoadTwoTransformersBuilder.of(NETWORK).build());
        assertNull(LoadTwoTransformersTapChangersBuilder.of(NETWORK).build());
        // Bus
        assertNull(StandardBusBuilder.of(NETWORK).build());
        assertNull(InfiniteBusBuilder.of(NETWORK).build());
        // Transformer
        assertNull(TransformerFixedRatioBuilder.of(NETWORK).build());
        // Line
        assertNull(LineBuilder.of(NETWORK).build());
        // Generator
        assertNull(BaseGeneratorBuilder.of(NETWORK).build());
        assertNull(SynchronizedGeneratorBuilder.of(NETWORK).build());
        assertNull(SynchronousGeneratorBuilder.of(NETWORK).build());
        assertNull(InertialGridBuilder.of(NETWORK).build());
        assertNull(WeccBuilder.of(NETWORK).build());
        assertNull(GridFormingConverterBuilder.of(NETWORK).build());
        assertNull(SignalNGeneratorBuilder.of(NETWORK).build());
        // HVDC
        assertNull(HvdcPBuilder.of(NETWORK).build());
        assertNull(HvdcVscBuilder.of(NETWORK).build());
        // Shunt
        assertNull(BaseShuntBuilder.of(NETWORK).build());
        // Static var comp
        assertNull(BaseStaticVarCompensatorBuilder.of(NETWORK).build());
    }

    @Test
    void testNotInstantiableAutomationSystem() {
        assertNull(UnderVoltageAutomationSystemBuilder.of(NETWORK).build());
        assertNull(TapChangerBlockingAutomationSystemBuilder.of(NETWORK).build());
        assertNull(TapChangerAutomationSystemBuilder.of(NETWORK).build());
        assertNull(DynamicOverloadManagementSystemBuilder.of(NETWORK).build());
        assertNull(DynamicTwoLevelOverloadManagementSystemBuilder.of(NETWORK).build());
        assertNull(PhaseShifterIAutomationSystemBuilder.of(NETWORK).build());
        assertNull(PhaseShifterPAutomationSystemBuilder.of(NETWORK).build());
    }

    @Test
    void testNotInstantiableEvent() {
        assertNull(EventActivePowerVariationBuilder.of(NETWORK).build());
        assertNull(NodeFaultEventBuilder.of(NETWORK).build());
        assertNull(EventDisconnectionBuilder.of(NETWORK).build());
        assertNull(EventReactivePowerVariationBuilder.of(NETWORK).build());
    }
}
