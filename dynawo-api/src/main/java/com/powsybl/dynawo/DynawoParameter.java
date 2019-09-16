/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

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

    public DynawoParameter(String name, String type, String value) {
        this.reference = false;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public DynawoParameter(String name, String type, String origData, String origName) {
        this.reference = true;
        this.name = name;
        this.type = type;
        this.origData = origData;
        this.origName = origName;
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

}
