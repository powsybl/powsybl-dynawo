/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class MacroConnector extends DydComponent implements DynawoDynamicModel {

    private List<DydConnection> connections;

    public MacroConnector(String id) {
        super(id);
        this.connections = new ArrayList<>();
    }

    public List<DydConnection> getConnections() {
        return connections;
    }

    public MacroConnector addConnections(List<DydConnection> connection) {
        connections.addAll(connection);
        return this;
    }

    public MacroConnector add(DydConnection connection) {
        connections.add(connection);
        return this;
    }
}
