/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.frequencysynchronizers;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.generators.OmegaRefGeneratorModel;

import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractFrequencySynchronizer extends AbstractPureDynamicBlackBoxModel implements FrequencySynchronizerModel {

    public static final String OMEGA_REF_ID = "OMEGA_REF";
    private static final String OMEGA_REF_PARAMETER_SET_ID = "OMEGA_REF";
    protected final List<OmegaRefGeneratorModel> omegaRefGenerators;

    protected AbstractFrequencySynchronizer(List<OmegaRefGeneratorModel> omegaRefGenerators) {
        super(OMEGA_REF_ID, OMEGA_REF_PARAMETER_SET_ID);
        this.omegaRefGenerators = omegaRefGenerators;
    }

    @Override
    public boolean isEmpty() {
        return omegaRefGenerators.isEmpty();
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }
}
