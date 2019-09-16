/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameterSet {

    private final int id;
    private final List<DynawoParameter> parameters;

    public DynawoParameterSet(int id, List<DynawoParameter> parameters) {
        this.id = id;
        this.parameters = new ArrayList<>(Objects.requireNonNull(parameters));
    }

    public DynawoParameterSet(int id, DynawoParameter... parameters) {
        this(id, Arrays.asList(parameters));
    }

    public int getId() {
        return id;
    }

    public List<DynawoParameter> getParameters() {
        return parameters;
    }
}
