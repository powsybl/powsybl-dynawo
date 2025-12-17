/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.generator;

import com.powsybl.commons.extensions.Extension;
import com.powsybl.iidm.network.Generator;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public interface SynchronousGeneratorProperties extends Extension<Generator> {

    String NAME = "synchronousGeneratorProperties";

    enum Windings {
        THREE_WINDINGS,
        FOUR_WINDINGS
    }

    enum Uva {
        LOCAL,
        DISTANT
    }

    @Override
    default String getName() {
        return NAME;
    }

    Windings getNumberOfWindings();

    void setNumberOfWindings(Windings numberOfWindings);

    String getGovernor();

    void setGovernor(String governor);

    String getVoltageRegulator();

    void setVoltageRegulator(String voltageRegulator);

    String getPss();

    void setPss(String pss);

    boolean isAuxiliaries();

    void setAuxiliaries(boolean auxiliaries);

    boolean isInternalTransformer();

    void setInternalTransformer(boolean internalTransformer);

    boolean isRpcl1();

    boolean isRpcl2();

    RpclType getRpcl();

    void setRpcl(RpclType rpcl);

    Uva getUva();

    void setUva(Uva uva);

    boolean isAggregated();

    void setAggregated(boolean aggregated);

    boolean isQlim();

    void setQlim(boolean qlim);

}
