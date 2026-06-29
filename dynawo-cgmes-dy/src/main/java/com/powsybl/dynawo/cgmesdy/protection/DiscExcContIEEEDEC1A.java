/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.protection;
/** DiscExcContIEEEDEC1A – IEEE Discontinuous excitation control type DEC1A. CIM: DiscExcContIEEEDEC1A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record DiscExcContIEEEDEC1A(
    String id, String excitationSystemId,
    double vtlmt, double vomax, double vomin, double vdis,
    double vethr, double vanmax, double vtm, double vtn,
    double vsmin, double escrv, double kan, double td, double vsmax,
    double tan, double esc
) { }
