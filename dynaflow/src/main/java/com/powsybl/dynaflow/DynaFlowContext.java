/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;

import java.util.Objects;

/**
 * @author Guillem Jane <janeg at aia.es>
 */
public class DynaFlowContext {

    public DynaFlowContext(Network network, LoadFlowParameters loadFlowParameters, DynaFlowParameters dynaWaltzParameters, String workingStateId) {
        this.network = Objects.requireNonNull(network);
        this.loadFlowParameters = Objects.requireNonNull(loadFlowParameters);
        this.dynaFlowParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.workingStateId = Objects.requireNonNull(workingStateId);
    }

    public Network getNetwork() {
        return network;
    }

    public LoadFlowParameters getLoadFlowParameters() {
        return loadFlowParameters;
    }

    public DynaFlowParameters getDynaFlowParameters() {
        return dynaFlowParameters;
    }

    public String getWorkingStateId() {
        return workingStateId;
    }

    private final Network network;
    private final LoadFlowParameters loadFlowParameters;
    private final DynaFlowParameters dynaFlowParameters;
    private final String workingStateId;
}
