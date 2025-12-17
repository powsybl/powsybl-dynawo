package com.powsybl.dynawo.criticaltimecalculation.json;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.algorithms.NodeFaultEventData;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsBuilder;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeFaultsDeserializerJsonTest {

    @Test
    void testNodeFaults() throws IOException {
        Network network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        try (var is = getClass().getResourceAsStream("/CriticalTimeCalculationNodeFaults.json")) {
            List<NodeFaultEventData> nodeFaultsList = new SupplierJsonDeserializer<>(
                    new CriticalTimeCalculationNodeFaultsJsonDeserializer(() -> new NodeFaultsBuilder(network, ReportNode.NO_OP)))
                    .deserialize(is);

            assertThat(nodeFaultsList).usingRecursiveFieldByFieldElementComparatorOnFields()
                    .containsExactlyInAnyOrderElementsOf(getNodeFaultsListFromEventData());
            assertThat(nodeFaultsList).usingRecursiveFieldByFieldElementComparatorOnFields()
                    .containsExactlyInAnyOrderElementsOf(getNodeFaultsListFromBuilder(network));
        }

    }

    private static List<NodeFaultEventData> getNodeFaultsListFromEventData() {
        return List.of(
                new NodeFaultEventData.Builder()
                        .setStaticId("NGEN")
                        .setFaultStartTime(1)
                        .setFaultStopTime(2)
                        .setFaultXPu(0.5)
                        .setFaultRPu(0.5)
                        .build(),
                new NodeFaultEventData.Builder()
                        .setStaticId("N1")
                        .setFaultStartTime(1)
                        .setFaultStopTime(2)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build()
        );
    }

    private static List<NodeFaultEventData> getNodeFaultsListFromBuilder(Network network) {
        return List.of(
                new NodeFaultsBuilder(network, ReportNode.NO_OP)
                        .elementId("GEN")
                        .faultXPu(0.5)
                        .faultRPu(0.5)
                        .build(),
                new NodeFaultsBuilder(network, ReportNode.NO_OP)
                        .elementId("GEN2")
                        .build()
        );
    }
}
