package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.NetworkXml;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class LoadsMerger {

    private static final String MERGE_LOAD_PREFIX_ID = "merged_load_.";

    private LoadsMerger() {
    }

    public static Network mergeLoads(Network network) throws PowsyblException {
        Network mergedLoadsNetwork = NetworkXml.copy(network);

        List<LoadsMerging> loadsMerging = mergedLoadsNetwork.getBusBreakerView().getBusStream()
                .filter(bus -> bus.getLoadStream().count() > 1)
                .map(LoadsMerger::mergeLoads)
                .collect(Collectors.toList());

        for (LoadsMerging merging : loadsMerging) {
            merging.loadsToMerge.forEach(Connectable::remove);
            merging.loadAdder.setP0(merging.busState.p0);
            merging.loadAdder.setQ0(merging.busState.q0);
            Load load = merging.loadAdder.add();
            load.getTerminal().setP(merging.busState.p);
            load.getTerminal().setQ(merging.busState.q);
        }
        return mergedLoadsNetwork;
    }

    private static LoadsMerging mergeLoads(Bus bus) {
        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();

        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId());

        loadAdder.setLoadType(LoadType.UNDEFINED);

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

        return new LoadsMerging(loadAdder, loadsToMerge, getBusState(bus));
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

    private static class LoadsMerging {
        private final BusState busState;
        private final LoadAdder loadAdder;
        private final Iterable<Load> loadsToMerge;

        public LoadsMerging(LoadAdder loadAdder, Iterable<Load> loadsToMerge, BusState busState) {
            this.loadAdder = loadAdder;
            this.loadsToMerge = loadsToMerge;
            this.busState = busState;
        }
    }
}
