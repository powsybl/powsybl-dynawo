/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.powsybl.dynawo.inputs.model.job.Job;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoInputs {

    private final List<Job> jobs;

    public DynawoInputs() {
        jobs = new ArrayList<>();
    }

    public void addJob(Job job) {
        Objects.requireNonNull(job);
        jobs.add(job);
    }

    public List<Job> getJobs() {
        return Collections.unmodifiableList(jobs);
    }
}
