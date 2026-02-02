/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public final class DynawoOutputVariableResolver {

    private final BlackBoxModelSupplier blackBoxModelSupplier;

    public DynawoOutputVariableResolver(Network network,
                                        BlackBoxModelSupplier blackBoxModelSupplier) {
        Objects.requireNonNull(network);
        this.blackBoxModelSupplier = Objects.requireNonNull(blackBoxModelSupplier);
    }

    public Map<OutputVariable.OutputType, List<OutputVariable>> resolveOutputVariables(
            Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables) {

        outputVariables.values().forEach(list -> {
            list.replaceAll(ov -> {
                if (ov instanceof DynawoOutputVariable dynawoOv) {
                    return resolveVariable(dynawoOv);
                }
                return ov;
            });
            list.removeIf(Objects::isNull);
        });

        return outputVariables;
    }

    /**
     * Resolves a single output variable
     */
    private DynawoOutputVariable resolveVariable(DynawoOutputVariable dynawoOv) {
        BlackBoxModel model = blackBoxModelSupplier.getDynamicModel(dynawoOv.getModelId());

        if (model == null) {
            dynawoOv.setDefault();
            return dynawoOv;
        }

        return model.isConnected() ? dynawoOv : null;
    }
}
