/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_STATIC_REFERENCE_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.NETWORK;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.simulator.DynawoParametersDatabase;
import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;
import com.powsybl.dynawo.xml.MacroStaticReferenceXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GeneratorSynchronousFourWindingsProportionalRegulations extends AbstractBlackBoxModel {

    protected static class Parameters extends AbstractBlackBoxModel.Parameters {

        public static AbstractBlackBoxModel.Parameters load(DynawoParametersDatabase parametersDatabase, String parameterSetId) {
            Parameters parameters = new Parameters();
            parameters.setGeneratorExcitationPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_ExcitationPu").getValue());
            parameters.setGeneratorMdPuEfd(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_MdPuEfd").getValue());
            parameters.setGeneratorH(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_H").getValue());
            parameters.setGeneratorDPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_DPu").getValue());
            parameters.setGeneratorRaPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_RaPu").getValue());
            parameters.setGeneratorXlPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XlPu").getValue());
            parameters.setGeneratorXdPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XdPu").getValue());
            parameters.setGeneratorXpdPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XpdPu").getValue());
            parameters.setGeneratorXppdPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XppdPu").getValue());
            parameters.setGeneratorTpd0(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tpd0").getValue());
            parameters.setGeneratorTppd0(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tppd0").getValue());
            parameters.setGeneratorXqPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XqPu").getValue());
            parameters.setGeneratorXpqPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XpqPu").getValue());
            parameters.setGeneratorXppqPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XppqPu").getValue());
            parameters.setGeneratorTpq0(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tpq0").getValue());
            parameters.setGeneratorTppq0(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tppq0").getValue());
            parameters.setGeneratorUNom(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UNom").getValue());
            parameters.setGeneratorSNom(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_SNom").getValue());
            parameters.setGeneratorPNomTurb(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_PNomTurb").getValue());
            parameters.setGeneratorPNomAlt(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_PNomAlt").getValue());
            parameters.setGeneratorSnTfo(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_SnTfo").getValue());
            parameters.setGeneratorUNomHV(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UNomHV").getValue());
            parameters.setGeneratorUNomLV(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UNomLV").getValue());
            parameters.setGeneratorUBaseHV(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UBaseHV").getValue());
            parameters.setGeneratorUBaseLV(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UBaseLV").getValue());
            parameters.setGeneratorRTfPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_RTfPu").getValue());
            parameters.setGeneratorXTfPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XTfPu").getValue());
            parameters.setVoltageRegulatorLagEfdMax(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_LagEfdMax").getValue());
            parameters.setVoltageRegulatorLagEfdMin(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_LagEfdMin").getValue());
            parameters.setVoltageRegulatorEfdMinPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_EfdMinPu").getValue());
            parameters.setVoltageRegulatorEfdMaxPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_EfdMaxPu").getValue());
            parameters.setVoltageRegulatorUsRefMinPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_UsRefMinPu").getValue());
            parameters.setVoltageRegulatorUsRefMaxPu(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_UsRefMaxPu").getValue());
            parameters.setVoltageRegulatorGain(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_Gain").getValue());
            parameters.setGovernorKGover(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_KGover").getValue());
            parameters.setGovernorPMin(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_PMin").getValue());
            parameters.setGovernorPMax(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_PMax").getValue());
            parameters.setGovernorPNom(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_PNom").getValue());
            parameters.setURefValueIn(parametersDatabase.getParameterSet(parameterSetId).getParameter("URef_ValueIn").getValue());
            parameters.setPmValueIn(parametersDatabase.getParameterSet(parameterSetId).getParameter("Pm_ValueIn").getValue());
            return parameters;
        }

        public String getGeneratorExcitationPu() {
            return generatorExcitationPu;
        }

        public void setGeneratorExcitationPu(String generatorExcitationPu) {
            this.generatorExcitationPu = generatorExcitationPu;
        }

        public String getGeneratorMdPuEfd() {
            return generatorMdPuEfd;
        }

        public void setGeneratorMdPuEfd(String generatorMdPuEfd) {
            this.generatorMdPuEfd = generatorMdPuEfd;
        }

        public String getGeneratorH() {
            return generatorH;
        }

        public void setGeneratorH(String generatorH) {
            this.generatorH = generatorH;
        }

        public String getGeneratorDPu() {
            return generatorDPu;
        }

        public void setGeneratorDPu(String generatorDPu) {
            this.generatorDPu = generatorDPu;
        }

        public String getGeneratorRaPu() {
            return generatorRaPu;
        }

        public void setGeneratorRaPu(String generatorRaPu) {
            this.generatorRaPu = generatorRaPu;
        }

        public String getGeneratorXlPu() {
            return generatorXlPu;
        }

        public void setGeneratorXlPu(String generatorXlPu) {
            this.generatorXlPu = generatorXlPu;
        }

        public String getGeneratorXdPu() {
            return generatorXdPu;
        }

        public void setGeneratorXdPu(String generatorXdPu) {
            this.generatorXdPu = generatorXdPu;
        }

        public String getGeneratorXpdPu() {
            return generatorXpdPu;
        }

        public void setGeneratorXpdPu(String generatorXpdPu) {
            this.generatorXpdPu = generatorXpdPu;
        }

        public String getGeneratorXppdPu() {
            return generatorXppdPu;
        }

        public void setGeneratorXppdPu(String generatorXppdPu) {
            this.generatorXppdPu = generatorXppdPu;
        }

        public String getGeneratorTpd0() {
            return generatorTpd0;
        }

        public void setGeneratorTpd0(String generatorTpd0) {
            this.generatorTpd0 = generatorTpd0;
        }

        public String getGeneratorTppd0() {
            return generatorTppd0;
        }

        public void setGeneratorTppd0(String generatorTppd0) {
            this.generatorTppd0 = generatorTppd0;
        }

        public String getGeneratorXqPu() {
            return generatorXqPu;
        }

        public void setGeneratorXqPu(String generatorXqPu) {
            this.generatorXqPu = generatorXqPu;
        }

        public String getGeneratorXpqPu() {
            return generatorXpqPu;
        }

        public void setGeneratorXpqPu(String generatorXpqPu) {
            this.generatorXpqPu = generatorXpqPu;
        }

        public String getGeneratorXppqPu() {
            return generatorXppqPu;
        }

        public void setGeneratorXppqPu(String generatorXppqPu) {
            this.generatorXppqPu = generatorXppqPu;
        }

        public String getGeneratorTpq0() {
            return generatorTpq0;
        }

        public void setGeneratorTpq0(String generatorTpq0) {
            this.generatorTpq0 = generatorTpq0;
        }

        public String getGeneratorTppq0() {
            return generatorTppq0;
        }

        public void setGeneratorTppq0(String generatorTppq0) {
            this.generatorTppq0 = generatorTppq0;
        }

        public String getGeneratorUNom() {
            return generatorUNom;
        }

        public void setGeneratorUNom(String generatorUNom) {
            this.generatorUNom = generatorUNom;
        }

        public String getGeneratorSNom() {
            return generatorSNom;
        }

        public void setGeneratorSNom(String generatorSNom) {
            this.generatorSNom = generatorSNom;
        }

        public String getGeneratorPNomTurb() {
            return generatorPNomTurb;
        }

        public void setGeneratorPNomTurb(String generatorPNomTurb) {
            this.generatorPNomTurb = generatorPNomTurb;
        }

        public String getGeneratorPNomAlt() {
            return generatorPNomAlt;
        }

        public void setGeneratorPNomAlt(String generatorPNomAlt) {
            this.generatorPNomAlt = generatorPNomAlt;
        }

        public String getGeneratorSnTfo() {
            return generatorSnTfo;
        }

        public void setGeneratorSnTfo(String generatorSnTfo) {
            this.generatorSnTfo = generatorSnTfo;
        }

        public String getGeneratorUNomHV() {
            return generatorUNomHV;
        }

        public void setGeneratorUNomHV(String generatorUNomHV) {
            this.generatorUNomHV = generatorUNomHV;
        }

        public String getGeneratorUNomLV() {
            return generatorUNomLV;
        }

        public void setGeneratorUNomLV(String generatorUNomLV) {
            this.generatorUNomLV = generatorUNomLV;
        }

        public String getGeneratorUBaseHV() {
            return generatorUBaseHV;
        }

        public void setGeneratorUBaseHV(String generatorUBaseHV) {
            this.generatorUBaseHV = generatorUBaseHV;
        }

        public String getGeneratorUBaseLV() {
            return generatorUBaseLV;
        }

        public void setGeneratorUBaseLV(String generatorUBaseLV) {
            this.generatorUBaseLV = generatorUBaseLV;
        }

        public String getGeneratorRTfPu() {
            return generatorRTfPu;
        }

        public void setGeneratorRTfPu(String generatorRTfPu) {
            this.generatorRTfPu = generatorRTfPu;
        }

        public String getGeneratorXTfPu() {
            return generatorXTfPu;
        }

        public void setGeneratorXTfPu(String generatorXTfPu) {
            this.generatorXTfPu = generatorXTfPu;
        }

        public String getVoltageRegulatorLagEfdMax() {
            return voltageRegulatorLagEfdMax;
        }

        public void setVoltageRegulatorLagEfdMax(String voltageRegulatorLagEfdMax) {
            this.voltageRegulatorLagEfdMax = voltageRegulatorLagEfdMax;
        }

        public String getVoltageRegulatorLagEfdMin() {
            return voltageRegulatorLagEfdMin;
        }

        public void setVoltageRegulatorLagEfdMin(String voltageRegulatorLagEfdMin) {
            this.voltageRegulatorLagEfdMin = voltageRegulatorLagEfdMin;
        }

        public String getVoltageRegulatorEfdMinPu() {
            return voltageRegulatorEfdMinPu;
        }

        public void setVoltageRegulatorEfdMinPu(String voltageRegulatorEfdMinPu) {
            this.voltageRegulatorEfdMinPu = voltageRegulatorEfdMinPu;
        }

        public String getVoltageRegulatorEfdMaxPu() {
            return voltageRegulatorEfdMaxPu;
        }

        public void setVoltageRegulatorEfdMaxPu(String voltageRegulatorEfdMaxPu) {
            this.voltageRegulatorEfdMaxPu = voltageRegulatorEfdMaxPu;
        }

        public String getVoltageRegulatorUsRefMinPu() {
            return voltageRegulatorUsRefMinPu;
        }

        public void setVoltageRegulatorUsRefMinPu(String voltageRegulatorUsRefMinPu) {
            this.voltageRegulatorUsRefMinPu = voltageRegulatorUsRefMinPu;
        }

        public String getVoltageRegulatorUsRefMaxPu() {
            return voltageRegulatorUsRefMaxPu;
        }

        public void setVoltageRegulatorUsRefMaxPu(String voltageRegulatorUsRefMaxPu) {
            this.voltageRegulatorUsRefMaxPu = voltageRegulatorUsRefMaxPu;
        }

        public String getVoltageRegulatorGain() {
            return voltageRegulatorGain;
        }

        public void setVoltageRegulatorGain(String voltageRegulatorGain) {
            this.voltageRegulatorGain = voltageRegulatorGain;
        }

        public String getGovernorKGover() {
            return governorKGover;
        }

        public void setGovernorKGover(String governorKGover) {
            this.governorKGover = governorKGover;
        }

        public String getGovernorPMin() {
            return governorPMin;
        }

        public void setGovernorPMin(String governorPMin) {
            this.governorPMin = governorPMin;
        }

        public String getGovernorPMax() {
            return governorPMax;
        }

        public void setGovernorPMax(String governorPMax) {
            this.governorPMax = governorPMax;
        }

        public String getGovernorPNom() {
            return governorPNom;
        }

        public void setGovernorPNom(String governorPNom) {
            this.governorPNom = governorPNom;
        }

        public String getURefValueIn() {
            return uRefValueIn;
        }

        public void setURefValueIn(String uRefValueIn) {
            this.uRefValueIn = uRefValueIn;
        }

        public String getPmValueIn() {
            return pmValueIn;
        }

        public void setPmValueIn(String pmValueIn) {
            this.pmValueIn = pmValueIn;
        }

        private String generatorExcitationPu;
        private String generatorMdPuEfd;
        private String generatorH;
        private String generatorDPu;
        private String generatorRaPu;
        private String generatorXlPu;
        private String generatorXdPu;
        private String generatorXpdPu;
        private String generatorXppdPu;
        private String generatorTpd0;
        private String generatorTppd0;
        private String generatorXqPu;
        private String generatorXpqPu;
        private String generatorXppqPu;
        private String generatorTpq0;
        private String generatorTppq0;
        private String generatorUNom;
        private String generatorSNom;
        private String generatorPNomTurb;
        private String generatorPNomAlt;
        private String generatorSnTfo;
        private String generatorUNomHV;
        private String generatorUNomLV;
        private String generatorUBaseHV;
        private String generatorUBaseLV;
        private String generatorRTfPu;
        private String generatorXTfPu;
        private String voltageRegulatorLagEfdMax;
        private String voltageRegulatorLagEfdMin;
        private String voltageRegulatorEfdMinPu;
        private String voltageRegulatorEfdMaxPu;
        private String voltageRegulatorUsRefMinPu;
        private String voltageRegulatorUsRefMaxPu;
        private String voltageRegulatorGain;
        private String governorKGover;
        private String governorPMin;
        private String governorPMax;
        private String governorPNom;
        private String uRefValueIn;
        private String pmValueIn;
    }

    public GeneratorSynchronousFourWindingsProportionalRegulations(String dynamicModelId, String staticId, String parameterSetId) {
        this(dynamicModelId, staticId, parameterSetId, null);
    }

    public GeneratorSynchronousFourWindingsProportionalRegulations(String dynamicModelId, String staticId, String parameterSetId, AbstractBlackBoxModel.Parameters parameters) {
        super(dynamicModelId, staticId, parameterSetId, parameters);
    }

    @Override
    public String getLib() {
        return "GeneratorSynchronousFourWindingsProportionalRegulations";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroStaticReference object
            writer.writeStartElement(DYN_URI, "macroStaticReference");
            writer.writeAttribute("id", MACRO_STATIC_REFERENCE_PREFIX + getLib());
            MacroStaticReferenceXml.writeStaticRef(writer, "generator_PGenPu", "p");
            MacroStaticReferenceXml.writeStaticRef(writer, "generator_QGenPu", "q");
            MacroStaticReferenceXml.writeStaticRef(writer, "generator_state", "state");
            writer.writeEndElement();

            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            MacroConnectorXml.writeConnect(writer, "generator_terminal", "@STATIC_ID@@NODE@_ACPIN");
            MacroConnectorXml.writeConnect(writer, "generator_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
            writer.writeEndElement();
        }

        // Write the blackBoxModel object
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
        writer.writeAttribute("staticId", getStaticId());
        MacroStaticReferenceXml.writeMacroStaticRef(writer, MACRO_STATIC_REFERENCE_PREFIX + getLib());
        writer.writeEndElement();

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getDynamicModelId(), NETWORK);
    }
}
