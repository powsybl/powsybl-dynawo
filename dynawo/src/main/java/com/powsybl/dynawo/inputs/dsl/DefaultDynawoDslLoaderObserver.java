/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DefaultDynawoDslLoaderObserver implements DynawoDslLoaderObserver {

    @Override
    public void begin(String dslFile) {
        // empty default implementation
    }

    @Override
    public void jobFound(String jobId) {
        // empty default implementation
    }

    @Override
    public void curveFound(String curveId) {
        // empty default implementation
    }

    @Override
    public void dynamicModelFound(String dynamicModelId) {
        // empty default implementation
    }

    @Override
    public void parameterSetFound(String parameterSetId) {
        // empty default implementation
    }

    @Override
    public void solverParameterSetFound(String solverParameterSetId) {
        // empty default implementation
    }

    @Override
    public void end() {
        // empty default implementation
    }

}
