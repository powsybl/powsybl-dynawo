/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.job;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LogAppender {

    private final String tag;
    private final String file;
    private final String lvlFilter;

    public LogAppender(String tag, String file, String lvlFilter) {
        this.tag = tag;
        this.file = file;
        this.lvlFilter = lvlFilter;
    }

    public String getTag() {
        return tag;
    }

    public String getFile() {
        return file;
    }

    public String getLvlFilter() {
        return lvlFilter;
    }

}
