package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.models.Model;

public interface DisconnectableEquipment extends Model {

    String getDisconnectableVarName();
}
