/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationManager;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationRunParameters {

    private static final Supplier<MarginCalculationParameters> DEFAULT_MC_PARAMETERS_SUPPLIER = MarginCalculationParameters::load;
    private static final Supplier<ComputationManager> DEFAULT_COMPUTATION_MANAGER_SUPPLIER = LocalComputationManager::getDefault;

    private MarginCalculationParameters marginCalculationParameters;
    private ComputationManager computationManager;
    private ReportNode reportNode = ReportNode.NO_OP;

    /**
     * Returns a {@link MarginCalculationRunParameters} instance with default value on each field.
     * @return the MarginCalculationRunParameters instance.
     */
    public static MarginCalculationRunParameters getDefault() {
        return new MarginCalculationRunParameters()
                .setMarginCalculationParameters(DEFAULT_MC_PARAMETERS_SUPPLIER.get())
                .setComputationManager(DEFAULT_COMPUTATION_MANAGER_SUPPLIER.get());
    }

    /**
     * {@link MarginCalculationParameters} getter<br>
     * If null, sets the field to its default value with {@link #DEFAULT_MC_PARAMETERS_SUPPLIER} before returning it.
     */
    public MarginCalculationParameters getMarginCalculationParameters() {
        if (marginCalculationParameters == null) {
            setMarginCalculationParameters(DEFAULT_MC_PARAMETERS_SUPPLIER.get());
        }
        return marginCalculationParameters;
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
     * Sets the security analysis parameters, see {@link MarginCalculationRunParameters}.
     */
    public MarginCalculationRunParameters setMarginCalculationParameters(MarginCalculationParameters marginCalculationParameters) {
        Objects.requireNonNull(marginCalculationParameters, "Margin calculation parameters should not be null");
        this.marginCalculationParameters = marginCalculationParameters;
        return this;
    }

    /**
     * Sets the computationManager handling command execution.
     */
    public MarginCalculationRunParameters setComputationManager(ComputationManager computationManager) {
        Objects.requireNonNull(computationManager, "ComputationManager should not be null");
        this.computationManager = computationManager;
        return this;
    }

    /**
     * Sets the reportNode used for functional logs, see {@link ReportNode}
     */
    public MarginCalculationRunParameters setReportNode(ReportNode reportNode) {
        Objects.requireNonNull(reportNode, "ReportNode should not be null");
        this.reportNode = reportNode;
        return this;
    }
}
