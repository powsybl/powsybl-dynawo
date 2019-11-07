/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.powsybl.dynawo.crv.DynawoCurve;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.par.DynawoParameterSet;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoDb {

    private final List<DynawoJob> jobs = new ArrayList<>();
    private final List<DynawoCurve> curves = new ArrayList<>();
    private final List<DynawoDynamicModel> dynamicModels = new ArrayList<>();
    private final List<DynawoParameterSet> parameterSets = new ArrayList<>();
    private final List<DynawoParameterSet> solverParameterSets = new ArrayList<>();

    public void addJob(DynawoJob job) {
        Objects.requireNonNull(job);
        jobs.add(job);
    }

    public List<DynawoJob> getJobs() {
        return jobs;
    }

    public void addCurve(DynawoCurve curve) {
        Objects.requireNonNull(curve);
        curves.add(curve);
    }

    public List<DynawoCurve> getCurves() {
        return curves;
    }

    public void addDynamicModel(DynawoDynamicModel dynamicModel) {
        Objects.requireNonNull(dynamicModel);
        dynamicModels.add(dynamicModel);
    }

    public List<DynawoDynamicModel> getDynamicModels() {
        return dynamicModels;
    }

    public void addParameterSet(DynawoParameterSet parameterSet) {
        Objects.requireNonNull(parameterSet);
        parameterSets.add(parameterSet);
    }

    public List<DynawoParameterSet> getParameterSets() {
        return parameterSets;
    }

    public void addSolverParameterSet(DynawoParameterSet solverParameterSet) {
        Objects.requireNonNull(solverParameterSet);
        solverParameterSets.add(solverParameterSet);
    }

    public List<DynawoParameterSet> getSolverParameterSets() {
        return solverParameterSets;
    }

}
