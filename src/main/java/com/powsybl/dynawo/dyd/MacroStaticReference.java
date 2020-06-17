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
public class MacroStaticReference {

    public static class StaticRef {

        public StaticRef(String var, String staticVar) {
            this.var = Objects.requireNonNull(var);
            this.staticVar = Objects.requireNonNull(staticVar);
        }

        public String getVar() {
            return var;
        }

        public String getStaticVar() {
            return staticVar;
        }

        private final String var;
        private final String staticVar;
    }

    public MacroStaticReference(String id, StaticRef... staticRefs) {
        this(id, Arrays.asList(staticRefs));
    }

    public MacroStaticReference(String id, List<StaticRef> staticRefs) {
        this.id = Objects.requireNonNull(id);
        this.staticRefs = Objects.requireNonNull(staticRefs);
    }

    public String getId() {
        return id;
    }

    public List<StaticRef> getStaticRefs() {
        return Collections.unmodifiableList(staticRefs);
    }

    private final String id;
    private final List<StaticRef> staticRefs;
}
