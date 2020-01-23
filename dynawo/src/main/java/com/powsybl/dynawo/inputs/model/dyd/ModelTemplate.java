/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.dyd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ModelTemplate extends DydComponent implements DynawoDynamicModel {

    private final List<UnitDynamicModel> unitDynamicModels = new ArrayList<>();
    private final List<Connection> connections = new ArrayList<>();
    private final List<InitConnection> initConnections = new ArrayList<>();

    public ModelTemplate(String id) {
        super(id);
    }

    public List<UnitDynamicModel> getUnitDynamicModels() {
        return Collections.unmodifiableList(unitDynamicModels);
    }

    public List<Connection> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    public List<InitConnection> getInitConnections() {
        return Collections.unmodifiableList(initConnections);
    }

    public ModelTemplate addUnitDynamicModels(List<UnitDynamicModel> unitDynamicModel) {
        unitDynamicModels.addAll(unitDynamicModel);
        return this;
    }

    public ModelTemplate add(UnitDynamicModel unitDynamicModel) {
        Objects.requireNonNull(unitDynamicModel);
        unitDynamicModels.add(unitDynamicModel);
        return this;
    }

    public ModelTemplate addConnections(List<Connection> connection) {
        connections.addAll(connection);
        return this;
    }

    public ModelTemplate add(Connection connection) {
        Objects.requireNonNull(connection);
        connections.add(connection);
        return this;
    }

    public ModelTemplate addInitConnections(List<InitConnection> initConnection) {
        initConnections.addAll(initConnection);
        return this;
    }

    public ModelTemplate add(InitConnection initConnection) {
        Objects.requireNonNull(initConnection);
        initConnections.add(initConnection);
        return this;
    }
}
