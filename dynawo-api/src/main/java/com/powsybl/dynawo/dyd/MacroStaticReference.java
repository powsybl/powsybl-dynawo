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
public class MacroStaticReference extends DydComponent implements DynawoDynamicModel {

    private List<StaticRef> staticRefs;

    public MacroStaticReference(String id) {
        super(id);
        this.staticRefs = new ArrayList<>();
    }

    public List<StaticRef> getStaticRefs() {
        return staticRefs;
    }

    public MacroStaticReference addStaticRefs(List<StaticRef> staticRef) {
        staticRefs.addAll(staticRef);
        return this;
    }

    public MacroStaticReference add(StaticRef staticRef) {
        staticRefs.add(staticRef);
        return this;
    }
}
