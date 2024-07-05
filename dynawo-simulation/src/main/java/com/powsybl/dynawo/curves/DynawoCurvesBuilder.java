/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.curves;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawo.builders.BuilderReports;

import java.util.*;
import java.util.function.Consumer;

/**
 * A curve for <pre>Dynawo</pre> can be defined using {@code staticId} and {@code variable} or {@code dynamicModelId} and {@code variable}.
 * Definition with {@code staticId} and {@code variable} are used when no explicit dynamic component exists.
 * <pre>Dynawo</pre> expects {@code dynamicModelId} = “NETWORK” for these variables.
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoCurvesBuilder {

    private static final String DEFAULT_DYNAMIC_MODEL_ID = "NETWORK";

    private final ReportNode reportNode;
    private boolean isInstantiable = true;
    private String dynamicModelId;
    private String staticId;
    private List<String> variables;

    public DynawoCurvesBuilder(ReportNode reportNode) {
        this.reportNode = reportNode;
    }

    public DynawoCurvesBuilder() {
        this(ReportNode.NO_OP);
    }

    public DynawoCurvesBuilder dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return this;
    }

    public DynawoCurvesBuilder staticId(String staticId) {
        this.staticId = staticId;
        return this;
    }

    public DynawoCurvesBuilder variables(String... variables) {
        this.variables = List.of(variables);
        return this;
    }

    public DynawoCurvesBuilder variables(List<String> variables) {
        this.variables = variables;
        return this;
    }

    public DynawoCurvesBuilder variable(String variable) {
        this.variables = List.of(variable);
        return this;
    }

    private String getId() {
        return dynamicModelId != null ? dynamicModelId : staticId;
    }

    private void checkData() {
        if (staticId != null && dynamicModelId != null) {
            BuilderReports.reportFieldConflict(reportNode, "dynamicModelId", "staticId");
        }
        if (variables == null) {
            BuilderReports.reportFieldNotSet(reportNode, "variables");
            isInstantiable = false;
        } else if (variables.isEmpty()) {
            BuilderReports.reportEmptyList(reportNode, "variables");
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

    public void add(Consumer<Curve> curveConsumer) {
        if (isInstantiable()) {
            boolean hasDynamicModelId = dynamicModelId != null;
            String id = hasDynamicModelId ? dynamicModelId : DEFAULT_DYNAMIC_MODEL_ID;
            variables.forEach(v -> curveConsumer.accept(new DynawoCurve(id, hasDynamicModelId ? v : staticId + "_" + v)));
        }
    }

    public List<Curve> build() {
        List<Curve> curves = new ArrayList<>();
        add(curves::add);
        return curves;
    }
}
