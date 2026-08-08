/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;
/** WindContRotorRIEC – Rotor resistance controller. CIM: WindContRotorRIEC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record WindContRotorRIEC(
    String id,
    double kirr, double komegafilt, double kpfilt, double kprr,
    double rmax, double rmin, double tomegafilt, double tpfilt
) { }
