/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticid.staticref;

import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModelExtended;
import com.powsybl.dynawaltz.dynamicmodels.staticid.BlackBoxModelWithStaticId;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface BlackBoxModelWithStaticRef extends BlackBoxModelExtended, BlackBoxModelWithStaticId {

    List<Pair<String, String>> getStaticRef();
}
