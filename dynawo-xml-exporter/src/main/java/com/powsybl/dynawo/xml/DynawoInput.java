/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoInput {

    public static final String DYANWO_PAR = "dynawoModel.par";
    public static final String NETWORK = "NETWORK";
    public static final String OMEGA_REF = "OMEGA_REF";
    public static final String IIDM = "IIDM";
    public static final String BOOLEAN = "BOOL";
    public static final String DOUBLE = "DOUBLE";
    public static final String INT = "INT";

    private DynawoInput() {
        throw new IllegalStateException("Utility class");
    }

    public static String writeInputHeader() {
        return String.join(System.lineSeparator(),
            "<?xml version='1.0' encoding='UTF-8'?>",
            "<!--",
            "    Copyright (c) 2015-2019, RTE (http://www.rte-france.com)",
            "    See AUTHORS.txt",
            "    All rights reserved.",
            "    This Source Code Form is subject to the terms of the Mozilla Public",
            "    License, v. 2.0. If a copy of the MPL was not distributed with this",
            "    file, you can obtain one at http://mozilla.org/MPL/2.0/.",
            "    SPDX-License-Identifier: MPL-2.0",
            "",
            "    This file is part of Dynawo, an hybrid C++/Modelica open source time domain",
            "    simulation tool for power systems.",
            "-->");
    }
}
