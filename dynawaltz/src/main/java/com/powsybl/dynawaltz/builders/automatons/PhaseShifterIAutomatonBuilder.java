package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton;
import com.powsybl.iidm.network.Network;

public class PhaseShifterIAutomatonBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterIAutomatonBuilder> {

    public static final String LIB = "PhaseShifterI";

    public PhaseShifterIAutomatonBuilder(Network network, Reporter reporter) {
        super(network, LIB, reporter);
    }

    @Override
    public PhaseShifterIAutomaton build() {
        return isInstantiable() ? new PhaseShifterIAutomaton(dynamicModelId, dslTransformer.getEquipment(), parameterSetId) : null;
    }

    @Override
    protected PhaseShifterIAutomatonBuilder self() {
        return this;
    }
}
