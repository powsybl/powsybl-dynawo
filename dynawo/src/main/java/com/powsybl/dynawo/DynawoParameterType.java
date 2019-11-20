/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public enum DynawoParameterType {

    DYNAWO_PAR("dynawoModel.par"),
    NETWORK("NETWORK"),
    OMEGA_REF("OMEGA_REF"),
    IIDM("IIDM"),
    BOOLEAN("BOOL"),
    DOUBLE("DOUBLE"),
    INT("INT");

    private String value;

    DynawoParameterType(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
