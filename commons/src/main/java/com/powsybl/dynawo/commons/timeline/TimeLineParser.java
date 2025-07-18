/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import com.powsybl.dynawo.commons.ExportMode;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public interface TimeLineParser {

    List<TimelineEntry> parse(Path timeLineFile);

    static List<TimelineEntry> parse(Path timelineFile, ExportMode exportMode) {
        TimeLineParser parser = switch (exportMode) {
            case CSV -> new CsvTimeLineParser(';');
            case TXT -> new CsvTimeLineParser();
            case XML -> new XmlTimeLineParser();
        };
        return parser.parse(timelineFile);
    }
}
