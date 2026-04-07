/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.dc;
/** ExcIEEEDC3A – IEEE Type DC3A non-continuously acting excitation system. CIM: ExcIEEEDC3A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEDC3A(
    String id, String synchronousMachineId,
    double trh, double kv, double vmax, double vmin,
    double ke, double te, double kf, double tf,
    double efd1, double seefd1, double efd2, double seefd2,
    boolean exclim
) { }
