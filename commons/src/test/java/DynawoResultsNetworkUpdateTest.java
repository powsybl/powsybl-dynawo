import com.powsybl.commons.extensions.Extension;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Guillem Jané Guasch <zamarrenolm at aia.es>
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
