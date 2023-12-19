/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.lines;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.lines.StandardLine;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LineBuilder extends AbstractEquipmentModelBuilder<Line, LineBuilder> {

    public static final String LIB = "Line";

    public LineBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.LINE, reporter);
    }

    public LineBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), IdentifiableType.LINE, reporter);
    }

    public LineBuilder(Network network) {
        super(network, new EquipmentConfig(LIB), IdentifiableType.LINE);
    }

    @Override
    protected Line findEquipment(String staticId) {
        return network.getLine(staticId);
    }

    @Override
    public StandardLine build() {
        return isInstantiable() ? new StandardLine(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LineBuilder self() {
        return this;
    }
}
