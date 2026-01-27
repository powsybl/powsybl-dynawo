/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.outputvariables;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.builders.BuilderReports;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An output variable for <pre>Dynawo</pre> can be defined using {@code staticId} and {@code variable} or {@code dynamicModelId} and {@code variable}.
 * Definition with {@code staticId} and {@code variable} are used when no explicit dynamic component exists.
 * <pre>Dynawo</pre> expects {@code dynamicModelId} = “NETWORK” for these variables.
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoOutputVariablesBuilder {

    private static final String DEFAULT_DYNAMIC_MODEL_ID = "NETWORK";
    private static final String VARIABLES_FIELD = "variables";

    private final ReportNode reportNode;
    private boolean isInstantiable = true;
    private String id;
    @Deprecated
    private String dynamicModelId;
    @Deprecated
    private String staticId;
    private List<String> variables;
    private OutputVariable.OutputType type = OutputVariable.OutputType.CURVE;

    public DynawoOutputVariablesBuilder(ReportNode reportNode) {
        this.reportNode = reportNode;
    }

    public DynawoOutputVariablesBuilder() {
        this(ReportNode.NO_OP);
    }

    public DynawoOutputVariablesBuilder dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return this;
    }

    public DynawoOutputVariablesBuilder staticId(String staticId) {
        this.staticId = staticId;
        return this;
    }

    public DynawoOutputVariablesBuilder id(String id) {
        this.id = id;
        return this;
    }

    public DynawoOutputVariablesBuilder variables(String... variables) {
        this.variables = Stream.of(variables).filter(v -> !v.isEmpty()).toList();
        return this;
    }

    public DynawoOutputVariablesBuilder variables(List<String> variables) {
        this.variables = variables.stream().filter(v -> !v.isEmpty()).toList();
        return this;
    }

    public DynawoOutputVariablesBuilder variable(String variable) {
        if (!variable.isEmpty()) {
            this.variables = List.of(variable);
        } else {
            this.variables = Collections.emptyList();
        }
        return this;
    }

    public DynawoOutputVariablesBuilder outputType(OutputVariable.OutputType type) {
        this.type = type;
        return this;
    }

    private String getId() {
        if (id != null) {
            return id;
        }
        return dynamicModelId != null ? dynamicModelId : staticId;
    }

    private void checkData() {
        if (id == null) {
            if (staticId == null && dynamicModelId == null) {
                BuilderReports.reportFieldNotSet(reportNode, "id");
                isInstantiable = false;
            }
            if (staticId != null && dynamicModelId != null) {
                BuilderReports.reportFieldConflict(reportNode, "dynamicModelId", "staticId");
            }
        }

        if (variables == null) {
            BuilderReports.reportFieldNotSet(reportNode, VARIABLES_FIELD);
            isInstantiable = false;
        } else if (variables.isEmpty()) {
            BuilderReports.reportEmptyList(reportNode, VARIABLES_FIELD);
            isInstantiable = false;
        }
    }

    private boolean isInstantiable() {
        checkData();
        if (!isInstantiable) {
            BuilderReports.reportOutputVariableInstantiationFailure(reportNode, getId());
        }
        return isInstantiable;
    }

    public void add(Consumer<OutputVariable> outputVariableConsumer) {
        if (!isInstantiable()) {
            return;
        }

        if (id != null) {
            // Use the explicit id as-is, unresolved variables

            variables.forEach(v -> {
                DynawoOutputVariable ov = new DynawoOutputVariable(id, v, type);
                ov.isUnresolved = true;
                outputVariableConsumer.accept(ov);
            });
            return; // skip legacy path
        }

        // Legacy behavior (unchanged)
        boolean hasDynamicModelId = dynamicModelId != null;
        String resolvedId = hasDynamicModelId ? dynamicModelId : DEFAULT_DYNAMIC_MODEL_ID;

        variables.forEach(v ->
                outputVariableConsumer.accept(
                        new DynawoOutputVariable(
                                resolvedId,
                                hasDynamicModelId ? v : staticId + "_" + v,
                                type
                        )));
    }

    public List<OutputVariable> build() {
        List<OutputVariable> outputVariables = new ArrayList<>();
        add(outputVariables::add);
        return outputVariables;
    }
}
