package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;

public class DynawoCurveTest {

    @Test
    public void test() {
        DynawoCurve curve = new DynawoCurve("busId", "variable1", "variable2");

        assertEquals("busId", curve.getModelId());

        Iterator<String> iterator = curve.getVariables().iterator();
        assertEquals("variable1", iterator.next());
        assertEquals("variable2", iterator.next());
    }
}
