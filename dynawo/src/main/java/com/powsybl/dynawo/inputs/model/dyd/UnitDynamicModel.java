/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.dyd;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class UnitDynamicModel extends DydParComponent {

    private final String name;
    private final String initName;
    private final String moFile;

    public UnitDynamicModel(String id, String name, String initName, String parametersFile, String parametersId) {
        this(id, name, null, initName, parametersFile, parametersId);
    }

    public UnitDynamicModel(String id, String name, String moFile, String initName, String parametersFile,
        String parametersId) {
        super(id, parametersFile, parametersId);
        this.name = name;
        this.initName = initName;
        this.moFile = moFile;
    }

    public String getName() {
        return name;
    }

    public String getInitName() {
        return initName;
    }

    public String getMoFile() {
        return moFile;
    }

}
