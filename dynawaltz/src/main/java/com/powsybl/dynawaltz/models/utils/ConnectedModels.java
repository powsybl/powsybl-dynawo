/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.utils;

import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.Model;

/**
 * Pair of models connected. One of them is necessarily a BlackBoxModel.
 * This class is used to consider only once this connection by overriding equals.
 *
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class ConnectedModels {

    private final BlackBoxModel blackBoxModel;
    private final Model model;

    private ConnectedModels(BlackBoxModel blackBoxModel, Model model) {
        this.blackBoxModel = blackBoxModel;
        this.model = model;
    }

    public static ConnectedModels of(BlackBoxModel bbm, Model m) {
        return new ConnectedModels(bbm, m);
    }

    public BlackBoxModel getBlackBoxModel() {
        return blackBoxModel;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public int hashCode() {
        return blackBoxModel.hashCode() + model.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectedModels) {
            ConnectedModels otherCm = (ConnectedModels) obj;
            return otherCm.blackBoxModel.equals(blackBoxModel) && otherCm.model.equals(model)
                    || otherCm.blackBoxModel.equals(model) && otherCm.model.equals(blackBoxModel);
        }
        return false;
    }
}
