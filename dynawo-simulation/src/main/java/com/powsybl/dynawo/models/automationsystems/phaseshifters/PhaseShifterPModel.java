package com.powsybl.dynawo.models.automationsystems.phaseshifters;

import com.powsybl.dynawo.models.Model;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public interface PhaseShifterPModel extends Model {
    TwoWindingsTransformer getConnectedTransformer();

    String getLockedVarName();
}
