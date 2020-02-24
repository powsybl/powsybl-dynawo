/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl;

import java.io.InputStream;
import java.nio.file.Path;

import com.powsybl.dynawo.inputs.model.DynawoInputs;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GroovyDslDynawoInputProvider extends AbstractDslDynawoInputsProvider {

    public GroovyDslDynawoInputProvider(final Path path) {
        super(path);
    }

    public GroovyDslDynawoInputProvider(final InputStream input) {
        super(input);
    }

    @Override
    public DynawoInputs createDynawoInputs(Network network) {
        return new DynawoDslLoader(script).load(network);
    }

}
