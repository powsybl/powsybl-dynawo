/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ModelicaModel extends DydComponent implements DynawoDynamicModel {

    private final List<UnitDynamicModel> unitDynamicModels = new ArrayList<>();
    private final List<Connection> connections = new ArrayList<>();
    private final List<InitConnection> initConnections = new ArrayList<>();

    private final String staticId;
    private final List<StaticRef> staticRefs = new ArrayList<>();
    private final List<DydComponent> macroStaticRefs = new ArrayList<>();

    public ModelicaModel(String id) {
        this(id, null);
    }

    public ModelicaModel(String id, String staticId) {
        super(id);
        this.staticId = staticId;
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

    public String getStaticId() {
        return staticId;
    }

    public List<StaticRef> getStaticRefs() {
        return Collections.unmodifiableList(staticRefs);
    }

    public List<DydComponent> getMacroStaticRefs() {
        return Collections.unmodifiableList(macroStaticRefs);
    }

    public ModelicaModel addUnitDynamicModels(List<UnitDynamicModel> unitDynamicModel) {
        unitDynamicModels.addAll(unitDynamicModel);
        return this;
    }

    public ModelicaModel add(UnitDynamicModel unitDynamicModel) {
        Objects.requireNonNull(unitDynamicModel);
        unitDynamicModels.add(unitDynamicModel);
        return this;
    }

    public ModelicaModel addConnections(List<Connection> connection) {
        connections.addAll(connection);
        return this;
    }

    public ModelicaModel add(Connection connection) {
        Objects.requireNonNull(connection);
        connections.add(connection);
        return this;
    }

    public ModelicaModel addInitConnections(List<InitConnection> initConnection) {
        initConnections.addAll(initConnection);
        return this;
    }

    public ModelicaModel add(InitConnection initConnection) {
        Objects.requireNonNull(initConnection);
        initConnections.add(initConnection);
        return this;
    }

    public ModelicaModel addStaticRefs(List<StaticRef> staticRef) {
        staticRefs.addAll(staticRef);
        return this;
    }

    public ModelicaModel add(StaticRef staticRef) {
        Objects.requireNonNull(staticRef);
        staticRefs.add(staticRef);
        return this;
    }

    public ModelicaModel addMacroStaticRefs(List<DydComponent> macroStaticRef) {
        macroStaticRefs.addAll(macroStaticRef);
        return this;
    }

    public ModelicaModel add(DydComponent macroStaticRef) {
        Objects.requireNonNull(macroStaticRef);
        macroStaticRefs.add(macroStaticRef);
        return this;
    }
}
