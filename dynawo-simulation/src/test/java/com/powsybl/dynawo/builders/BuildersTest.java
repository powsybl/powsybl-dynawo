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
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.events.*;
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

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class BuildersTest {

    private static final Network NETWORK = SvcTestCaseFactory.create();
    private static final String WRONG_LIB = "wrongLib";

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
        assertNull(BaseLoadBuilder.of(NETWORK, BaseLoadBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(LoadOneTransformerBuilder.of(NETWORK, LoadOneTransformerBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(LoadOneTransformerTapChangerBuilder.of(NETWORK, LoadOneTransformerTapChangerBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(LoadTwoTransformersBuilder.of(NETWORK, LoadTwoTransformersBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(LoadTwoTransformersTapChangersBuilder.of(NETWORK, LoadTwoTransformersTapChangersBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // Bus
        assertNull(StandardBusBuilder.of(NETWORK, StandardBusBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(InfiniteBusBuilder.of(NETWORK, InfiniteBusBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // Transformer
        assertNull(TransformerFixedRatioBuilder.of(NETWORK, TransformerFixedRatioBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // Line
        assertNull(LineBuilder.of(NETWORK, LineBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // Generator
        assertNull(BaseGeneratorBuilder.of(NETWORK, BaseGeneratorBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(SynchronizedGeneratorBuilder.of(NETWORK, SynchronizedGeneratorBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(SynchronousGeneratorBuilder.of(NETWORK, SynchronousGeneratorBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(InertialGridBuilder.of(NETWORK, InertialGridBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(WeccBuilder.of(NETWORK, WeccBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(GridFormingConverterBuilder.of(NETWORK, GridFormingConverterBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(SignalNGeneratorBuilder.of(NETWORK, SignalNGeneratorBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // HVDC
        assertNull(HvdcPBuilder.of(NETWORK, HvdcPBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(HvdcVscBuilder.of(NETWORK, HvdcVscBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // Shunt
        assertNull(BaseShuntBuilder.of(NETWORK, BaseShuntBuilder.getSupportedModelInfos().iterator().next().name()).build());
        // Static var comp
        assertNull(BaseStaticVarCompensatorBuilder.of(NETWORK, BaseStaticVarCompensatorBuilder.getSupportedModelInfos().iterator().next().name()).build());
    }

    @Test
    void testNotInstantiableAutomationSystem() {
        assertNull(UnderVoltageAutomationSystemBuilder.of(NETWORK, UnderVoltageAutomationSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(TapChangerBlockingAutomationSystemBuilder.of(NETWORK, TapChangerBlockingAutomationSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(TapChangerAutomationSystemBuilder.of(NETWORK, TapChangerAutomationSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(DynamicOverloadManagementSystemBuilder.of(NETWORK, DynamicOverloadManagementSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(DynamicTwoLevelOverloadManagementSystemBuilder.of(NETWORK, DynamicTwoLevelOverloadManagementSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(PhaseShifterIAutomationSystemBuilder.of(NETWORK, PhaseShifterIAutomationSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
        assertNull(PhaseShifterPAutomationSystemBuilder.of(NETWORK, PhaseShifterPAutomationSystemBuilder.getSupportedModelInfos().iterator().next().name()).build());
    }

    @Test
    void testNotInstantiableEvent() {
        assertNull(EventActivePowerVariationBuilder.of(NETWORK).build());
        assertNull(NodeFaultEventBuilder.of(NETWORK).build());
        assertNull(EventDisconnectionBuilder.of(NETWORK).build());
        assertNull(EventReactivePowerVariationBuilder.of(NETWORK).build());
        assertNull(EventReferenceVoltageVariationBuilder.of(NETWORK).build());
        assertNull(EventOpenSwitchBuilder.of(NETWORK).build());
        assertNull(EventCloseSwitchBuilder.of(NETWORK).build());
    }
}
