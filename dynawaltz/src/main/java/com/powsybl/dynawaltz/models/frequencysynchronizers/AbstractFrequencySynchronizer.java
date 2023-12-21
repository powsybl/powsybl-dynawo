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

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractFrequencySynchronizer extends AbstractPureDynamicBlackBoxModel implements FrequencySynchronizerModel {

    private static final String FREQUENCY_SYNCHRONIZER_ID = "FREQ_SYNC";
    private static final String FREQUENCY_SYNCHRONIZER_PARAMETER_SET_ID = "FREQ_SYNC_PAR";
    protected final List<FrequencySynchronizedModel> synchronizedEquipments;

    protected AbstractFrequencySynchronizer(List<FrequencySynchronizedModel> synchronizedEquipments, String lib) {
        super(FREQUENCY_SYNCHRONIZER_ID, FREQUENCY_SYNCHRONIZER_PARAMETER_SET_ID, lib);
        this.synchronizedEquipments = synchronizedEquipments;
    }

    @Override
    public boolean isEmpty() {
        return synchronizedEquipments.isEmpty();
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }
}
