/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.parser;

/**
 * Unchecked exception thrown when a CGMES DY profile cannot be parsed.
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public class CgmesDyParseException extends RuntimeException {
    public CgmesDyParseException(String message) {
        super(message);
    }

    public CgmesDyParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
