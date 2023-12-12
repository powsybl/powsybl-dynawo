package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected final Network network;
    protected final Reporter reporter;
    protected boolean isInstantiable = true;

    protected AbstractDynamicModelBuilder(Network network, Reporter reporter) {
        this.network = network;
        this.reporter = reporter;
    }

    protected AbstractDynamicModelBuilder(Network network) {
        this.network = network;
        this.reporter = Reporter.NO_OP;
    }

    protected abstract void checkData();

    protected final boolean isInstantiable() {
        checkData();
        if (isInstantiable) {
            Reporters.reportModelInstantiation(reporter, getModelId());
        } else {
            Reporters.reportModelInstantiationFailure(reporter, getModelId());
        }
        return isInstantiable;
    }

    protected abstract String getModelId();
}
