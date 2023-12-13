package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractEquipmentModelBuilder<T extends Identifiable, R extends AbstractEquipmentModelBuilder<T, R>> extends AbstractDynamicModelBuilder {

    protected String dynamicModelId;
    protected String parameterSetId;
    protected final EquipmentConfig equipmentConfig;
    protected final DslEquipment<T> dslEquipment;

    protected AbstractEquipmentModelBuilder(Network network, EquipmentConfig equipmentConfig, IdentifiableType equipmentType, Reporter reporter) {
        super(network, reporter);
        this.equipmentConfig = equipmentConfig;
        this.dslEquipment = new DslEquipment<>(equipmentType);
    }

    protected AbstractEquipmentModelBuilder(Network network, EquipmentConfig equipmentConfig, IdentifiableType equipmentType) {
        this(network, equipmentConfig, equipmentType, Reporter.NO_OP);
    }

    public R staticId(String staticId) {
        dslEquipment.addEquipment(staticId, this::findEquipment);
        return self();
    }

    public R dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return self();
    }

    public R parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    @Override
    protected void checkData() {
        isInstantiable = dslEquipment.checkEquipmentData(reporter);
        if (parameterSetId == null) {
            Reporters.reportFieldNotSet(reporter, "parameterSetId");
            isInstantiable = false;
        }
        if (dynamicModelId == null) {
            Reporters.reportFieldReplacement(reporter, "dynamicModelId", "staticId", dslEquipment.hasStaticId() ? dslEquipment.getStaticId() : "(unknown staticId)");
            dynamicModelId = dslEquipment.getStaticId();
        }

    }

    protected abstract T findEquipment(String staticId);

    public T getEquipment() {
        return dslEquipment.getEquipment();
    }

    @Override
    public String getModelId() {
        return dynamicModelId != null ? dynamicModelId : "unknownDynamicId";
    }

    @Override
    public abstract DynamicModel build();

    protected abstract R self();
}
