/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynamicsimulation.OutputVariable;

import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
public class DynawoOutputVariable implements OutputVariable {

    private String dynamicModelId;
    private String variable;
    private final OutputType outputType;
    private boolean isDefault = false;
    private static final String DEFAULT_DYNAMIC_MODEL_ID = "NETWORK";

    DynawoOutputVariable(String dynamicModelId, String variable, OutputType outputType) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.variable = Objects.requireNonNull(variable);
        this.outputType = Objects.requireNonNull(outputType);
    }

    @Override
    public String getModelId() {
        if (isDefault) {
            return DEFAULT_DYNAMIC_MODEL_ID;
        }
        return dynamicModelId;
    }

    @Override
    public String getVariableName() {
        if (isDefault) {
            return dynamicModelId + "_" + variable;
        }
        return variable;
    }

    @Override
    public OutputType getOutputType() {
        return outputType;
    }

    public void isDefault() {
        isDefault = true;
    }
}
