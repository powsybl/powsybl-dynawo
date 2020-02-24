/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.powsybl.dynawo.inputs.model.crv.Curve;
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.par.ParameterSet;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoInputs {

    private final Network network;
    private final List<Job> jobs = new ArrayList<>();
    private final List<Curve> curves = new ArrayList<>();
    private final List<DynawoDynamicModel> dynamicModels = new ArrayList<>();
    private final List<ParameterSet> parameterSets = new ArrayList<>();
    private final List<ParameterSet> solverParameterSets = new ArrayList<>();

    public DynawoInputs(Network network) {
        this.network = Objects.requireNonNull(network);
    }

    public Network getNetwork() {
        return network;
    }

    public void addJob(Job job) {
        Objects.requireNonNull(job);
        jobs.add(job);
    }

    public List<Job> getJobs() {
        return Collections.unmodifiableList(jobs);
    }

    public void addCurve(Curve curve) {
        Objects.requireNonNull(curve);
        curves.add(curve);
    }

    public List<Curve> getCurves() {
        return Collections.unmodifiableList(curves);
    }

    public void addDynamicModel(DynawoDynamicModel dynamicModel) {
        Objects.requireNonNull(dynamicModel);
        dynamicModels.add(dynamicModel);
    }

    public List<DynawoDynamicModel> getDynamicModels() {
        return Collections.unmodifiableList(dynamicModels);
    }

    public void addParameterSet(ParameterSet parameterSet) {
        Objects.requireNonNull(parameterSet);
        parameterSets.add(parameterSet);
    }

    public List<ParameterSet> getParameterSets() {
        return Collections.unmodifiableList(parameterSets);
    }

    public void addSolverParameterSet(ParameterSet solverParameterSet) {
        Objects.requireNonNull(solverParameterSet);
        solverParameterSets.add(solverParameterSet);
    }

    public List<ParameterSet> getSolverParameterSets() {
        return Collections.unmodifiableList(solverParameterSets);
    }

}
