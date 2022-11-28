/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticref.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network.BusModel;
import com.powsybl.dynawaltz.dynamicmodels.staticref.AbstractBlackBoxModelWithStaticRef;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractGeneratorModel extends AbstractBlackBoxModelWithStaticRef implements GeneratorModel {

    protected static final List<Pair<String, String>> STATIC_REF = Arrays.asList(
            Pair.of("generator_PGenPu", "p"),
            Pair.of("generator_QGenPu", "q"),
            Pair.of("generator_state", "state"));

    private final GeneratorParameters generatorParameters;

    protected AbstractGeneratorModel(String dynamicModelId, String staticId, String parameterSetId,
                                  GeneratorParameters generatorParameters) {
        super(dynamicModelId, staticId, parameterSetId);
        this.generatorParameters = generatorParameters;
    }

    @Override
    public List<Pair<String, String>> getStaticRef() {
        return STATIC_REF;
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) {
        Generator generator = context.getNetwork().getGenerator(getStaticId());
        if (generator == null) {
            throw new PowsyblException("Generator static id unknown: " + getStaticId());
        }
        String connectedStaticId = generator.getTerminal().getBusBreakerView().getConnectableBus().getId();
        BlackBoxModel connectedBbm = context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (connectedBbm == null) {
            return List.of(context.getNetworkModel().getDefaultBusModel(connectedStaticId));
        }
        return List.of(connectedBbm);
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (!(connected instanceof BusModel)) {
            throw new PowsyblException("GeneratorModel can only connect to BusModel");
        }
        BusModel connectedBusModel = (BusModel) connected;
        return Arrays.asList(
                Pair.of(getTerminalVarName(), connectedBusModel.getTerminalVarName()),
                Pair.of(getSwitchOffSignalNodeVarName(), connectedBusModel.getSwitchOffSignalVarName())
        );
    }

    @Override
    public String getTerminalVarName() {
        return generatorParameters.getTerminalVarName();
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return generatorParameters.getSwitchOffSignalNodeVarName();
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return generatorParameters.getSwitchOffSignalEventVarName();
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return generatorParameters.getSwitchOffSignalAutomatonVarName();
    }

    @Override
    public String getRunningVarName() {
        return generatorParameters.getRunningVarName();
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writeBlackBoxModel(writer, context);
    }
}
