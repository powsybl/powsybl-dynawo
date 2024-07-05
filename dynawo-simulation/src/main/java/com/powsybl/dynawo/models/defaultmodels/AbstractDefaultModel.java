package com.powsybl.dynawo.models.defaultmodels;

import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.xml.DynawoSimulationXmlConstants;

import java.util.List;
import java.util.Objects;

public abstract class AbstractDefaultModel implements Model {

    private final String staticId;

    protected AbstractDefaultModel(String staticId) {
        this.staticId = Objects.requireNonNull(staticId);
    }

    public String getStaticId() {
        return staticId;
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(
                MacroConnectAttribute.of("id2", DynawoSimulationXmlConstants.NETWORK),
                MacroConnectAttribute.of("name2", staticId)
        );
    }
}
