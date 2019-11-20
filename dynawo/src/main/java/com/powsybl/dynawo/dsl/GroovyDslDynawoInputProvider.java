/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import com.powsybl.dynawo.crv.DynawoCurve;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GroovyDslDynawoInputProvider extends AbstractDslDynawoInputProvider {

    public GroovyDslDynawoInputProvider(final Path path) {
        super(path);
    }

    public GroovyDslDynawoInputProvider(InputStream input) {
        super(input);
    }

    @Override
    public List<DynawoJob> getDynawoJobs(Network network) {
        DynawoDb dynawoDb = new DynawoDslLoader(script).load(network);
        return dynawoDb.getJobs();
    }

    @Override
    public List<DynawoCurve> getDynawoCurves(Network network) {
        DynawoDb dynawoDb = new DynawoDslLoader(script).load(network);
        return dynawoDb.getCurves();
    }

    @Override
    public List<DynawoDynamicModel> getDynawoDynamicModels(Network network) {
        DynawoDb dynawoDb = new DynawoDslLoader(script).load(network);
        return dynawoDb.getDynamicModels();
    }

    @Override
    public List<DynawoParameterSet> getDynawoParameterSets(Network network) {
        DynawoDb dynawoDb = new DynawoDslLoader(script).load(network);
        return dynawoDb.getParameterSets();
    }

    @Override
    public List<DynawoParameterSet> getDynawoSolverParameterSets(Network network) {
        DynawoDb dynawoDb = new DynawoDslLoader(script).load(network);
        return dynawoDb.getSolverParameterSets();
    }
}
