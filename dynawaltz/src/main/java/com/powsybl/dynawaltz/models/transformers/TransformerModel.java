/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.dynawaltz.models.automationsystems.BranchModel;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface TransformerModel extends BranchModel {

    String getStateValueVarName();

    String getStepVarName();

    String getIMonitoredVarName();

    String getPMonitoredVarName();

    String getDisableInternalTapChangerVarName();
}
