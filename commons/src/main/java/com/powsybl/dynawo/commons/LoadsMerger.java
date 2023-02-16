package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.NetworkXml;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class LoadsMerger {

    private static final String MERGE_LOAD_PREFIX_ID = "merged_load_.";

    private LoadsMerger() {
    }

    public static Network mergeLoads(Network network) throws PowsyblException {
        Network mergedLoadsNetwork = NetworkXml.copy(network);
        for (Bus bus : mergedLoadsNetwork.getBusBreakerView().getBuses()) {
            if (bus.getLoadStream().count() > 1) {
                mergeLoads(bus);
            }
        }
        return mergedLoadsNetwork;
    }

    private static void mergeLoads(Bus bus) {
        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();

        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId());

        loadAdder.setLoadType(LoadType.UNDEFINED);

        BusState busState = getBusState(bus);

        loadAdder.setP0(busState.p0);
        loadAdder.setQ0(busState.q0);

        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        }

        Iterable<Load> loadsToMerge = bus.getLoads();
        if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            Load firstLoad = loadsToMerge.iterator().next();
            int node = firstLoad.getTerminal().getNodeBreakerView().getNode();
            loadAdder.setNode(node);
        }

        loadsToMerge.forEach(Connectable::remove);
        Load load = loadAdder.add();
        load.getTerminal().setP(busState.p);
        load.getTerminal().setQ(busState.q);
    }

    static final class BusState {
        private final double p;
        private final double q;
        private final double p0;
        private final double q0;

        private BusState(double p, double q, double p0, double q0) {
            this.p = p;
            this.q = q;
            this.p0 = p0;
            this.q0 = q0;
        }

        public double getP() {
            return p;
        }

        public double getQ() {
            return q;
        }
    }

    static BusState getBusState(Bus bus) {
        double p0 = 0;
        double q0 = 0;
        double p = 0;
        double q = 0;
        for (Load load : bus.getLoads()) {
            p0 += load.getP0();
            q0 += load.getQ0();
            p += load.getTerminal().getP();
            q += load.getTerminal().getQ();
        }
        return new BusState(p, q, p0, q0);
    }
}
