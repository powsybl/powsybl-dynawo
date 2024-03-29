/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoVersion implements Comparable<DynawoVersion> {

    private static final String DEFAULT_SEPARATOR = "[.]";
    private static final String DEFAULT_DELIMITER = ".";
    private static final String CREATION_ERROR = "Wrong Dynaflow version format : ";
    private final List<Integer> versionArray;

    public DynawoVersion(Integer... version) {
        if (version.length == 0 || Arrays.stream(version).anyMatch(v -> v < 0)) {
            throw new PowsyblException(CREATION_ERROR + Arrays.toString(version));
        }
        this.versionArray = List.of(version);
    }

    public static DynawoVersion createFromString(String version) {
        return createFromString(version, DEFAULT_SEPARATOR);
    }

    public static DynawoVersion createFromString(String version, String separator) {
        try {
            Integer[] ints = Arrays.stream(version.split(separator)).map(Integer::parseInt).toArray(Integer[]::new);
            return new DynawoVersion(ints);
        } catch (Exception e) {
            throw new PowsyblException(CREATION_ERROR + e.getMessage(), e);
        }
    }

    public String toString(String separator) {
        return versionArray.stream().map(Object::toString).collect(Collectors.joining(separator));
    }

    @Override
    public String toString() {
        return this.toString(DEFAULT_DELIMITER);
    }

    @Override
    public int compareTo(DynawoVersion dynawoVersion) {
        if (dynawoVersion == null) {
            return 1;
        }
        return compareVersionArray(0, versionArray, dynawoVersion.versionArray);
    }

    private int compareVersionArray(int index, List<Integer> dv1, List<Integer> dv2) {
        boolean dv1End = index >= dv1.size();
        boolean dv2End = index >= dv2.size();
        if (dv1End && dv2End) {
            return 0;
        } else if (dv1End) {
            return dv2.get(index) > 0 ? -1 : 0;
        } else if (dv2End) {
            return dv1.get(index) > 0 ? 1 : 0;
        }
        int dv1Val = dv1.get(index);
        int dv2Val = dv2.get(index);
        if (dv1Val > dv2Val) {
            return 1;
        } else if (dv1Val < dv2Val) {
            return -1;
        }
        return compareVersionArray(index + 1, dv1, dv2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DynawoVersion that = (DynawoVersion) o;
        return this.compareTo(that) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionArray);
    }
}
