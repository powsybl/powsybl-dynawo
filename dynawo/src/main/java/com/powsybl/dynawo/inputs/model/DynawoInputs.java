/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model;

import java.util.List;

import com.powsybl.dynawo.inputs.model.crv.Curve;
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.par.ParameterSet;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoInputs {

    // FIXME Reconsider if we need here an interface
    // Why not use directly the object model with one implementation
    // Currently the class DynawoDb inside inputs.dsl package is a
    // complete object 

    Network getNetwork();

    List<Job> getJobs();

    List<Curve> getCurves();

    List<DynawoDynamicModel> getDynamicModels();

    List<ParameterSet> getParameterSets();

    List<ParameterSet> getSolverParameterSets();

}
