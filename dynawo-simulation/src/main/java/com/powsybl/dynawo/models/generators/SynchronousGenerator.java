/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.VarPrefix;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class SynchronousGenerator extends BaseGenerator implements FrequencySynchronizedModel {

    private static final String DEFAULT_OMEGA_PU_VAR_NAME = "generator_omegaPu";
    private static final String DEFAULT_OMEGA_REF_PU = "generator_omegaRefPu";
    private static final String DEFAULT_RUNNING = "generator_running";

    private String omegaPu = DEFAULT_OMEGA_PU_VAR_NAME;
    private String omegaRefPu = DEFAULT_OMEGA_REF_PU;
    private String running = DEFAULT_RUNNING;

    private final EnumGeneratorComponent generatorComponent;

    protected SynchronousGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig, EnumGeneratorComponent generatorComponent) {
        super(generator, parameterSetId, modelConfig);
        this.generatorComponent = Objects.requireNonNull(generatorComponent);
        Map<String, VarPrefix> configVarPrefix = modelConfig.varPrefix();
        if (!configVarPrefix.isEmpty()) {
            VarPrefix varPrefix = configVarPrefix.get("omegaPu");
            if (varPrefix != null) {
                this.omegaPu = varPrefix.toVarName();
            }
            if ((varPrefix = configVarPrefix.get("omegaRefPu")) != null) {
                this.omegaRefPu = varPrefix.toVarName();
            }
            if ((varPrefix = configVarPrefix.get("running")) != null) {
                this.running = varPrefix.toVarName();
            }
        }
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return generatorComponent.getVarMapping();
    }

    @Override
    public String getTerminalVarName() {
        return generatorComponent.getTerminalVarName();
    }

    @Override
    public String getOmegaRefPuVarName() {
        return omegaRefPu;
    }

    @Override
    public String getRunningVarName() {
        return running;
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return Arrays.asList(
                new VarConnection("omega_grp_@INDEX@", omegaPu),
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public double getWeightGen(DynawoSimulationParameters dynawoSimulationParameters) {
        double h = dynawoSimulationParameters.getModelParameters(getParameterSetId()).getDouble("generator_H");
        double sNom = dynawoSimulationParameters.getModelParameters(getParameterSetId()).getDouble("generator_SNom");
        return h * sNom;
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }
}
