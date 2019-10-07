/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.List;

import com.powsybl.dynawo.crv.DynawoCurve;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoProvider {

    List<DynawoJob> getDynawoJobs(Network network);

    List<DynawoCurve> getDynawoCurves(Network network);

    List<DynawoDynamicModel> getDynawoDynamicModels(Network network);

    List<DynawoParameterSet> getDynawoParameterSets(Network network);

    List<DynawoParameterSet> getDynawoSolverParameterSets(Network network);

    default String asScript() {
        throw new UnsupportedOperationException(
            "Serialization not supported for dynawo provider of type " + this.getClass().getName());
    }

}
