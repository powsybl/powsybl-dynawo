/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.DynaWaltzContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface Model extends DynamicModel {
    String getStaticId();

    String getLib();

    List<Pair<String, String>> getAttributesConnectTo();
}
