/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.loadflow.LoadFlowParameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import static com.powsybl.dynaflow.DynaflowConstants.CONFIG_FILENAME;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaflowParameters extends AbstractExtension<LoadFlowParameters> {

    private static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-default-parameters";
    public static final boolean DEFAULT_SVC_REGULATION_ON = false;
    public static final boolean DEFAULT_SHUNT_REGULATION_ON = false;
    public static final boolean DEFAULT_AUTOMATIC_SLACK_BUS_ON = false;
    public static final boolean DEFAULT_VSC_AS_GENERATORS = true;
    public static final boolean DEFAULT_LCC_AS_LOADS = true;

    private boolean svcRegulationOn = DEFAULT_SVC_REGULATION_ON;
    private boolean shuntRegulationOn = DEFAULT_SHUNT_REGULATION_ON;
    private boolean automaticSlackBusOn = DEFAULT_AUTOMATIC_SLACK_BUS_ON;
    private boolean vscAsGenerators = DEFAULT_VSC_AS_GENERATORS;
    private boolean lccAsLoads = DEFAULT_LCC_AS_LOADS;

    public boolean getSvcRegulationOn() {
        return svcRegulationOn;
    }

    public DynaflowParameters setSvcRegulationOn(boolean svcRegulationOn) {
        this.svcRegulationOn = svcRegulationOn;
        return this;
    }

    public boolean getShuntRegulationOn() {
        return shuntRegulationOn;
    }

    public DynaflowParameters setShuntRegulationOn(boolean shuntRegulationOn) {
        this.shuntRegulationOn = shuntRegulationOn;
        return this;
    }

    public boolean getAutomaticSlackBusOn() {
        return automaticSlackBusOn;
    }

    public DynaflowParameters setAutomaticSlackBusOn(boolean automaticSlackBusOn) {
        this.automaticSlackBusOn = automaticSlackBusOn;
        return this;
    }

    public boolean getVscAsGenerators() {
        return vscAsGenerators;
    }

    public DynaflowParameters setVscAsGenerators(boolean vscAsGenerators) {
        this.vscAsGenerators = vscAsGenerators;
        return this;
    }

    public boolean getLccAsLoads() {
        return lccAsLoads;
    }

    public DynaflowParameters setLccAsLoads(boolean lccAsLoads) {
        this.lccAsLoads = lccAsLoads;
        return this;
    }

    public void writeConfigInputFile(Path workingDir, LoadFlowParameters params) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(workingDir.resolve(CONFIG_FILENAME).toString())) {
            writeConfigInputFile(outputStream, params);
        }
    }

    public void writeConfigInputFile(OutputStream outputStream, LoadFlowParameters params) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator jsonGenerator = factory.createGenerator(outputStream, JsonEncoding.UTF8)) {
            jsonGenerator.useDefaultPrettyPrinter();
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("dfl-config");
            jsonGenerator.writeBooleanField("SVCRegulationOn", svcRegulationOn);
            jsonGenerator.writeBooleanField("ShuntRegulationOn", shuntRegulationOn);
            jsonGenerator.writeBooleanField("AutomaticSlackBusOn", automaticSlackBusOn);
            jsonGenerator.writeBooleanField("VSCAsGenerators", vscAsGenerators);
            jsonGenerator.writeBooleanField("LCCAsLoads", lccAsLoads);
            jsonGenerator.writeBooleanField("InfiniteReactiveLimits", params.isNoGeneratorReactiveLimits());
            jsonGenerator.writeBooleanField("PSTRegulationOn", params.isPhaseShifterRegulationOn());
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }

    }

    @Override
    public String getName() {
        return "DynaflowParameters";
    }

    @Override
    public String toString() {
        ImmutableMap.Builder<String, Object> immutableMapBuilder = ImmutableMap.builder();
        immutableMapBuilder
                .put("svcRegulationOn", svcRegulationOn)
                .put("shuntRegulationON", shuntRegulationOn)
                .put("automaticSlackBusON", automaticSlackBusOn)
                .put("vscAsGenerators", vscAsGenerators)
                .put("lccAsLoads", lccAsLoads);

        return immutableMapBuilder.build().toString();
    }

    @AutoService(LoadFlowParameters.ConfigLoader.class)
    public static class DynaflowConfigLoader implements LoadFlowParameters.ConfigLoader<DynaflowParameters> {

        //Watch out for the name in the config.yml, no upper case at the beginning to match the one in the config.json
        //that can not have upper case also at the beginning
        @Override
        public DynaflowParameters load(PlatformConfig platformConfig) {
            DynaflowParameters parameters = new DynaflowParameters();

            platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS)
                    .ifPresent(config -> {
                        parameters.setSvcRegulationOn(config.getBooleanProperty("svcRegulationOn", DEFAULT_SVC_REGULATION_ON))
                                .setShuntRegulationOn(config.getBooleanProperty("shuntRegulationOn", DEFAULT_SHUNT_REGULATION_ON))
                                .setAutomaticSlackBusOn(config.getBooleanProperty("automaticSlackBusOn", DEFAULT_AUTOMATIC_SLACK_BUS_ON))
                                .setVscAsGenerators(config.getBooleanProperty("vscAsGenerators", DEFAULT_VSC_AS_GENERATORS))
                                .setLccAsLoads(config.getBooleanProperty("lccAsLoads", DEFAULT_LCC_AS_LOADS));
                    });

            return parameters;
        }

        @Override
        public String getExtensionName() {
            return "DynaflowParameters";
        }

        @Override
        public String getCategoryName() {
            return "loadflow-parameters";
        }

        @Override
        public Class<? super DynaflowParameters> getExtensionClass() {
            return DynaflowParameters.class;
        }
    }
}
