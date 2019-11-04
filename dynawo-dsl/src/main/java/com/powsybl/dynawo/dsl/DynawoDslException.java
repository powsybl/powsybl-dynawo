/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import com.powsybl.commons.PowsyblException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoDslException extends PowsyblException {

    public DynawoDslException(String message) {
        super(message);
    }

    public DynawoDslException(String message, Throwable cause) {
        super(message, cause);
    }
}
