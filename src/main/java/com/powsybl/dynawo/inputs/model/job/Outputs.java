/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Outputs {

    private final String directory;
    private final boolean dumpLocalInitValues;
    private final boolean dumpGlobalInitValues;
    private final String timeLine;
    private final boolean exportIIDMFile;
    private final boolean exportDumpFile;
    private final String curve;
    private final String exportMode;
    private final List<LogAppender> appenders = new ArrayList<>();

    public Outputs() {
        this("curve");
    }

    public Outputs(String curve) {
        this("outputs", false, false, "TXT", false, true, curve, "CSV");
    }

    private Outputs(String directory, boolean dumpLocalInitValues, boolean dumpGlobalInitValues, String timeLine,
        boolean exportDumpFile, boolean exportIidmFile, String curve, String exportMode) {
        this.directory = Objects.requireNonNull(directory);
        this.dumpLocalInitValues = Objects.requireNonNull(dumpLocalInitValues);
        this.dumpGlobalInitValues = Objects.requireNonNull(dumpGlobalInitValues);
        this.timeLine = Objects.requireNonNull(timeLine);
        this.exportDumpFile = Objects.requireNonNull(exportDumpFile);
        this.exportIIDMFile = Objects.requireNonNull(exportIidmFile);
        this.curve = Objects.requireNonNull(curve);
        this.exportMode = Objects.requireNonNull(exportMode);
        add(new LogAppender());
    }

    public String getDirectory() {
        return directory;
    }

    public boolean isDumpLocalInitValues() {
        return dumpLocalInitValues;
    }

    public boolean isDumpGlobalInitValues() {
        return dumpGlobalInitValues;
    }

    public String getTimeLine() {
        return timeLine;
    }

    public boolean isExportDumpFile() {
        return exportDumpFile;
    }

    public boolean isExportIidmFile() {
        return exportIIDMFile;
    }

    public String getCurve() {
        return curve;
    }

    public String getExportMode() {
        return exportMode;
    }

    public List<LogAppender> getAppenders() {
        return appenders;
    }

    public Outputs add(LogAppender appender) {
        Objects.requireNonNull(appender);
        appenders.add(appender);
        return this;
    }
}
