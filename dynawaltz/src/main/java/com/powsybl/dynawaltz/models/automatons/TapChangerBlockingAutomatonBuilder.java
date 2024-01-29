/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.*;
import com.powsybl.iidm.network.*;

import java.util.*;

import static com.powsybl.dynawaltz.builders.BuildersUtil.MEASUREMENT_POINT_TYPE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerBlockingAutomatonBuilder> {

    private static final String CATEGORY = "tcbs";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);
    private static final String TAP_CHANGER_TYPE = IdentifiableType.TWO_WINDINGS_TRANSFORMER + "/" + IdentifiableType.LOAD;
    private static final String U_MEASUREMENTS_FIELD = "uMeasurements";
    private static final String TRANSFORMER_FIELD = "transformers";

    private final BuilderEquipmentsList<Identifiable<?>> tapChangerEquipments;
    private final BuilderIdListEquipmentList<Identifiable<?>> uMeasurementPoints;

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
        tapChangerEquipments = new BuilderEquipmentsList<>(TAP_CHANGER_TYPE, TRANSFORMER_FIELD, true);
        uMeasurementPoints = new BuilderIdListEquipmentList<>(MEASUREMENT_POINT_TYPE, U_MEASUREMENTS_FIELD);
    }

    public TapChangerBlockingAutomatonBuilder transformers(String... staticIds) {
        tapChangerEquipments.addEquipments(staticIds, id -> this.getTapChangerEquipment(network, id));
        return self();
    }

    public TapChangerBlockingAutomatonBuilder transformers(Collection<String> staticIds) {
        tapChangerEquipments.addEquipments(staticIds, id -> this.getTapChangerEquipment(network, id));
        return self();
    }

    private Identifiable<?> getTapChangerEquipment(Network network, String staticId) {
        Identifiable<?> tapChangerEquipment = network.getTwoWindingsTransformer(staticId);
        return tapChangerEquipment != null ? tapChangerEquipment : network.getLoad(staticId);
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(String... staticIds) {
        uMeasurementPoints.addEquipments(staticIds, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(Collection<String> staticIds) {
        uMeasurementPoints.addEquipments(staticIds, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(List<String>[] staticIdsArray) {
        uMeasurementPoints.addEquipments(staticIdsArray, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(List<List<String>> staticIdsList) {
        uMeasurementPoints.addEquipments(staticIdsList, id -> BuildersUtil.getActionConnectionPoint(network, id));
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= tapChangerEquipments.checkEquipmentData(reporter);
        isInstantiable &= uMeasurementPoints.checkEquipmentData(reporter);
    }

    @Override
    public TapChangerBlockingAutomaton build() {
        return isInstantiable() ? new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId, tapChangerEquipments.getEquipments(), tapChangerEquipments.getMissingEquipmentId(), uMeasurementPoints.getEquipments(), getLib()) : null;
    }

    @Override
    protected TapChangerBlockingAutomatonBuilder self() {
        return this;
    }
}
