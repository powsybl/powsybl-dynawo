/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerBlockingAutomatonBuilder> {

    public static final String LIB = "TapChangerBlockingAutomaton";

    private final List<Load> loads = new ArrayList<>();
    private final List<TwoWindingsTransformer> transformers = new ArrayList<>();
    private final List<String> tapChangerAutomatonIds = new ArrayList<>();
    private List<Identifiable<?>> uMeasurements;

    public TapChangerBlockingAutomatonBuilder(Network network, String lib, Reporter reporter) {
        super(network, lib, reporter);
    }

    public TapChangerBlockingAutomatonBuilder transformers(String[] staticIds) {
        Arrays.stream(staticIds).forEach(id -> {
            Identifiable<?> equipment = network.getIdentifiable(id);
            if (equipment == null) {
                tapChangerAutomatonIds.add(id);
            } else {
                switch (equipment.getType()) {
                    case LOAD -> loads.add((Load) equipment);
                    case TWO_WINDINGS_TRANSFORMER -> transformers.add((TwoWindingsTransformer) equipment);
                    default -> Reporters.reportStaticIdUnknown(reporter, "uMeasurements", id, "LOAD/TWO_WINDINGS_TRANSFORMER");
                }
            }
        });
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(String[] staticIds) {
        uMeasurements = new ArrayList<>();
        for (String staticId : staticIds) {
            Identifiable<?> measurementPoint = network.getIdentifiable(staticId);
            if (measurementPoint == null || !BuildersUtil.isActionConnectionPoint(measurementPoint.getType())) {
                Reporters.reportStaticIdUnknown(reporter, "uMeasurements", staticId, "BUS/BUSBAR_SECTION");
            }
            uMeasurements.add(measurementPoint);
        }
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(List<String>[] staticIdsArray) {
        uMeasurements = new ArrayList<>();
        for (List<String> staticIds : staticIdsArray) {
            for (String staticId : staticIds) {
                Identifiable<?> measurementPoint = network.getIdentifiable(staticId);
                if (measurementPoint == null || !BuildersUtil.isActionConnectionPoint(measurementPoint.getType())) {
                    Reporters.reportStaticIdUnknown(reporter, "uMeasurements", staticId, "BUS/BUSBAR_SECTION");
                } else {
                    uMeasurements.add(measurementPoint);
                    break;
                }
            }
        }
        return self();
    }

    @Override
    protected void checkData() {
        if (uMeasurements == null) {
            Reporters.reportFieldNotSet(reporter, "uMeasurements");
            isInstantiable = false;
        } else if (uMeasurements.isEmpty()) {
            Reporters.reportEmptyList(reporter, "uMeasurements");
            isInstantiable = false;
        }
        if (loads.isEmpty() && transformers.isEmpty() && tapChangerAutomatonIds.isEmpty()) {
            Reporters.reportEmptyList(reporter, "transformers");
            isInstantiable = false;
        }
    }

    @Override
    public TapChangerBlockingAutomaton build() {
        return isInstantiable() ? new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId, transformers, loads, tapChangerAutomatonIds, uMeasurements) : null;
    }

    @Override
    protected TapChangerBlockingAutomatonBuilder self() {
        return this;
    }
}
