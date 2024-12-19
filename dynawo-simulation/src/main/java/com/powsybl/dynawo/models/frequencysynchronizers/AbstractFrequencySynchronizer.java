/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.frequencysynchronizers;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractFrequencySynchronizer extends AbstractPureDynamicBlackBoxModel implements FrequencySynchronizerModel {

    private static final String FREQUENCY_SYNCHRONIZER_ID = "FREQ_SYNC";
    private static final String FREQUENCY_SYNCHRONIZER_PARAMETER_SET_ID = "FREQ_SYNC_PAR";
    protected final List<FrequencySynchronizedModel> synchronizedEquipments;
    private final String defaultParFile;

    protected AbstractFrequencySynchronizer(List<FrequencySynchronizedModel> synchronizedEquipments, ModelConfig modelConfig, String defaultParFile) {
        super(FREQUENCY_SYNCHRONIZER_ID, FREQUENCY_SYNCHRONIZER_PARAMETER_SET_ID, modelConfig);
        this.synchronizedEquipments = synchronizedEquipments;
        this.defaultParFile = defaultParFile;
    }

    @Override
    public boolean isEmpty() {
        return synchronizedEquipments.isEmpty();
    }

    @Override
    public String getDefaultParFile() {
        return defaultParFile;
    }
}
