/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssIEEE4B – IEEE Type PSS4B multi-band PSS. CIM: PssIEEE4B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record PssIEEE4B(
    String id, String excitationSystemId,
    double bwh1, double bwh2, double bwl1, double bwl2,
    double kh, double kh1, double kh11, double kh17, double kh2,
    double ki, double ki1, double ki11, double ki17, double ki2,
    double kl, double kl1, double kl11, double kl17, double kl2,
    double omeganh1, double omeganh2, double omeganl1, double omeganl2,
    double th1, double th10, double th11, double th12, double th2,
    double th3, double th4, double th5, double th6, double th7, double th8, double th9,
    double ti1, double ti10, double ti11, double ti12, double ti2,
    double ti3, double ti4, double ti5, double ti6, double ti7, double ti8, double ti9,
    double tl1, double tl10, double tl11, double tl12, double tl2,
    double tl3, double tl4, double tl5, double tl6, double tl7, double tl8, double tl9,
    double vsmax, double vsmin, double vshmax, double vshmin, double vsimax, double vsimin,
    double vslmax, double vslmin
) { }
