package com.powsybl.dynawaltz.builders.events;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.events.NodeFaultEvent;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

public class NodeFaultEventBuilder extends AbstractEventModelBuilder<Bus, NodeFaultEventBuilder> {

    public static final String TAG = "NodeFault";

    protected double faultTime;
    protected double rPu;
    protected double xPu;

    public NodeFaultEventBuilder(Network network, Reporter reporter) {
        super(network, new DslEquipment<Bus>(IdentifiableType.BUS), TAG, reporter);
    }

    public NodeFaultEventBuilder faultTime(double faultTime) {
        this.faultTime = faultTime;
        return self();
    }

    public NodeFaultEventBuilder rPu(double rPu) {
        this.rPu = rPu;
        return self();
    }

    public NodeFaultEventBuilder xPu(double xPu) {
        this.xPu = xPu;
        return self();
    }

    protected Bus findEquipment(String staticId) {
        return network.getBusBreakerView().getBus(staticId);
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (faultTime <= 0) {
            Reporters.reportCrossThreshold(reporter, "faultTime", faultTime, "strictly positive");
            isInstantiable = false;
        }
        if (rPu < 0) {
            Reporters.reportCrossThreshold(reporter, "rPu", rPu, "positive");
            isInstantiable = false;
        }
        if (xPu < 0) {
            Reporters.reportCrossThreshold(reporter, "xPu", xPu, "positive");
            isInstantiable = false;
        }
    }

    @Override
    public NodeFaultEvent build() {
        return isInstantiable() ? new NodeFaultEvent(dslEquipment.getEquipment(), startTime, faultTime, rPu, xPu) : null;
    }

    protected NodeFaultEventBuilder self() {
        return this;
    }
}
