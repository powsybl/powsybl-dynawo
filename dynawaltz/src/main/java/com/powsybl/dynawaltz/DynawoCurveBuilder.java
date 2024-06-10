/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawaltz.builders.BuilderReports;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoCurveBuilder {

    private static final String DEFAULT_DYNAMIC_MODEL_ID = "NETWORK";

    private final ReportNode reportNode;
    private boolean isInstantiable = true;
    private String dynamicModelId;
    private String staticId;
    private String variable;
    private String[] variables;

    public DynawoCurveBuilder(ReportNode reportNode) {
        this.reportNode = reportNode;
    }

    public DynawoCurveBuilder dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return this;
    }

    public DynawoCurveBuilder staticId(String staticId) {
        this.staticId = staticId;
        return this;
    }

    public DynawoCurveBuilder variables(String[] variables) {
        this.variables = variables;
        return this;
    }

    public DynawoCurveBuilder variable(String variable) {
        this.variable = variable;
        return this;
    }

    private String getId() {
        return dynamicModelId != null ? dynamicModelId : staticId;
    }

    private void checkData() {
        if (staticId != null && dynamicModelId != null) {
            BuilderReports.reportFieldConflict(reportNode, "dynamicModelId", "staticId");
        }
        if (variable == null) {
            BuilderReports.reportFieldNotSet(reportNode, "variable");
            isInstantiable = false;
        }
    }

    private boolean isInstantiable() {
        checkData();
        if (!isInstantiable) {
            BuilderReports.reportCurveInstantiationFailure(reportNode, getId());
        }
        return isInstantiable;
    }

    //TODO handle list
    public DynawoCurve build() {
        if (isInstantiable()) {
            return dynamicModelId != null ? new DynawoCurve(dynamicModelId, variable)
                    : new DynawoCurve(DEFAULT_DYNAMIC_MODEL_ID, staticId + "_" + variable);
        }
        return null;
    }
}
