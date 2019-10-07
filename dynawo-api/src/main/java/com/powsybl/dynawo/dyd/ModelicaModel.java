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
public class ModelicaModel extends DydComponent implements DynawoDynamicModel {

    private List<UnitDynamicModel> unitDynamicModels;
    private List<Connection> connections;
    private List<InitConnection> initConnections;

    private String staticId;
    private List<StaticRef> staticRefs;
    private List<DydComponent> macroStaticRefs;

    public ModelicaModel(String id) {
        this(id, null);
    }

    public ModelicaModel(String id, String staticId) {
        super(id);
        this.unitDynamicModels = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.initConnections = new ArrayList<>();

        this.staticId = staticId;
        this.staticRefs = new ArrayList<>();
        this.macroStaticRefs = new ArrayList<>();
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

    public String getStaticId() {
        return staticId;
    }

    public List<StaticRef> getStaticRefs() {
        return staticRefs;
    }

    public List<DydComponent> getMacroStaticRefs() {
        return macroStaticRefs;
    }

    public ModelicaModel addUnitDynamicModels(List<UnitDynamicModel> unitDynamicModel) {
        unitDynamicModels.addAll(unitDynamicModel);
        return this;
    }

    public ModelicaModel add(UnitDynamicModel unitDynamicModel) {
        unitDynamicModels.add(unitDynamicModel);
        return this;
    }

    public ModelicaModel addConnections(List<Connection> connection) {
        connections.addAll(connection);
        return this;
    }

    public ModelicaModel add(Connection connection) {
        connections.add(connection);
        return this;
    }

    public ModelicaModel addInitConnections(List<InitConnection> initConnection) {
        initConnections.addAll(initConnection);
        return this;
    }

    public ModelicaModel add(InitConnection initConnection) {
        initConnections.add(initConnection);
        return this;
    }

    public ModelicaModel addStaticRefs(List<StaticRef> staticRef) {
        staticRefs.addAll(staticRef);
        return this;
    }

    public ModelicaModel add(StaticRef staticRef) {
        staticRefs.add(staticRef);
        return this;
    }

    public ModelicaModel addMacroStaticRefs(List<DydComponent> macroStaticRef) {
        macroStaticRefs.addAll(macroStaticRef);
        return this;
    }

    public ModelicaModel add(DydComponent macroStaticRef) {
        macroStaticRefs.add(macroStaticRef);
        return this;
    }
}
