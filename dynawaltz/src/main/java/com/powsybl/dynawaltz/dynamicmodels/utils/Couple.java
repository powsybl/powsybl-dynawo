package com.powsybl.dynawaltz.dynamicmodels.utils;

public class Couple<T> {

    private final T obj1;
    private final T obj2;

    public Couple(T obj1, T obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public T getObj1() {
        return obj1;
    }

    public T getObj2() {
        return obj2;
    }

    @Override
    public int hashCode() {
        return obj1.hashCode() + obj2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Couple) {
            Couple<T> cpl = (Couple) obj;
            if (cpl.getObj1().equals(obj1) && cpl.getObj2().equals(obj2)
                || cpl.getObj2().equals(obj1) && cpl.getObj1().equals(obj2)) {
                return true;
            }
        }
        return false;
    }
}
