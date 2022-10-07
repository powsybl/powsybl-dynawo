/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractGeneratorModel extends AbstractBlackBoxModel implements GeneratorModel {

    protected static final List<Pair<String, String>> VAR_MAPPING = Arrays.asList(
            Pair.of("generator_PGenPu", "p"),
            Pair.of("generator_QGenPu", "q"),
            Pair.of("generator_state", "state"));

    private final String terminalVarName;
    private final String switchOffSignalNodeVarName;
    private final String switchOffSignalEventVarName;
    private final String switchOffSignalAutomatonVarName;
    private final String omegaRefPuVarName;
    private final String runningVarName;

    protected AbstractGeneratorModel(String dynamicModelId, String staticId, String parameterSetId,
                                  String terminalVarName, String switchOffSignalNodeVarName,
                                  String switchOffSignalEventVarName, String switchOffSignalAutomatonVarName,
                                  String omegaRefPuVarName, String runningVarName) {
        super(dynamicModelId, staticId, parameterSetId);
        this.terminalVarName = terminalVarName;
        this.switchOffSignalNodeVarName = switchOffSignalNodeVarName;
        this.switchOffSignalEventVarName = switchOffSignalEventVarName;
        this.switchOffSignalAutomatonVarName = switchOffSignalAutomatonVarName;
        this.omegaRefPuVarName = omegaRefPuVarName;
        this.runningVarName = runningVarName;
    }

    @Override
    public List<Pair<String, String>> getVarsMapping() {
        return VAR_MAPPING;
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
        return terminalVarName;
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return switchOffSignalNodeVarName;
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return switchOffSignalEventVarName;
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return switchOffSignalAutomatonVarName;
    }

    @Override
    public String getOmegaRefPuVarName() {
        return omegaRefPuVarName;
    }

    @Override
    public String getRunningVarName() {
        return runningVarName;
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        writeBlackBoxModel(writer, context);
    }
}
