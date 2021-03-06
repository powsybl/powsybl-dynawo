/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GeneratorSynchronousFourWindingsProportionalRegulations extends AbstractGeneratorModel {

    public GeneratorSynchronousFourWindingsProportionalRegulations(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "GeneratorSynchronousFourWindingsProportionalRegulations";
    }
}
