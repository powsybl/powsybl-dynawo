/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ModelInfo {

    /**
     * Returns the model name, lib if <code>alias</code> is null <code>lib</code> otherwise
     */
    String name();

    /**
     * Returns the library name used in Dynawo
     */
    String lib();

    /**
     * Returns the alias of the library name used in Powsybl-Dynawo instead of <code>lib</code> (or <code>null</code> if there is no alias)
     */
    String alias();

    /**
     * Returns a definition of the model
     */
    String doc();

    /**
     * Concatenation of lib, alias and doc field
     */
    String formattedInfo();

    /**
     * Dynawo version range where the model can be used
     */
    VersionInterval version();
}
