/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.inputs.model.DynawoInputs;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class XMLDynawoInputsExporter {

    public XMLDynawoInputsExporter() {
        this(PlatformConfig.defaultConfig());
    }

    public XMLDynawoInputsExporter(PlatformConfig platformConfig) {
        this.platformConfig = Objects.requireNonNull(platformConfig);
    }

    public void export(DynawoInputs inputs, Path workingDir) throws IOException, XMLStreamException {
        new JobsXml().write(workingDir, inputs.getJobs());
    }

    private final PlatformConfig platformConfig;

}
