/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoXmlConstants {

    public static final String DYN_BASE_URI = "http://www.rte-france.com/dynawo";

    public static final String DYN_PREFIX = "dyn";

    public static final String DYN_URI = DYN_BASE_URI;

    public static final String INDENT = "    ";

    public static final String NETWORK = "NETWORK";

    public static final String MACRO_STATIC_REFERENCE_PREFIX = "MSR_";
    public static final String MACRO_CONNECTOR_PREFIX = "MC_";
    public static final String MACRO_CONNECTOR_TO_GENERATOR_SUFIX = "ToGenerator";
    public static final String MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFIX = "ToNumCCMachine";

    private DynawoXmlConstants() {
    }
}
