/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dyd.AbstractBlackBoxModel;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoXmlContext {

    private final String parFile;

    private final Map<String, AtomicInteger> counters = new HashMap<>();

    private final Map<String, DynamicModel> dynamicModels = new HashMap<>();

    public DynawoXmlContext(DynawoContext context) {
        Objects.requireNonNull(context);
        this.parFile = Paths.get(context.getDynawoParameters().getParametersFile()).getFileName().toString();
        context.getDynamicModels().forEach(dynamicModel -> {
            if (!(dynamicModel instanceof AbstractBlackBoxModel)) {
                return;
            }
            AbstractBlackBoxModel bbm = (AbstractBlackBoxModel) dynamicModel;
            this.dynamicModels.put(bbm.getId(), bbm);
        });
    }

    public String getParFile() {
        return parFile;
    }

    public int getIndex(String modelType, boolean increment) {
        AtomicInteger counter = counters.computeIfAbsent(modelType, k -> new AtomicInteger());
        return increment ? counter.getAndIncrement() : counter.get();
    }

    public DynamicModel getDynamicModel(String dynamicModelId) {
        return dynamicModels.get(dynamicModelId);
    }
}
