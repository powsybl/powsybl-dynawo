package com.powsybl.dynawaltz.builders.lines;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.lines.StandardLine;
import com.powsybl.dynawaltz.models.svarcs.BaseStaticVarCompensator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.StaticVarCompensator;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class LineBuilder extends AbstractEquipmentModelBuilder<Line, LineBuilder> {

    public static final String LIB = "Line";

    public LineBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.LINE, reporter);
    }

    public LineBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), IdentifiableType.LINE, reporter);
    }

    public LineBuilder(Network network) {
        super(network, new EquipmentConfig(LIB), IdentifiableType.LINE);
    }

    @Override
    protected Line findEquipment(String staticId) {
        return network.getLine(staticId);
    }

    @Override
    public StandardLine build() {
        return isInstantiable() ? new StandardLine(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LineBuilder self() {
        return this;
    }
}
