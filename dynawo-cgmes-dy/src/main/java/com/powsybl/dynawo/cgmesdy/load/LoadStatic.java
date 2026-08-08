/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.load;
/**
 * LoadStatic – ZIP load model (constant Z + I + P components).
 * CIM: LoadStatic
 * @param kp1-kp6  Active power polynomial coefficients
 * @param kq1-kq6  Reactive power polynomial coefficients
 * @param ep1-ep3  Active power voltage exponents
 * @param eq1-eq3  Reactive power voltage exponents
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record LoadStatic(
    String id, String energyConsumerId,
    double kp1, double kp2, double kp3, double kp4, double kp5, double kp6,
    double kq1, double kq2, double kq3, double kq4, double kq5, double kq6,
    double ep1, double ep2, double ep3, double eq1, double eq2, double eq3,
    String staticLoadModelType
) { }
