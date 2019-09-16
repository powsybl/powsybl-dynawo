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
public class DynawoModeler {

    private final String compile;
    private final String iidm;
    private final String parameters;
    private final int parameterId;
    private final String dyd;

    public DynawoModeler(String compile, String iidm, String parameters, int parameterId, String dyd) {
        this.compile = compile;
        this.iidm = iidm;
        this.parameters = parameters;
        this.parameterId = parameterId;
        this.dyd = dyd;
    }

    public String getCompile() {
        return compile;
    }

    public String getIidm() {
        return iidm;
    }

    public String getParameters() {
        return parameters;
    }

    public int getParameterId() {
        return parameterId;
    }

    public String getDyd() {
        return dyd;
    }

}
