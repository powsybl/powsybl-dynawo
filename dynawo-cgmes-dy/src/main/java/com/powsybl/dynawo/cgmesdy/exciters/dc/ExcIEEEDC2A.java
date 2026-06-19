/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.dc;
/** ExcIEEEDC2A – IEEE Type DC2A separately excited DC excitation system. CIM: ExcIEEEDC2A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEDC2A(
    String id, String synchronousMachineId,
    double tr, double ka, double ta, double tb, double tc,
    double vrmax, double vrmin, double ke, double te,
    double kf, double tf, double kc, double kd, double ki,
    double efd1, double seefd1, double efd2, double seefd2,
    boolean uelin, boolean exclim
) { }
