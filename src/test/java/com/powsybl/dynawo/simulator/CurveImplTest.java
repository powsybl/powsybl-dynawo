package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

public class CurveImplTest {

    @Test
    public void test() {
        CurveImpl curve = new CurveImpl("busId", Arrays.asList("variable1", "variable2"));

        assertEquals("busId", curve.getModelId());

        Iterator<String> iterator = curve.getVariables().iterator();
        assertEquals("variable1", iterator.next());
        assertEquals("variable2", iterator.next());
    }
}
