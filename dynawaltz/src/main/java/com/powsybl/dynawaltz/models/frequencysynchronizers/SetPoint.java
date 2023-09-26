/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.frequencysynchronizers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.parameters.ParametersSet;

import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;

/**
 * Special generators' frequency synchronizer used when an Infinite Bus is present in the model.
 *
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SetPoint extends AbstractFrequencySynchronizer {

    public SetPoint(List<FrequencySynchronizedModel> synchronizedEquipments) {
        super(synchronizedEquipments);
    }

    @Override
    public String getLib() {
        return "SetPoint";
    }

    @Override
    public void createDynamicModelParameters(DynaWaltzContext context, Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        paramSet.addParameter("setPoint_Value0", DOUBLE, Double.toString(1));
        parametersAdder.accept(paramSet);
    }

    private List<VarConnection> getVarConnectionsWith(FrequencySynchronizedModel connected) {
        return connected.getSetPointVarConnections();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) throws PowsyblException {
        for (FrequencySynchronizedModel eq : synchronizedEquipments) {
            createMacroConnections(eq, getVarConnectionsWith(eq), context);
        }
    }
}
