/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import com.powsybl.dynawo.crv.DynawoCurve;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.par.DynawoParameterSet;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoDslHandler {

    void addJob(DynawoJob job);

    void addCurve(DynawoCurve curve);

    void addDynamicModel(DynawoDynamicModel dynamicModel);

    void addParameterSet(DynawoParameterSet parameterSet);

    void addSolverParameterSet(DynawoParameterSet solverParameterSet);
}
