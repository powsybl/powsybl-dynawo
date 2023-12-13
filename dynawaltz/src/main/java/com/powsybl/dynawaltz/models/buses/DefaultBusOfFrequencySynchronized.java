package com.powsybl.dynawaltz.models.buses;

import com.powsybl.dynawaltz.models.defaultmodels.AbstractDefaultModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;

import java.util.List;

public class DefaultBusOfFrequencySynchronized extends AbstractDefaultModel implements BusOfFrequencySynchronizedModel {

    private final String frequencySynchronizedStaticId;

    public DefaultBusOfFrequencySynchronized(String staticId, String frequencySynchronizedStaticId) {
        super(staticId);
        this.frequencySynchronizedStaticId = frequencySynchronizedStaticId;
    }

    @Override
    public String getName() {
        return "DefaultBusOfFrequencySynchronized";
    }

    public String getNumCCVarName() {
        return "@@NAME@@@NODE@_numcc";
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(
                MacroConnectAttribute.of("id2", DynaWaltzXmlConstants.NETWORK),
                MacroConnectAttribute.of("name2", frequencySynchronizedStaticId)
        );
    }
}
