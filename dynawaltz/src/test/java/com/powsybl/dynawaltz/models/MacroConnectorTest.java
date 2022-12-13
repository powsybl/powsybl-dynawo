package com.powsybl.dynawaltz.models;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

public class MacroConnectorTest {

    @Test
    public void compareTwoMacroConnectorsEqual() {
        BlackBoxModel dumdum1 = mock(BlackBoxModel.class);
        BlackBoxModel dumdum2 = mock(BlackBoxModel.class);

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(new VarConnection("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(dumdum1, dumdum2, List.of(new VarConnection("pair1", "pair2")));
        assertEquals(connector1, connector2);
    }

    @Test
    public void compareTwoMacroConnectorsSameLib1DifferentLib2() {
        BlackBoxModel dumdum1 = mock(BlackBoxModel.class);
        BlackBoxModel dumdum2 = mock(BlackBoxModel.class);

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(new VarConnection("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(dumdum1, mock(BlackBoxModel.class), List.of(new VarConnection("pair1", "pair2")));
        assertNotEquals(connector1, connector2);
    }

    @Test
    public void compareTwoMacroConnectorsSameLib2DifferentLib1() {
        BlackBoxModel dumdum1 = mock(BlackBoxModel.class);
        BlackBoxModel dumdum2 = mock(BlackBoxModel.class);

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(new VarConnection("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(mock(BlackBoxModel.class), dumdum2, List.of(new VarConnection("pair1", "pair2")));
        assertNotEquals(connector1, connector2);
    }

    @Test
    public void compareTwoMacroConnectorsInvertedLibs() {
        BlackBoxModel dumdum1 = mock(BlackBoxModel.class);
        BlackBoxModel dumdum2 = mock(BlackBoxModel.class);

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(new VarConnection("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(dumdum2, dumdum1, List.of(new VarConnection("pair1", "pair2")));
        assertEquals(connector1, connector2);
    }

}
