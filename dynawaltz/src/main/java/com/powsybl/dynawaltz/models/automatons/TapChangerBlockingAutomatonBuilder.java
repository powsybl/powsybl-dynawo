/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.BuildersUtil;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigsSingleton;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerBlockingAutomatonBuilder> {

    private static final String CATEGORY = "tcbs";
    private static final Map<String, ModelConfig> LIBS = ModelConfigsSingleton.getInstance().getModelConfigs(CATEGORY);
    private static final String U_MEASUREMENTS_FIELD = "uMeasurements";

    private final List<Load> loads = new ArrayList<>();
    private final List<TwoWindingsTransformer> transformers = new ArrayList<>();
    private final List<String> tapChangerAutomatonIds = new ArrayList<>();
    private List<Identifiable<?>> uMeasurements;

    public static TapChangerBlockingAutomatonBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static TapChangerBlockingAutomatonBuilder of(Network network, Reporter reporter) {
        return new TapChangerBlockingAutomatonBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static TapChangerBlockingAutomatonBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static TapChangerBlockingAutomatonBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, TapChangerBlockingAutomatonBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new TapChangerBlockingAutomatonBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected TapChangerBlockingAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    public TapChangerBlockingAutomatonBuilder transformers(String... staticIds) {
        Arrays.stream(staticIds).forEach(id -> {
            Identifiable<?> equipment = network.getIdentifiable(id);
            if (equipment == null) {
                tapChangerAutomatonIds.add(id);
            } else {
                switch (equipment.getType()) {
                    case LOAD -> loads.add((Load) equipment);
                    case TWO_WINDINGS_TRANSFORMER -> transformers.add((TwoWindingsTransformer) equipment);
                    default -> Reporters.reportStaticIdUnknown(reporter, U_MEASUREMENTS_FIELD, id, "LOAD/TWO_WINDINGS_TRANSFORMER");
                }
            }
        });
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(String... staticIds) {
        uMeasurements = new ArrayList<>();
        for (String staticId : staticIds) {
            Identifiable<?> measurementPoint = BuildersUtil.getActionConnectionPoint(network, staticId);
            if (measurementPoint == null) {
                Reporters.reportStaticIdUnknown(reporter, U_MEASUREMENTS_FIELD, staticId, "BUS/BUSBAR_SECTION");
            } else {
                uMeasurements.add(measurementPoint);
            }
        }
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(List<String>[] staticIdsArray) {
        uMeasurements = new ArrayList<>();
        for (List<String> staticIds : staticIdsArray) {
            for (String staticId : staticIds) {
                Identifiable<?> measurementPoint = BuildersUtil.getActionConnectionPoint(network, staticId);
                if (measurementPoint == null) {
                    Reporters.reportStaticIdUnknown(reporter, U_MEASUREMENTS_FIELD, staticId, "BUS/BUSBAR_SECTION");
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
            Reporters.reportFieldNotSet(reporter, U_MEASUREMENTS_FIELD);
            isInstantiable = false;
        } else if (uMeasurements.isEmpty()) {
            Reporters.reportEmptyList(reporter, U_MEASUREMENTS_FIELD);
            isInstantiable = false;
        }
        if (loads.isEmpty() && transformers.isEmpty() && tapChangerAutomatonIds.isEmpty()) {
            Reporters.reportEmptyList(reporter, "transformers");
            isInstantiable = false;
        }
    }

    @Override
    public TapChangerBlockingAutomaton build() {
        return isInstantiable() ? new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId, transformers, loads, tapChangerAutomatonIds, uMeasurements, getLib()) : null;
    }

    @Override
    protected TapChangerBlockingAutomatonBuilder self() {
        return this;
    }
}
