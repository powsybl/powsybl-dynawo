package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.VarConnection;

import java.util.List;

public interface LoadWithTransformers extends Model {

    List<VarConnection> getTapChangerVarConnections(TransformerSide side);
}
