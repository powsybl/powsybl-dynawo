/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

/**
 * @param lib Lib name
 * @param alias Alias of the library name used in Powsybl-Dynawo
 * @param doc Definition of the event model
 * @param version Dynawo version range where the model can be used
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record EventModelInfo(String lib, String alias, String doc, VersionInterval version) implements ModelInfo {

    public EventModelInfo(String lib, String alias, String info) {
        this(lib, alias, info, VersionInterval.createDefaultVersion());
    }

    public EventModelInfo(String lib, String info) {
        this(lib, null, info, VersionInterval.createDefaultVersion());
    }

    @Override
    public String name() {
        return alias == null ? lib : alias;
    }

    /**
     * Concatenation of name, doc and version bound
     */
    public String formattedInfo() {
        return name() + (alias != null ? " (" + lib + ")" : "")
                + (doc != null ? ": " + doc : "")
                + " (" + version.formattedInfo() + ")";
    }
}
