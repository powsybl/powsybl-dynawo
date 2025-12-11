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
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;

import static com.powsybl.dynawo.models.generators.GeneratorProperties.*;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Olivier Perrin {@literal <olivier.perrin at rte-france.com>}
 */
public class SynchronousGenerator extends BaseGenerator implements FrequencySynchronizedModel {

    protected SynchronousGenerator(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig,
                isGeneratorCustom(modelConfig) ? CustomGeneratorComponent.fromModelConfig(modelConfig, EnumGeneratorComponent.createFrom(modelConfig)) :
                        new Description(EnumGeneratorComponent.createFrom(modelConfig)));
    }

    @Override
    public String getOmegaRefPuVarName() {
        return getComponentDescription().omegaRefPu();
    }

    @Override
    public String getRunningVarName() {
        return getComponentDescription().running();
    }

    public String getOmegaPuVarName() {
        return getComponentDescription().omegaPu();
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return Arrays.asList(
                new VarConnection("omega_grp_@INDEX@", getOmegaPuVarName()),
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

    static class Description extends BaseGenerator.Description implements ComponentDescription {
        protected EnumGeneratorComponent generatorComponent;

        Description(EnumGeneratorComponent generatorComponent) {
            this.generatorComponent = generatorComponent;
        }

        @Override
        public List<VarMapping> varMapping() {
            return generatorComponent.getVarMapping();
        }

        @Override
        public String terminal() {
            return generatorComponent.getTerminalVarName();
        }

        @Override
        public String omegaRefPu() {
            return DEFAULT_OMEGA_REF_PU;
        }

        @Override
        public String running() {
            return DEFAULT_RUNNING;
        }

        @Override
        public String omegaPu() {
            return DEFAULT_OMEGA_PU;
        }
    }
}
