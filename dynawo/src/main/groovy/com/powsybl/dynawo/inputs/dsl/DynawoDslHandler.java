/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl;

import com.powsybl.dynawo.inputs.model.crv.Curve;
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.par.ParameterSet;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoDslHandler {

    void addJob(Job job);

    void addCurve(Curve curve);

    void addDynamicModel(DynawoDynamicModel dynamicModel);

    void addParameterSet(ParameterSet parameterSet);

    void addSolverParameterSet(ParameterSet solverParameterSet);
}
