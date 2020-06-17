/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class MacroConnector {

    public static class Connect {

        public Connect(String var1, String var2) {
            this.var1 = Objects.requireNonNull(var1);
            this.var2 = Objects.requireNonNull(var2);
        }

        public String getVar1() {
            return var1;
        }

        public String getVar2() {
            return var2;
        }

        private final String var1;
        private final String var2;
    }

    public MacroConnector(String id, Connect... connections) {
        this(id, Arrays.asList(connections));
    }

    public MacroConnector(String id, List<Connect> connections) {
        this.id = Objects.requireNonNull(id);
        this.connections = Objects.requireNonNull(connections);
    }

    public String getId() {
        return id;
    }

    public List<Connect> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    private final String id;
    private final List<Connect> connections;
}
