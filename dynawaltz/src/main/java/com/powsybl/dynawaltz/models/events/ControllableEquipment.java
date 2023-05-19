package com.powsybl.dynawaltz.models.events;

import com.powsybl.dynawaltz.models.Model;

public interface ControllableEquipment extends Model {

    String getDeltaPVarName();
}
