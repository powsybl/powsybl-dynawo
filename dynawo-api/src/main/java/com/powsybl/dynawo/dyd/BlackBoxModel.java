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
public class BlackBoxModel extends DydParComponent implements DynawoDynamicModel {

    private final String lib;

    private String staticId;
    private List<StaticRef> staticRefs;
    private List<DydComponent> macroStaticRefs;

    public BlackBoxModel(String id, String lib, String parametersFile, int parametersId) {
        this(id, lib, parametersFile, parametersId, null);
    }

    public BlackBoxModel(String id, String lib, String parametersFile, int parametersId, String staticId) {
        super(id, parametersFile, parametersId);
        this.lib = lib;

        this.staticId = staticId;
        this.staticRefs = new ArrayList<>();
        this.macroStaticRefs = new ArrayList<>();
    }

    public String getLib() {
        return lib;
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

    public BlackBoxModel addStaticRefs(List<StaticRef> staticRef) {
        staticRefs.addAll(staticRef);
        return this;
    }

    public BlackBoxModel add(StaticRef staticRef) {
        staticRefs.add(staticRef);
        return this;
    }

    public BlackBoxModel addMacroStaticRefs(List<DydComponent> macroStaticRef) {
        macroStaticRefs.addAll(macroStaticRef);
        return this;
    }

    public BlackBoxModel add(DydComponent macroStaticRef) {
        macroStaticRefs.add(macroStaticRef);
        return this;
    }
}
