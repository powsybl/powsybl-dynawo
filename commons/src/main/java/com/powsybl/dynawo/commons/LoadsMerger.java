package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.NetworkXml;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class LoadsMerger {

    private static final String MERGE_LOAD_PREFIX_ID = "merged_load_.";
    private final Network initialNetwork;
    private final Network mergedLoadsNetwork;

    public LoadsMerger(Network network) {
        this.initialNetwork = network;
        this.mergedLoadsNetwork = NetworkXml.copy(network);
    }

    public Network getMergedLoadsNetwork() {
        return mergedLoadsNetwork;
    }

    public void mergeLoads() throws PowsyblException {
        for (Bus bus : mergedLoadsNetwork.getBusBreakerView().getBuses()) {
            if (bus.getLoadStream().count() > 1) {
                mergeLoads(bus);
            }
        }
    }

    public void unmergeLoads() throws PowsyblException {
        for (Bus bus0 : initialNetwork.getBusBreakerView().getBuses()) {
            if (bus0.getLoadStream().count() > 1) {
                Bus bus1 = mergedLoadsNetwork.getBusBreakerView().getBus(bus0.getId());
                if (bus1.getLoadStream().count() == 1) {
                    recreateInitialLoads(bus0, bus1);
                }
            }
        }
    }

    private void recreateInitialLoads(Bus bus0, Bus bus1) {
        Load mergedLoad = bus1.getLoadStream().findFirst()
                .orElseThrow(() -> new PowsyblException("Missing merged load in bus " + bus1.getId()));

        BusState busState = getBusState(bus0);

        LoadAdder loadAdderUsedToReplaceMergeLoad = null;
        Load loadReplacingTheMergedLoad = null;
        for (Load oldLoad : bus0.getLoads()) {
            LoadAdder mergedLoadNotReplaced = createNewLoad(bus1, mergedLoad, oldLoad, busState);
            if (mergedLoadNotReplaced != null) {
                loadAdderUsedToReplaceMergeLoad = mergedLoadNotReplaced;
                loadReplacingTheMergedLoad = oldLoad;
            }
        }

        double qMergedLoad = mergedLoad.getTerminal().getQ();
        double pMergedLoad = mergedLoad.getTerminal().getP();
        mergedLoad.remove();
        if (loadAdderUsedToReplaceMergeLoad != null) {
            //Removing the mergedLoad is mandatory when trying to replace it.
            Load newLoad = loadAdderUsedToReplaceMergeLoad.add();
            loadReplacingTheMergedLoad.getAliases().forEach(newLoad::addAlias);
            newLoad.getTerminal().setP(loadReplacingTheMergedLoad.getTerminal().getP() / busState.p * pMergedLoad);
            newLoad.getTerminal().setQ(loadReplacingTheMergedLoad.getTerminal().getQ() / busState.q * qMergedLoad);
        }
    }

    private LoadAdder createNewLoad(Bus bus, Load mergedLoad, Load oldLoad, BusState busState) {
        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();
        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();

        oldLoad.getOptionalName().ifPresent(loadAdder::setName);
        loadAdder.setId(oldLoad.getId());

        loadAdder.setLoadType(LoadType.UNDEFINED);
        loadAdder.setP0(oldLoad.getP0() / busState.p0 * mergedLoad.getP0());
        loadAdder.setQ0(oldLoad.getQ0() / busState.q0 * mergedLoad.getQ0());

        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        }

        int nodeToAssign = -1;
        if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            nodeToAssign = oldLoad.getTerminal().getNodeBreakerView().getNode();
            loadAdder.setNode(nodeToAssign);
        }

        if (TopologyKind.BUS_BREAKER.equals(topologyKind)
                || mergedLoad.getTerminal().getNodeBreakerView().getNode() != nodeToAssign) {
            Load newLoad = loadAdder.add();
            oldLoad.getAliases().forEach(newLoad::addAlias);
            newLoad.getTerminal().setP(oldLoad.getTerminal().getP() / busState.p * mergedLoad.getTerminal().getP());
            newLoad.getTerminal().setQ(oldLoad.getTerminal().getQ() / busState.q * mergedLoad.getTerminal().getQ());
            return null;
        }
        //When the mergedLoad cannot be replaced, special treatment is mandatory
        return loadAdder;
    }

    private void mergeLoads(Bus bus) {
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

    private static final class BusState {
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
    }

    private static BusState getBusState(Bus bus) {
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
