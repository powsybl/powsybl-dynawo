package com.powsybl.dynawo.outputvariables;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public final class DynawoOutputVariableResolver {

    private final BlackBoxModelSupplier blackBoxModelSupplier;
    private static final String DEFAULT_DYNAMIC_MODEL_ID = "NETWORK";

    public DynawoOutputVariableResolver(Network network,
                                        BlackBoxModelSupplier blackBoxModelSupplier) {
        Objects.requireNonNull(network);
        this.blackBoxModelSupplier = Objects.requireNonNull(blackBoxModelSupplier);
    }

    public Map<OutputVariable.OutputType, List<OutputVariable>> resolveOutputVariables(
            Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables) {

        return outputVariables.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(ov -> ov instanceof DynawoOutputVariable
                                        ? resolveVariable((DynawoOutputVariable) ov)
                                        : null)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                ));
    }

    /**
     * Resolves a single output variable
     */
    private DynawoOutputVariable resolveVariable(DynawoOutputVariable dynawoOv) {
        String dynamicModelId = dynawoOv.getModelId();

        boolean isDynamic = blackBoxModelSupplier.hasDynamicModel(dynamicModelId);
        boolean isConnected = blackBoxModelSupplier.dynamicModelIsConnected(dynamicModelId);

        if (!isDynamic) {
            dynawoOv.setDynamicModelId(DEFAULT_DYNAMIC_MODEL_ID);
            dynawoOv.setVariable(dynamicModelId + "_" + dynawoOv.getVariableName());
        }

        return isConnected ? dynawoOv : null;
    }
}
