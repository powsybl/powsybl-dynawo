/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.dyd;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DydParComponent extends DydComponent {

    private final String parametersFile;
    private final String parametersId;

    public DydParComponent(String id, String parametersFile, String parametersId) {
        super(id);
        this.parametersFile = Objects.requireNonNull(parametersFile);
        this.parametersId = parametersId;
    }

    public String getParametersFile() {
        return parametersFile;
    }

    public String getParametersId() {
        return parametersId;
    }

}
