/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class GeneratorFictitiousBuilder extends AbstractGeneratorBuilder<GeneratorFictitiousBuilder> {

    //TODO voir ou mettre la lib en static (utilisé dans le groovy extension; le builder et la classe finale) -> a mettre dnas la classe ?
    // ou garder dans les builder avec la modif des constructeurs derrière et passer tous les constructeur en protected

    public GeneratorFictitiousBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public GeneratorFictitious build() {
        return isInstantiable() ? new GeneratorFictitious(dynamicModelId, getEquipment(), parameterSetId, modelConfig.getLib()) : null;
    }

    @Override
    protected GeneratorFictitiousBuilder self() {
        return this;
    }
}
