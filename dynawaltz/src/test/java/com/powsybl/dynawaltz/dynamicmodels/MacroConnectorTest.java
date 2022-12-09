package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.dynamicmodels.utils.MacroConnector;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MacroConnectorTest {

    @Test
    public void compareTwoMacroConnectorsEqual() {
        MacroConnector connector1 = new MacroConnector("lib1", "lib2", List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector("lib1", "lib2", List.of(Pair.of("pair1", "pair2")));
        assertTrue(connector1.equals(connector2));
    }

    @Test
    public void compareTwoMacroConnectorsSameLib1DifferentLib2() {
        MacroConnector connector1 = new MacroConnector("lib1", "lib2", List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector("lib1", "lib3", List.of(Pair.of("pair1", "pair2")));
        assertFalse(connector1.equals(connector2));
    }

    @Test
    public void compareTwoMacroConnectorsSameLib2DifferentLib1() {
        MacroConnector connector1 = new MacroConnector("lib1", "lib2", List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector("lib3", "lib2", List.of(Pair.of("pair1", "pair2")));
        assertFalse(connector1.equals(connector2));
    }

    @Test
    public void compareTwoMacroConnectorsInvertedLibs() {
        MacroConnector connector1 = new MacroConnector("lib1", "lib2", List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector("lib2", "lib1", List.of(Pair.of("pair1", "pair2")));
        assertTrue(connector1.equals(connector2));
    }

}
