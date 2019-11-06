/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DydParComponent extends DydComponent {

    private final String parametersFile;
    private final int parametersId;

    public DydParComponent(String id, String parametersFile, int parametersId) {
        super(id);
        this.parametersFile = parametersFile;
        this.parametersId = parametersId;
    }

    public String getParametersFile() {
        return parametersFile;
    }

    public int getParametersId() {
        return parametersId;
    }

}
