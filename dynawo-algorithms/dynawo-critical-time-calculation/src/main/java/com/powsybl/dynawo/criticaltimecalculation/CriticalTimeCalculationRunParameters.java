/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationManager;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationRunParameters {

    private static final Supplier<CriticalTimeCalculationParameters> DEFAULT_CTC_PARAMETERS_SUPPLIER = CriticalTimeCalculationParameters::load;
    private static final Supplier<ComputationManager> DEFAULT_COMPUTATION_MANAGER_SUPPLIER = LocalComputationManager::getDefault;

    private CriticalTimeCalculationParameters criticalTimeCalculationParameters;
    private ComputationManager computationManager;
    private ReportNode reportNode = ReportNode.NO_OP;

    /**
     * Returns a {@link CriticalTimeCalculationRunParameters} instance with default value on each field.
     * @return the CriticalTimeCalculationRunParameters instance.
     */
    public static CriticalTimeCalculationRunParameters getDefault() {
        return new CriticalTimeCalculationRunParameters()
                .setCriticalTimeCalculationParameters(DEFAULT_CTC_PARAMETERS_SUPPLIER.get())
                .setComputationManager(DEFAULT_COMPUTATION_MANAGER_SUPPLIER.get());
    }

    /**
     * {@link CriticalTimeCalculationParameters} getter<br>
     * If null, sets the field to its default value with {@link #DEFAULT_CTC_PARAMETERS_SUPPLIER} before returning it.
     */
    public CriticalTimeCalculationParameters getCriticalTimeCalculationParameters() {
        if (criticalTimeCalculationParameters == null) {
            setCriticalTimeCalculationParameters(DEFAULT_CTC_PARAMETERS_SUPPLIER.get());
        }
        return criticalTimeCalculationParameters;
    }

    /**
     * {@link ComputationManager} getter<br>
     * If null, sets the field to its default value with {@link #DEFAULT_COMPUTATION_MANAGER_SUPPLIER} before returning it.
     */
    public ComputationManager getComputationManager() {
        if (computationManager == null) {
            setComputationManager(DEFAULT_COMPUTATION_MANAGER_SUPPLIER.get());
        }
        return computationManager;
    }

    public ReportNode getReportNode() {
        return reportNode;
    }

    /**
     * Sets the security analysis parameters, see {@link CriticalTimeCalculationRunParameters}.
     */
    public CriticalTimeCalculationRunParameters setCriticalTimeCalculationParameters(CriticalTimeCalculationParameters criticalTimeCalculationParameters) {
        Objects.requireNonNull(criticalTimeCalculationParameters, "Critical time calculation parameters should not be null");
        this.criticalTimeCalculationParameters = criticalTimeCalculationParameters;
        return this;
    }

    /**
     * Sets the computationManager handling command execution.
     */
    public CriticalTimeCalculationRunParameters setComputationManager(ComputationManager computationManager) {
        Objects.requireNonNull(computationManager, "ComputationManager should not be null");
        this.computationManager = computationManager;
        return this;
    }

    /**
     * Sets the reportNode used for functional logs, see {@link ReportNode}
     */
    public CriticalTimeCalculationRunParameters setReportNode(ReportNode reportNode) {
        Objects.requireNonNull(reportNode, "ReportNode should not be null");
        this.reportNode = reportNode;
        return this;
    }
}
