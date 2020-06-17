/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.util.List;

import com.powsybl.dynamicsimulation.DynamicModel;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoDynamicModel extends DynamicModel {

    public abstract String getId();

    public abstract String getStaticId();

    public abstract String getParameterSetId();

    public abstract String getLib();

    public abstract List<MacroConnect> getMacroConnects();

    public abstract List<String> getMacroStaticRefs();

}
