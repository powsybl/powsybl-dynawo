/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public class TimeSeries {
    // TODO: use powsybl time series instead of a specific minimal class here

    public TimeSeries(List<String> names, Map<Double, List<Double>> values) {
        this.names = names;
        this.values = values;
    }

    public List<String> getNames() {
        return names;
    }

    public Map<Double, List<Double>> getValues() {
        return values;
    }

    private List<String> names = new ArrayList<>();

    // For each value of time, the values for all series
    private Map<Double, List<Double>> values = new HashMap<>();

}
