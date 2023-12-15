package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.AbstractDynamicModelBuilder;
import com.powsybl.dynawaltz.builders.ModelBuilder;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractAutomatonModelBuilder<T extends AbstractAutomatonModelBuilder<T>> extends AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected String dynamicModelId;
    protected String parameterSetId;
    protected final String lib;

    public AbstractAutomatonModelBuilder(Network network, String lib, Reporter reporter) {
        super(network, reporter);
        this.lib = lib;
    }

    public T dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId;
        return self();
    }

    public T parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
        return self();
    }

    @Override
    protected void checkData() {
        if (dynamicModelId == null) {
            Reporters.reportFieldNotSet(reporter, "dynamicModelId");
            isInstantiable = false;
        }

        if (parameterSetId == null) {
            Reporters.reportFieldNotSet(reporter, "dynamicModelId");
            isInstantiable = false;
        }

    }

    @Override
    public String getModelId() {
        return dynamicModelId != null ? dynamicModelId : "unknownDynamicId";
    }

    @Override
    public abstract DynamicModel build();

    protected abstract T self();
}
