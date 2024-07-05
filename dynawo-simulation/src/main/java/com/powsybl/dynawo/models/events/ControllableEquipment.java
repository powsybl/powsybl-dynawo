package com.powsybl.dynawo.models.events;

import com.powsybl.dynawo.models.Model;

public interface ControllableEquipment extends Model {

    String getDeltaPVarName();
}
