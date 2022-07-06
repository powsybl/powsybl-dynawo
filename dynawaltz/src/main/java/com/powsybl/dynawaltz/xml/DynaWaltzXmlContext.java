/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParametersDatabase;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzXmlContext {

    private final DynaWaltzContext context;

    private final String parFile;

    private final Map<String, AtomicInteger> counters = new HashMap<>();

    private final Map<String, BlackBoxModel> blackBoxModels;

    public DynaWaltzXmlContext(DynaWaltzContext context) {
        this.context = Objects.requireNonNull(context);
        this.parFile = Paths.get(context.getDynaWaltzParameters().getParametersFile()).getFileName().toString();
        this.blackBoxModels = context.getBlackBoxModelStream()
                .collect(Collectors.toMap(BlackBoxModel::getDynamicModelId, Function.identity(), (o1, o2) -> o1, LinkedHashMap::new));
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

    public Collection<BlackBoxModel> getBlackBoxModels() {
        return blackBoxModels.values();
    }

    public BlackBoxModel getBlackBoxModel(String dynamicModelId) {
        return blackBoxModels.get(dynamicModelId);
    }

    public DynaWaltzParametersDatabase getParametersDatabase() {
        return context.getParametersDatabase();
    }
}
