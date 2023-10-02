package com.powsybl.dynawaltz.models.defaultmodels;

import com.powsybl.dynawaltz.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;

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
                MacroConnectAttribute.of("id2", DynaWaltzXmlConstants.NETWORK),
                MacroConnectAttribute.of("name2", staticId)
        );
    }
}
