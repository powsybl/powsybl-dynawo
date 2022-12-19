/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.utils;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class ConnectedModelTypes {

    private final String modelNameA;
    private final String modelNameB;

    private ConnectedModelTypes(String modelNameA, String modelNameB) {
        this.modelNameA = modelNameA;
        this.modelNameB = modelNameB;
    }

    public static ConnectedModelTypes of(String a, String b) {
        return new ConnectedModelTypes(a, b);
    }

    @Override
    public int hashCode() {
        return modelNameA.hashCode() + modelNameB.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectedModelTypes) {
            ConnectedModelTypes otherCmc = (ConnectedModelTypes) obj;
            return otherCmc.modelNameA.equals(modelNameA) && otherCmc.modelNameB.equals(modelNameB)
                    || otherCmc.modelNameB.equals(modelNameA) && otherCmc.modelNameA.equals(modelNameB);
        }
        return false;
    }
}
