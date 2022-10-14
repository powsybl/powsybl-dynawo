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
import java.util.stream.Collectors;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzXmlContext {

    private final DynaWaltzContext context;

    private final String parFile;

    private final Map<String, AtomicInteger> counters = new HashMap<>();
    private final Map<BlackBoxModel, Integer> libIndexMap = new HashMap<>();

    private final Map<String, BlackBoxModel> blackBoxModelsMap;
    private final List<BlackBoxModel> blackBoxModels;

    public DynaWaltzXmlContext(DynaWaltzContext context) {
        this.context = Objects.requireNonNull(context);
        this.parFile = Paths.get(context.getDynaWaltzParameters().getParametersFile()).getFileName().toString();
        this.blackBoxModelsMap = context.getDynamicIdBlackBoxModelMap();
        this.blackBoxModels = context.getBlackBoxModels();
    }

    public String getParFile() {
        return parFile;
    }

    public String getSimulationParFile() {
        return context.getNetwork().getId() + ".par";
    }

    public int getLibIndex(BlackBoxModel bbm) {
        return getLibIndexMap().get(bbm);
    }

    private Map<BlackBoxModel, Integer> getLibIndexMap() {
        if (libIndexMap.isEmpty()) {
            blackBoxModels.forEach(bbm -> libIndexMap.put(bbm,
                    counters.computeIfAbsent(bbm.getLib(), k -> new AtomicInteger()).getAndIncrement()));
        }
        return libIndexMap;
    }

    public Collection<BlackBoxModel> getBlackBoxModels() {
        return blackBoxModels;
    }

    public BlackBoxModel getBlackBoxModel(String dynamicModelId) {
        return blackBoxModelsMap.get(dynamicModelId);
    }

    public DynaWaltzParametersDatabase getParametersDatabase() {
        return context.getParametersDatabase();
    }
}
