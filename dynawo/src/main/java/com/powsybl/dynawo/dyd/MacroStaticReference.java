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
public class MacroStaticReference extends DydComponent implements DynawoDynamicModel {

    private final List<StaticRef> staticRefs = new ArrayList<>();

    public MacroStaticReference(String id) {
        super(id);
    }

    public List<StaticRef> getStaticRefs() {
        return Collections.unmodifiableList(staticRefs);
    }

    public MacroStaticReference addStaticRefs(List<StaticRef> staticRef) {
        staticRefs.addAll(staticRef);
        return this;
    }

    public MacroStaticReference add(StaticRef staticRef) {
        Objects.requireNonNull(staticRef);
        staticRefs.add(staticRef);
        return this;
    }
}
