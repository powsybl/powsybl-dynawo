/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dynamicmodels.AbstractBlackBoxModel;
import com.powsybl.dynawo.DynawoParametersDatabase;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoXmlContext {

    private final DynawoContext context;

    private final String parFile;

    private final Map<String, AtomicInteger> counters = new HashMap<>();

    private final Map<String, AbstractBlackBoxModel> blackBoxModels;

    public DynawoXmlContext(DynawoContext context) {
        this.context = Objects.requireNonNull(context);
        this.parFile = Paths.get(context.getDynawoParameters().getParametersFile()).getFileName().toString();
        this.blackBoxModels = context.getDynamicModels().stream()
                .filter(AbstractBlackBoxModel.class::isInstance)
                .map(AbstractBlackBoxModel.class::cast)
                .collect(Collectors.toMap(AbstractBlackBoxModel::getDynamicModelId, value -> value));
    }

    public String getParFile() {
        return parFile;
    }

    public String getSimulationParFile() {
        return context.getNetwork().getId() + ".par";
    }

    public int getIndex(String modelType, boolean increment) {
        AtomicInteger counter = counters.computeIfAbsent(modelType, k -> new AtomicInteger());
        return increment ? counter.getAndIncrement() : counter.get();
    }

    public Collection<AbstractBlackBoxModel> getBlackBoxModels() {
        return blackBoxModels.values();
    }

    public AbstractBlackBoxModel getBlackBoxModel(String dynamicModelId) {
        return blackBoxModels.get(dynamicModelId);
    }

    public DynawoParametersDatabase getParametersDatabase() {
        return context.getParametersDatabase();
    }
}
