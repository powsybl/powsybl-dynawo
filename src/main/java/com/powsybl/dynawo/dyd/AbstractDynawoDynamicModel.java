/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.powsybl.dynamicsimulation.DynamicModel;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractDynawoDynamicModel implements DynamicModel {

    public enum DynamicModelType {
        BLACK_BOX_MODEL, MODELICA_MODEL;
    }

    public AbstractDynawoDynamicModel(String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public abstract DynamicModelType getType();

    public String getId() {
        return id;
    }

    // TODO Confirm:
    // It seems that all dynawo model types
    // (ModelicaModel, BlackBoxModel, ModelTemplate, ...)
    // may have macroStaticRefs, staticRefs, macroConnects and macroConnectors

    public List<String> getMacroStaticRefs() {
        return Collections.emptyList();
    }

    public List<MacroConnect> getMacroConnects() {
        return Collections.emptyList();
    }

    private final String id;
}
