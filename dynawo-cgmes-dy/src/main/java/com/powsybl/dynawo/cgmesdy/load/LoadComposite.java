/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.load;
/** LoadComposite – Composite load (induction motor + static). CIM: LoadComposite
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record LoadComposite(
    String id, String energyConsumerId,
    double epvs, double epfs, double eqvs, double eqfs,
    double epvd, double epfd, double eqvd, double eqfd,
    double mv, double mf, double lfmac, double lfs, double lfrac, double pfrac,
    double td, double tf, double tc,
    double xm, double xp, double xpp, double ls, double ra, double tpo, double tppo
) { }
