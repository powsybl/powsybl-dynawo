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
public class DynawoOutputs {

    private final String directory;
    private final String curve;

    public DynawoOutputs(String directory, String curve) {
        this.directory = directory;
        this.curve = curve;
    }

    public String getDirectory() {
        return directory;
    }

    public String getCurve() {
        return curve;
    }

}
