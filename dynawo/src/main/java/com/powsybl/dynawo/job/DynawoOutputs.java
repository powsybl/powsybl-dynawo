/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoOutputs {

    private final String directory;
    private final boolean dumpLocalInitValues;
    private final boolean dumpGlobalInitValues;
    private final String constraints;
    private final String timeLine;
    private final boolean exportFinalState;
    private final boolean exportIIDMFile;
    private final boolean exportDumpFile;
    private final String curve;
    private final String exportMode;
    private final List<LogAppender> appenders = new ArrayList<>();

    public DynawoOutputs(String directory, String curve) {
        this(directory, true, true, null, "TXT", false, false, false, curve, "CSV");
    }

    public DynawoOutputs(String directory, boolean dumpLocalInitValues, boolean dumpGlobalInitValues,
        String constraints, String timeLine, boolean exportFinalState, boolean exportIidmFile, boolean exportDumpFile,
        String curve, String exportMode) {
        this.directory = Objects.requireNonNull(directory);
        this.dumpLocalInitValues = dumpLocalInitValues;
        this.dumpGlobalInitValues = dumpGlobalInitValues;
        this.constraints = constraints;
        this.timeLine = Objects.requireNonNull(timeLine);
        this.exportFinalState = exportFinalState;
        this.exportIIDMFile = exportIidmFile;
        this.exportDumpFile = exportDumpFile;
        this.curve = Objects.requireNonNull(curve);
        this.exportMode = Objects.requireNonNull(exportMode);
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

    public String getConstraints() {
        return constraints;
    }

    public String getTimeLine() {
        return timeLine;
    }

    public boolean isExportFinalState() {
        return exportFinalState;
    }

    public boolean isExportIidmFile() {
        return exportIIDMFile;
    }

    public boolean isExportDumpFile() {
        return exportDumpFile;
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

    public DynawoOutputs addAppenders(List<LogAppender> appender) {
        appenders.addAll(appender);
        return this;
    }

    public DynawoOutputs add(LogAppender appender) {
        Objects.requireNonNull(appender);
        appenders.add(appender);
        return this;
    }
}
