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
public class LoadAlphaBeta extends AbstractBlackBoxModel {

    public LoadAlphaBeta(String modelId, String staticId, String parameterSetId) {
        super(modelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadAlphaBeta";
    }

    @Override
    public List<MacroConnect> getMacroConnects() {
        return Collections.singletonList(new MacroConnect("LoadToNode", getId(), "NETWORK"));
    }

    @Override
    public List<String> getMacroStaticRefs() {
        return Collections.singletonList("Load");
    }

}
