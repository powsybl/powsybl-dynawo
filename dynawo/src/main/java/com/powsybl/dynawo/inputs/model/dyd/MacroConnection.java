/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.dyd;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class MacroConnection implements DynawoDynamicModel {

    private final String connector;
    private final String id1;
    private final String id2;

    public MacroConnection(String connector, String id1, String id2) {
        this.connector = Objects.requireNonNull(connector);
        this.id1 = Objects.requireNonNull(id1);
        this.id2 = Objects.requireNonNull(id2);
    }

    public String getConnector() {
        return connector;
    }

    public String getId1() {
        return id1;
    }

    public String getId2() {
        return id2;
    }

    @Override
    public String getId() {
        return null;
    }

}
