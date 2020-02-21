/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoInputsProviderFactory {

    DynawoInputsProvider create(Path dynawoFile);

    DynawoInputsProvider create(InputStream data);

}
