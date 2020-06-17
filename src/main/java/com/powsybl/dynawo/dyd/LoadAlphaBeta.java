/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.Collections;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadAlphaBeta implements DynawoDynamicModel {

    public LoadAlphaBeta(String modelId, String staticId, String parameterSetId) {
        this.modelId = modelId;
        this.staticId = staticId;
        this.parameterSetId = parameterSetId;
    }

    @Override
    public String getId() {
        return modelId;
    }

    @Override
    public String getStaticId() {
        return staticId;
    }

    @Override
    public String getParameterSetId() {
        return parameterSetId;
    }

    @Override
    public String getLib() {
        return "LoadAlphaBeta";
    }

    @Override
    public List<MacroConnector> getMacroConnectors() {
        return Collections.singletonList(new MacroConnector("LoadToNode", modelId, "NETWORK"));
    }

    @Override
    public List<String> getMacroStaticReferencesId() {
        return Collections.singletonList("Load");
    }

    private final String modelId;
    private final String staticId;
    private final String parameterSetId;
}
