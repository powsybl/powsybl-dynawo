/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.builders.VersionInterval;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.frequencysynchronizers.*;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO mutualize code with context builder
//TODO handle simplifiers
public class ModelExtensionsAdder {

    protected final Network network;
    protected String workingVariantId;
    protected List<BlackBoxModel> dynamicModels;
    protected BlackBoxModelSupplier blackBoxModelSupplier;
    protected DynawoVersion dynawoVersion = DynawoConstants.VERSION_MIN;
    protected ReportNode reportNode = ReportNode.NO_OP;

    protected ModelExtensionsAdder(Network network, List<BlackBoxModel> dynamicModels) {
        this.network = network;
        this.dynamicModels = dynamicModels;
    }

    public ModelExtensionsAdder workingVariantId(String workingVariantId) {
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        return this;
    }

    public ModelExtensionsAdder currentVersion(DynawoVersion currentVersion) {
        this.dynawoVersion = currentVersion;
        return this;
    }

    public ModelExtensionsAdder reportNode(ReportNode reportNode) {
        this.reportNode = reportNode;
        return this;
    }

    protected void setupData() {
        if (workingVariantId == null) {
            workingVariantId = network.getVariantManager().getWorkingVariantId();
        }
        setupDynamicModels();
    }

    private void setupDynamicModels() {
        Stream<BlackBoxModel> uniqueIdsDynamicModels = Objects.requireNonNull(dynamicModels).stream()
                .filter(distinctByDynamicId(reportNode).and(supportedVersion(dynawoVersion, reportNode)));

        dynamicModels = uniqueIdsDynamicModels.collect(Collectors.toCollection(ArrayList::new));
        checkFrequencySynchronizer();
        blackBoxModelSupplier = BlackBoxModelSupplier.createFrom(dynamicModels);
        checkForbiddenDefaultModels();
    }

    private void checkForbiddenDefaultModels() {
        if (dynamicModels.stream().anyMatch(BlackBoxModel::needMandatoryDynamicModels)) {
            checkEquipmentDynamicModels(network.getLines());
            checkEquipmentDynamicModels(network.getTwoWindingsTransformers());
            checkEquipmentDynamicModels(network.getBusBreakerView().getBuses());
        }
    }

    private <E extends Identifiable<E>> void checkEquipmentDynamicModels(Iterable<E> equipments) {
        for (E equipment : equipments) {
            if (!blackBoxModelSupplier.hasDynamicModel(equipment)) {
                throw new PowsyblException(String.format("At least one dynamic model forbid default models and the equipment %s does not possess a dynamic model", equipment.getId()));
            }
        }
    }

    private void checkFrequencySynchronizer() {
        List<SignalNModel> signalNModels = filterDynamicModels(SignalNModel.class);
        List<FrequencySynchronizedModel> frequencySynchronizedModels = filterDynamicModels(FrequencySynchronizedModel.class);
        boolean hasFrequencySynchronizedModels = !frequencySynchronizedModels.isEmpty();
        boolean hasSignalNModels = !signalNModels.isEmpty();
        if (hasFrequencySynchronizedModels && hasSignalNModels) {
            throw new PowsyblException("Signal N and frequency synchronized generators cannot be used with one another");
        }
    }

    private <R extends Model> List<R> filterDynamicModels(Class<R> modelClass) {
        return dynamicModels.stream()
                .filter(modelClass::isInstance)
                .map(modelClass::cast)
                .toList();
    }

    protected static Predicate<BlackBoxModel> distinctByDynamicId(ReportNode reportNode) {
        Set<String> seen = new HashSet<>();
        return bbm -> {
            if (!seen.add(bbm.getDynamicModelId())) {
                DynawoSimulationReports.reportDuplicateDynamicId(reportNode, bbm.getDynamicModelId(), bbm.getName());
                return false;
            }
            return true;
        };
    }

    protected static Predicate<BlackBoxModel> supportedVersion(DynawoVersion currentVersion, ReportNode reportNode) {
        return bbm -> {
            VersionInterval versionInterval = bbm.getVersionInterval();
            if (currentVersion.compareTo(versionInterval.min()) < 0) {
                DynawoSimulationReports.reportDynawoVersionTooHigh(reportNode, bbm.getName(), bbm.getDynamicModelId(), versionInterval.min(), currentVersion);
                return false;
            }
            if (versionInterval.max() != null && currentVersion.compareTo(versionInterval.max()) >= 0) {
                DynawoSimulationReports.reportDynawoVersionTooLow(reportNode, bbm.getName(), bbm.getDynamicModelId(), versionInterval.max(), currentVersion, versionInterval.endCause());
                return false;
            }
            return true;
        };
    }

    public void addModelExtensions() {
        setupData();
        dynamicModels.forEach(BlackBoxModel::createDynawoModelInfoExtension);
    }
}
