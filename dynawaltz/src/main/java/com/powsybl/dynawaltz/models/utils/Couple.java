/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.utils;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public final class Couple<T> {

    private final T obj1;
    private final T obj2;

    private Couple(T obj1, T obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public static <U> Couple<U> of(U a, U b) {
        return new Couple<>(a, b);
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
            Couple<?> cpl = (Couple<?>) obj;
            return cpl.obj1.equals(obj1) && cpl.obj2.equals(obj2)
                    || cpl.obj2.equals(obj1) && cpl.obj1.equals(obj2);
        }
        return false;
    }
}
