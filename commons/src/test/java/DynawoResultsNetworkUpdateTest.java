import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author Guillem Jané Guasch <janeg at aia.es>
 */
public class DynawoResultsNetworkUpdateTest {

    @Test
    public void testUpdate() {
        Network networkInput = EurostagTutorialExample1Factory.create();
        Network networkOutput = EurostagTutorialExample1Factory.create();

        DynawoResultsNetworkUpdate.zero(networkOutput);
        DynawoResultsNetworkUpdate.update(networkInput, networkOutput);
        assertTrue(DynawoResultsNetworkUpdate.equalState(networkInput, networkOutput));
    }
}
