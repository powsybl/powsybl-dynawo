/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.parameters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class Set {

    private final Map<String, Parameter> parameters = new LinkedHashMap<>();

    private final List<Reference> references = new ArrayList<>();

    public void addParameter(String name, ParameterType type, String value) {
        parameters.put(name, new Parameter(name, type, value));
    }

    public void addReference(String name, ParameterType type, String origData, String origName) {
        references.add(new Reference(name, type, origData, origName));
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public Parameter getParameter(String name) {
        return parameters.get(name);
    }
}
