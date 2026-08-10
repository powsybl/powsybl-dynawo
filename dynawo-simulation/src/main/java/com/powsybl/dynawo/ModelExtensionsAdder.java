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
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.frequencysynchronizers.*;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawo.AbstractContextBuilder.distinctByDynamicId;
import static com.powsybl.dynawo.AbstractContextBuilder.supportedVersion;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO mutualize checkForbiddenDefaultModels context builder ?
//TODO Handle simplifiers ?
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
        MacroConnectionsAdder adder = new MacroConnectionsAdder(blackBoxModelSupplier, mc -> { },
                (s, f) -> f.apply(s), reportNode);
        dynamicModels.forEach(bbm -> bbm.createMacroConnections(adder));
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
        boolean hasFrequencySynchronizedModels = dynamicModels.stream().anyMatch(FrequencySynchronizedModel.class::isInstance);
        boolean hasSignalNModels = dynamicModels.stream().anyMatch(SignalNModel.class::isInstance);
        if (hasFrequencySynchronizedModels && hasSignalNModels) {
            throw new PowsyblException("Signal N and frequency synchronized generators cannot be used with one another");
        }
    }

    public void addModelExtensions() {
        setupData();
        dynamicModels.forEach(BlackBoxModel::createDynawoModelExtension);
    }
}
