/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameter {

    private final boolean reference;

    private final String name;
    private final String type;
    private String value;
    private String origName;
    private String origData;
    private String componentId;

    public DynawoParameter(String name, String type, String value) {
        this.reference = false;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }

    public DynawoParameter(String name, String type, String origData, String origName) {
        this(name, type, origData, origName, null);
    }

    public DynawoParameter(String name, String type, String origData, String origName, String componentId) {
        this.reference = true;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.origData = Objects.requireNonNull(origData);
        this.origName = Objects.requireNonNull(origName);
        this.componentId = componentId;
    }

    public boolean isReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getOrigName() {
        return origName;
    }

    public String getOrigData() {
        return origData;
    }

    public String getComponentId() {
        return componentId;
    }

}
