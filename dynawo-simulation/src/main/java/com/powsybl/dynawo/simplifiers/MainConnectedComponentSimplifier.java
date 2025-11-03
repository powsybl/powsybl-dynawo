/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.simplifiers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.iidm.network.*;

import java.util.function.Predicate;

/**
 * Filter equipment models in the main connected component
 *
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(ModelsRemovalSimplifier.class)
public final class MainConnectedComponentSimplifier implements ModelsRemovalSimplifier {

    private static final ModelSimplifierInfo MODEL_INFO = new ModelSimplifierInfo("mainComponentEquipment",
            "Filter equipment in the main connected component",
            SIMPLIFIER_TYPE);

    @Override
    public ModelSimplifierInfo getSimplifierInfo() {
        return MODEL_INFO;
    }

    @Override
    public Predicate<BlackBoxModel> getModelRemovalPredicate(ReportNode reportNode) {
        ReportNode simplifierReport = SimplifierReports.createMainConnectedComponentSimplifierReportNode(reportNode);
        return model -> {
            if (model instanceof EquipmentBlackBoxModel eqBbm) {
                return isMainConnected(eqBbm, simplifierReport);
            }
            return true;
        };
    }

    private static boolean isMainConnected(EquipmentBlackBoxModel bbm, ReportNode reportNode) {
        return switch (bbm.getEquipment()) {
            case Injection<?> inj -> isMainConnected(inj, reportNode, bbm);
            case Branch<?> br -> isMainConnected(br, reportNode, bbm);
            case HvdcLine l -> isMainConnected(l.getConverterStation1(), reportNode, bbm)
                    || isMainConnected(l.getConverterStation2(), reportNode, bbm);
            case Bus b -> isMainConnected(b, reportNode, bbm);
            default -> true;
        };
    }

    private static boolean isMainConnected(Injection<?> equipment, ReportNode reportNode, BlackBoxModel bbm) {
        return isMainConnected(equipment.getTerminal().getBusBreakerView().getBus(), reportNode, bbm);
    }

    private static boolean isMainConnected(Branch<?> equipment, ReportNode reportNode, BlackBoxModel bbm) {
        return isMainConnected(equipment.getTerminal1().getBusBreakerView().getBus(), reportNode, bbm)
                || isMainConnected(equipment.getTerminal2().getBusBreakerView().getBus(), reportNode, bbm);
    }

    private static boolean isMainConnected(Bus bus, ReportNode reportNode, BlackBoxModel bbm) {
        if (!bus.isInMainConnectedComponent()) {
            SimplifierReports.reportSubConnectedComponent(reportNode, bbm.getName(), bbm.getDynamicModelId());
            return false;
        }
        return true;
    }
}
