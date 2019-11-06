/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
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
public class ModelTemplate extends DydComponent implements DynawoDynamicModel {

    private List<UnitDynamicModel> unitDynamicModels;
    private List<Connection> connections;
    private List<InitConnection> initConnections;

    public ModelTemplate(String id) {
        super(id);
        this.unitDynamicModels = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.initConnections = new ArrayList<>();
    }

    public List<UnitDynamicModel> getUnitDynamicModels() {
        return unitDynamicModels;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public List<InitConnection> getInitConnections() {
        return initConnections;
    }

    public ModelTemplate addUnitDynamicModels(List<UnitDynamicModel> unitDynamicModel) {
        unitDynamicModels.addAll(unitDynamicModel);
        return this;
    }

    public ModelTemplate add(UnitDynamicModel unitDynamicModel) {
        unitDynamicModels.add(unitDynamicModel);
        return this;
    }

    public ModelTemplate addConnections(List<Connection> connection) {
        connections.addAll(connection);
        return this;
    }

    public ModelTemplate add(Connection connection) {
        connections.add(connection);
        return this;
    }

    public ModelTemplate addInitConnections(List<InitConnection> initConnection) {
        initConnections.addAll(initConnection);
        return this;
    }

    public ModelTemplate add(InitConnection initConnection) {
        initConnections.add(initConnection);
        return this;
    }

}
