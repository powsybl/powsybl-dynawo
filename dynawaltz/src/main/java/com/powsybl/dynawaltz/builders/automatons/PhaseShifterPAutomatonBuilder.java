package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomaton;
import com.powsybl.iidm.network.Network;

public class PhaseShifterPAutomatonBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterPAutomatonBuilder> {

    public static final String LIB = "PhaseShifterP";

    public PhaseShifterPAutomatonBuilder(Network network, Reporter reporter) {
        super(network, LIB, reporter);
    }

    @Override
    public PhaseShifterPAutomaton build() {
        return isInstantiable() ? new PhaseShifterPAutomaton(dynamicModelId, dslTransformer.getEquipment(), parameterSetId) : null;
    }

    @Override
    protected PhaseShifterPAutomatonBuilder self() {
        return this;
    }
}
