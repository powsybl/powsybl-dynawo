package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Connectable;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.LoadAdder;
import com.powsybl.iidm.network.LoadType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Terminal;
import com.powsybl.iidm.network.TopologyKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DynawoResultsMergeLoads {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoResultsMergeLoads.class);
    private static final String MERGE_LOAD_PREFIX_ID = "merged_load_.";
    private final String idNetwork;
    private final Map<String, List<InfoLoad>> mapLoadsToMergePerBus = new HashMap<>();
    private static final String WRONG_NETWORK_ASSOCIATION = "Not associated to the right network. Linked to ";

    class InfoLoad {
        private final Load load;
        private double coeffP0 = 0.;
        private double coeffQ0 = 0.;
        private double coeffP = 0.;
        private double coeffQ = 0.;
        private int node = -1;

        public InfoLoad(Load load, TopologyKind topologyKind) {
            this.load = load;
            if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
                this.node = load.getTerminal().getNodeBreakerView().getNode();
            }
        }

        public Load getLoad() {
            return load;
        }

        public double getCoeffP0() {
            return coeffP0;
        }

        public void setCoeffP0(double coeffP0) {
            this.coeffP0 = coeffP0;
        }

        public double getCoeffQ0() {
            return coeffQ0;
        }

        public void setCoeffQ0(double coeffQ0) {
            this.coeffQ0 = coeffQ0;
        }

        public double getCoeffP() {
            return coeffP;
        }

        public void setCoeffP(double coeffP) {
            this.coeffP = coeffP;
        }

        public double getCoeffQ() {
            return coeffQ;
        }

        public void setCoeffQ(double coeffQ) {
            this.coeffQ = coeffQ;
        }

        public int getNode() {
            return node;
        }
    }

    public DynawoResultsMergeLoads(String idNetwork) {
        this.idNetwork = idNetwork;
    }

    public void mergeLoads(Network network) throws PowsyblException {
        if (!idNetwork.equals(network.getId())) {
            throw new PowsyblException(WRONG_NETWORK_ASSOCIATION + idNetwork);
        }
        if (mapLoadsToMergePerBus.size() != 0) {
            throw new PowsyblException("There is already merged loads for network id " + idNetwork);
        }
        retrieveMapLoadsToMergePerBus(network);
        List<Load> loadsModified = createLoadsMerged(network);
        deleteLoads(loadsModified);
    }

    public void unmergeLoads(Network network) throws PowsyblException {
        if (!idNetwork.equals(network.getId())) {
            throw new PowsyblException(WRONG_NETWORK_ASSOCIATION + idNetwork);
        }
        if (mapLoadsToMergePerBus.size() != 0) {
            proceedUnmergeLoads(network);
            mapLoadsToMergePerBus.clear();
        } else {
            LOGGER.warn("No load unmerged for network {}", idNetwork);
        }
    }

    private void proceedUnmergeLoads(Network network) {
        for (Bus bus : network.getBusBreakerView().getBuses()) {
            if (mapLoadsToMergePerBus.containsKey(bus.getId())) {
                Optional<Load> optionalMergedLoad = bus.getLoadStream().filter(load -> load.getId().startsWith(MERGE_LOAD_PREFIX_ID)).findAny();
                optionalMergedLoad.ifPresent(mergedLoad -> recreateInitialLoads(bus, mergedLoad));
            }
        }
    }

    private void recreateInitialLoads(Bus bus, Load mergedLoad) {
        List<InfoLoad> loads = mapLoadsToMergePerBus.get(bus.getId());
        LoadAdder loadAdderUsedToReplaceMergeLoad = null;
        InfoLoad loadReplacingTheMergedLoad = null;
        for (InfoLoad load : loads) {
            LoadAdder mergedLoadNotReplaced = createNewLoad(bus, mergedLoad, load);
            if (mergedLoadNotReplaced != null) {
                loadAdderUsedToReplaceMergeLoad = mergedLoadNotReplaced;
                loadReplacingTheMergedLoad = load;
            }
        }
        double qMergedLoad = mergedLoad.getTerminal().getQ();
        double pMergedLoad = mergedLoad.getTerminal().getP();
        mergedLoad.remove();
        if (loadAdderUsedToReplaceMergeLoad != null) {
            //Removing the mergedLoad is mandatory when trying to replace it.
            replacingMergedLoad(loadAdderUsedToReplaceMergeLoad, loadReplacingTheMergedLoad, qMergedLoad, pMergedLoad);
        }
    }

    private void replacingMergedLoad(LoadAdder loadAdderUsedToReplaceMergeLoad, InfoLoad loadReplacingTheMergedLoad, double qMergedLoad, double pMergedLoad) {
        Load newLoad = loadAdderUsedToReplaceMergeLoad.add();
        loadReplacingTheMergedLoad.getLoad().getAliases().forEach(newLoad::addAlias);
        newLoad.getTerminal().setP(loadReplacingTheMergedLoad.getCoeffP() * pMergedLoad);
        newLoad.getTerminal().setQ(loadReplacingTheMergedLoad.getCoeffQ() * qMergedLoad);
    }

    private LoadAdder createNewLoad(Bus bus, Load mergedLoad, InfoLoad oldLoad) {
        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();
        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();

        oldLoad.getLoad().getOptionalName().ifPresent(loadAdder::setName);
        loadAdder.setId(oldLoad.getLoad().getId());

        loadAdder.setLoadType(LoadType.UNDEFINED);
        loadAdder.setP0(oldLoad.getCoeffP0() * mergedLoad.getP0());
        loadAdder.setQ0(oldLoad.getCoeffQ0() * mergedLoad.getQ0());

        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        }

        int nodeToAssign = -1;
        if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            nodeToAssign = oldLoad.getNode();
            loadAdder.setNode(nodeToAssign);
        }

        if (TopologyKind.BUS_BREAKER.equals(topologyKind)
                || mergedLoad.getTerminal().getNodeBreakerView().getNode() != nodeToAssign) {
            Load newLoad = loadAdder.add();
            oldLoad.getLoad().getAliases().forEach(newLoad::addAlias);
            newLoad.getTerminal().setP(oldLoad.getCoeffP() * mergedLoad.getTerminal().getP());
            newLoad.getTerminal().setQ(oldLoad.getCoeffQ() * mergedLoad.getTerminal().getQ());
            return null;
        }
        //When the mergedLoad cannot be replaced, special treatment is mandatory
        return loadAdder;
    }

    private void retrieveMapLoadsToMergePerBus(Network network) {
        for (Bus bus : network.getBusBreakerView().getBuses()) {
            if (bus.getLoadStream().count() > 1) {
                mapLoadsToMergePerBus.put(bus.getId(), bus.getLoadStream().map(load -> new InfoLoad(load, bus.getVoltageLevel().getTopologyKind())).collect(Collectors.toList()));
            }
        }
    }

    private List<Load> createLoadsMerged(Network network) {
        List<Load> refLoadsModified = new ArrayList<>();
        for (Bus bus : network.getBusBreakerView().getBuses()) {
            if (mapLoadsToMergePerBus.containsKey(bus.getId())) {
                refLoadsModified.add(createMergedLoad(bus));
            }
        }
        return refLoadsModified;
    }

    private Load createMergedLoad(Bus bus) {
        TopologyKind topologyKind = bus.getVoltageLevel().getTopologyKind();

        LoadAdder loadAdder = bus.getVoltageLevel().newLoad();
        List<InfoLoad> lLoad = mapLoadsToMergePerBus.get(bus.getId());
        loadAdder.setId(MERGE_LOAD_PREFIX_ID + bus.getId());

        loadAdder.setLoadType(LoadType.UNDEFINED);

        double p0 = lLoad.stream().map(InfoLoad::getLoad).mapToDouble(Load::getP0).sum();
        loadAdder.setP0(p0);

        double q0 = lLoad.stream().map(InfoLoad::getLoad).mapToDouble(Load::getQ0).sum();
        loadAdder.setQ0(q0);

        double p = lLoad.stream().map(InfoLoad::getLoad).map(Load::getTerminal).mapToDouble(Terminal::getP).sum();
        double q = lLoad.stream().map(InfoLoad::getLoad).map(Load::getTerminal).mapToDouble(Terminal::getQ).sum();

        for (InfoLoad infoLoad : lLoad) {
            infoLoad.setCoeffP0(infoLoad.getLoad().getP0() / p0);
            infoLoad.setCoeffQ0(infoLoad.getLoad().getQ0() / q0);
            infoLoad.setCoeffP(infoLoad.getLoad().getTerminal().getP() / p);
            infoLoad.setCoeffQ(infoLoad.getLoad().getTerminal().getQ() / q);
        }

        if (TopologyKind.BUS_BREAKER.equals(topologyKind)) {
            loadAdder.setBus(bus.getId());
            loadAdder.setConnectableBus(bus.getId());
        }

        Load refLoadForMergedLoads = null;
        if (TopologyKind.NODE_BREAKER.equals(topologyKind)) {
            refLoadForMergedLoads = bus.getLoads().iterator().next();
            loadAdder.setNode(refLoadForMergedLoads.getTerminal().getNodeBreakerView().getNode());
            refLoadForMergedLoads.remove();
        }

        Load load = loadAdder.add();
        load.getTerminal().setP(p);
        load.getTerminal().setQ(q);

        return refLoadForMergedLoads;
    }

    private void deleteLoads(List<Load> loadsAlreadyModified) {
        mapLoadsToMergePerBus.values().forEach(loads ->
                loads.stream().map(InfoLoad::getLoad)
                        .filter(((Predicate<Load>) (loadsAlreadyModified::contains)).negate())
                        .forEach(Connectable::remove));
    }
}
