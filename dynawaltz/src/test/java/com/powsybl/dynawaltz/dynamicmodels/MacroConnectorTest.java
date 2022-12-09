package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.utils.MacroConnector;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MacroConnectorTest {
    class DummyBlackBox extends AbstractBlackBoxModelWithParameterId {

        AtomicInteger atomicInteger = new AtomicInteger();

        public DummyBlackBox() {
            super("parameterId");
        }


        @Override
        public String getLib() {
            return "lib" + atomicInteger.incrementAndGet();
        }

        @Override
        public List<Pair<String, String>> getAttributesConnectTo() {
            return null;
        }

        @Override
        public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {

        }

        @Override
        public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {

        }

        @Override
        public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
            return null;
        }

        @Override
        public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext dynaWaltzContext) {
            return null;
        }
    }

    @Test
    public void compareTwoMacroConnectorsEqual() {
        DummyBlackBox dumdum1 = new DummyBlackBox();
        DummyBlackBox dumdum2 = new DummyBlackBox();

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(dumdum1, dumdum2, List.of(Pair.of("pair1", "pair2")));
        assertTrue(connector1.equals(connector2));
    }

    @Test
    public void compareTwoMacroConnectorsSameLib1DifferentLib2() {
        DummyBlackBox dumdum1 = new DummyBlackBox();
        DummyBlackBox dumdum2 = new DummyBlackBox();

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(dumdum1, new DummyBlackBox(), List.of(Pair.of("pair1", "pair2")));
        assertFalse(connector1.equals(connector2));
    }

    @Test
    public void compareTwoMacroConnectorsSameLib2DifferentLib1() {
        DummyBlackBox dumdum1 = new DummyBlackBox();
        DummyBlackBox dumdum2 = new DummyBlackBox();

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(new DummyBlackBox(), dumdum2, List.of(Pair.of("pair1", "pair2")));
        assertFalse(connector1.equals(connector2));
    }

    @Test
    public void compareTwoMacroConnectorsInvertedLibs() {
        DummyBlackBox dumdum1 = new DummyBlackBox();
        DummyBlackBox dumdum2 = new DummyBlackBox();

        MacroConnector connector1 = new MacroConnector(dumdum1, dumdum2, List.of(Pair.of("pair1", "pair2")));
        MacroConnector connector2 = new MacroConnector(dumdum2, dumdum1, List.of(Pair.of("pair1", "pair2")));
        assertTrue(connector1.equals(connector2));
    }

}
