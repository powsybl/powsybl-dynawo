package com.powsybl.dynawaltz.builders.hvdcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractHvdcBuilder<R extends AbstractEquipmentModelBuilder<HvdcLine, R>> extends AbstractEquipmentModelBuilder<HvdcLine, R> {

    protected TwoSides danglingSide;

    public AbstractHvdcBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.HVDC_LINE, reporter);
    }

    public AbstractHvdcBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.HVDC_LINE);
    }

    public R dangling(TwoSides danglingSide) {
        this.danglingSide = danglingSide;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        boolean isDangling = equipmentConfig.isDangling();
        if (isDangling && danglingSide == null) {
            Reporters.reportFieldNotSet(reporter, "dangling");
            isInstantiable = false;
        } else if (!isDangling && danglingSide != null) {
            Reporters.reportFieldSetWithWrongEquipment(reporter, "dangling", equipmentConfig.getLib());
            isInstantiable = false;
        }
    }

    @Override
    protected HvdcLine findEquipment(String staticId) {
        return network.getHvdcLine(staticId);
    }
}
