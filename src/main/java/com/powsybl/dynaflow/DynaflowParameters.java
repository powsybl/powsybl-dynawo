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

public class DynaflowParameters extends AbstractExtension<LoadFlowParameters> {

    private static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-default-parameters";
    public static final boolean SVC_REGULATION_ON = false;
    public static final boolean SHUNT_REGULATION_ON = false;
    public static final boolean AUTOMATIC_SLACK_BUS_ON = false;
    public static final boolean VSC_AS_GENERATORS = true;
    public static final boolean LCC_AS_LOADS = true;

    private boolean svcRegulationOn = SVC_REGULATION_ON;
    private boolean shuntRegulationOn = SHUNT_REGULATION_ON;
    private boolean automaticSlackBusOn = AUTOMATIC_SLACK_BUS_ON;
    private boolean vscAsGenerators = VSC_AS_GENERATORS;
    private boolean lccAsLoads = LCC_AS_LOADS;

    public boolean getSvcRegulationOn() {
        return svcRegulationOn;
    }

    public void setSvcRegulationOn(boolean svcRegulationOn) {
        this.svcRegulationOn = svcRegulationOn;
    }

    public boolean getShuntRegulationOn() {
        return shuntRegulationOn;
    }

    public void setShuntRegulationOn(boolean shuntRegulationOn) {
        this.shuntRegulationOn = shuntRegulationOn;
    }

    public boolean getAutomaticSlackBusOn() {
        return automaticSlackBusOn;
    }

    public void setAutomaticSlackBusOn(boolean automaticSlackBusOn) {
        this.automaticSlackBusOn = automaticSlackBusOn;
    }

    public boolean getVscAsGenerators() {
        return vscAsGenerators;
    }

    public void setVscAsGenerators(boolean vscAsGenerators) {
        this.vscAsGenerators = vscAsGenerators;
    }

    public boolean getLccAsLoads() {
        return lccAsLoads;
    }

    public void setLccAsLoads(boolean lccAsLoads) {
        this.lccAsLoads = lccAsLoads;
    }

    public void writeConfigInputFile(Path workingDir, LoadFlowParameters params) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(workingDir.resolve(CONFIG_FILENAME).toString());
        writeConfigInputFile(outputStream, params);
    }

    public void writeConfigInputFile(OutputStream outputStream, LoadFlowParameters params) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonGenerator jsonGenerator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
        jsonGenerator.useDefaultPrettyPrinter();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectFieldStart("dfl-config");
        jsonGenerator.writeBooleanField("SVCRegulationOn", svcRegulationOn);
        jsonGenerator.writeBooleanField("ShuntRegulationOn", shuntRegulationOn);
        jsonGenerator.writeBooleanField("AutomaticSlackBusOn", automaticSlackBusOn);
        jsonGenerator.writeBooleanField("VSCAsGenerators", vscAsGenerators);
        jsonGenerator.writeBooleanField("LCCAsLoads", lccAsLoads);
        //This is here that we do the mapping from the names of the variables from powsybl to dynaflow
        jsonGenerator.writeBooleanField("InfiniteReactiveLimits", params.isNoGeneratorReactiveLimits());
        jsonGenerator.writeBooleanField("PSTRegulationOn", params.isPhaseShifterRegulationOn());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
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
                        parameters.setSvcRegulationOn(config.getBooleanProperty("svcRegulationOn", SVC_REGULATION_ON));
                        parameters.setShuntRegulationOn(config.getBooleanProperty("shuntRegulationOn", SHUNT_REGULATION_ON));
                        parameters.setAutomaticSlackBusOn(config.getBooleanProperty("automaticSlackBusOn", AUTOMATIC_SLACK_BUS_ON));
                        parameters.setVscAsGenerators(config.getBooleanProperty("vscAsGenerators", VSC_AS_GENERATORS));
                        parameters.setLccAsLoads(config.getBooleanProperty("lccAsLoads", LCC_AS_LOADS));
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
