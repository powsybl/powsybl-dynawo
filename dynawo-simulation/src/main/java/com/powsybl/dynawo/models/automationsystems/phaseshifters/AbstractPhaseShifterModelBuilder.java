/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.phaseshifters;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.BuilderEquipment;
import com.powsybl.dynawo.builders.BuilderReports;
import com.powsybl.dynawo.builders.EquipmentChecker;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.automationsystems.AbstractAutomationSystemModelBuilder;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractPhaseShifterModelBuilder<T extends AbstractAutomationSystemModelBuilder<T>> extends AbstractAutomationSystemModelBuilder<T> {

    protected final BuilderEquipment<TwoWindingsTransformer> transformer;
    private static final EquipmentChecker<TwoWindingsTransformer> HAS_PHASE_TAP_CHANGER = (eq, f, r) -> {
        if (!eq.hasPhaseTapChanger()) {
            BuilderReports.reportMissingPhaseTapChanger(r, f, eq.getId());
            return false;
        }
        return true;
    };

    protected AbstractPhaseShifterModelBuilder(Network network, ModelConfig modelConfig, ReportNode parentReportNode) {
        super(network, modelConfig, parentReportNode);
        transformer = new BuilderEquipment<>(IdentifiableType.TWO_WINDINGS_TRANSFORMER.toString(),
                "transformer", reportNode);
    }

    public T transformer(String staticId) {
        transformer.addEquipment(staticId, network::getTwoWindingsTransformer, HAS_PHASE_TAP_CHANGER);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= transformer.checkEquipmentData();
    }
}
