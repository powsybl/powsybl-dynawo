package com.powsybl.dynawo.commons;

import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.network.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.NetworkXml;
import org.junit.Test;

import static com.powsybl.commons.test.ComparisonUtils.compareTxt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DynawoResultsMergeLoadsTest extends AbstractConverterTest {

    @Test
    public void nothingHappensWhenUnmergingUnchangedNetwork() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.unmergeLoads();

        Network expectedNetwork = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);

        compare(expectedNetwork, dynawoResultsMergeLoads.getNetwork());
    }

    @Test
    public void mergeLoadsMicroAssembled() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();

        Network expectedIidm = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembledWithMergedLoads", new ResourceSet("/MicroAssembled", "MicroAssembledWithMergedLoads.xiidm")), null);
        compare(expectedIidm, dynawoResultsMergeLoads.getNetwork());
    }

    @Test
    public void exceptionWhenMergingNetworkAlreadyModifiedMicroAssembled() {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();

        assertThrows("There is already merged loads for network id " + network.getId(), PowsyblException.class, () -> dynawoResultsMergeLoads.mergeLoads());
    }

    @Test
    public void unmergeLoadsMicroAssembled() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();
        dynawoResultsMergeLoads.unmergeLoads();

        assertEquals(2, network.getBusBreakerView().getBus("_81b0e447-181e-4aec-8921-f1dd7813bebc").getLoadStream().count());
        Network expectedIidm = Importers.importData("XIIDM", new ResourceDataSource("MicroAssembled", new ResourceSet("/MicroAssembled", "MicroAssembled.xiidm")), null);

        compare(expectedIidm, dynawoResultsMergeLoads.getNetwork());
    }

    @Test
    public void mergeLoadsSmallBusBranch() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranch", new ResourceSet("/SmallBusBranch", "SmallBusBranch.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();

        Network expectedIidm = Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranchWithMergeLoads", new ResourceSet("/SmallBusBranch", "SmallBusBranchWithMergeLoads.xiidm")), null);
        compare(expectedIidm, dynawoResultsMergeLoads.getNetwork());
    }

    @Test
    public void unmergeLoadsSmallBusBranch() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranch", new ResourceSet("/SmallBusBranch", "SmallBusBranch.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();
        dynawoResultsMergeLoads.unmergeLoads();

        assertEquals(2, network.getBusBreakerView().getBus("_04483c26-c766-11e1-8775-005056c00008").getLoadStream().count());
        Network expectedIidm = Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranch", new ResourceSet("/SmallBusBranch", "SmallBusBranch.xiidm")), null);

        compare(expectedIidm, dynawoResultsMergeLoads.getNetwork());
    }

    @Test
    public void mergeLoadsSmallNodeBreaker() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("SmallNodeBreaker_fix_line_044bbe91", new ResourceSet("/SmallNodeBreaker", "SmallNodeBreaker_fix_line_044bbe91.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();

        Network expectedIidm = Importers.importData("XIIDM", new ResourceDataSource("SmallNodeBreakerWithMergeLoads", new ResourceSet("/SmallNodeBreaker", "SmallNodeBreakerWithMergeLoads.xiidm")), null);
        compare(expectedIidm, dynawoResultsMergeLoads.getNetwork());
    }

    @Test
    public void unmergeLoadsSmallNodeBreaker() throws IOException {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("SmallNodeBreaker_fix_line_044bbe91", new ResourceSet("/SmallNodeBreaker", "SmallNodeBreaker_fix_line_044bbe91.xiidm")), null);
        DynawoResultsMergeLoads dynawoResultsMergeLoads = new DynawoResultsMergeLoads(network);
        dynawoResultsMergeLoads.mergeLoads();
        dynawoResultsMergeLoads.unmergeLoads();

        assertEquals(2, network.getVoltageLevel("_0476c639-c766-11e1-8775-005056c00008").getLoadStream().count());
        Network expectedIidm = Importers.importData("XIIDM", new ResourceDataSource("SmallNodeBreaker_fix_line_044bbe91", new ResourceSet("/SmallNodeBreaker", "SmallNodeBreaker_fix_line_044bbe91.xiidm")), null);

        compare(expectedIidm, dynawoResultsMergeLoads.getNetwork());
    }

    private void compare(Network expected, Network actual) throws IOException {
        Path pexpected = tmpDir.resolve("expected.xiidm");
        assertNotNull(pexpected);
        Path pactual = tmpDir.resolve("actual.xiidm");
        assertNotNull(pactual);
        NetworkXml.write(expected, pexpected);
        actual.setCaseDate(expected.getCaseDate());
        NetworkXml.write(actual, pactual);
        compareTxt(Files.newInputStream(pexpected), Files.newInputStream(pactual));
    }
}
