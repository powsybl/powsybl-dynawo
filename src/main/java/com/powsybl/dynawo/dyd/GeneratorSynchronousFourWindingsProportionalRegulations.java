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

    public static class Parameters extends AbstractBlackBoxModel.Parameters {

        public static AbstractBlackBoxModel.Parameters load(DynawoParametersDatabase parametersDatabase, String parameterSetId) {
            Parameters parameters = new Parameters();
            parameters.setGeneratorExcitationPu(Integer.parseInt(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_ExcitationPu").getValue()));
            parameters.setGeneratorMdPuEfd(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_MdPuEfd").getValue()));
            parameters.setGeneratorH(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_H").getValue()));
            parameters.setGeneratorDPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_DPu").getValue()));
            parameters.setGeneratorRaPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_RaPu").getValue()));
            parameters.setGeneratorXlPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XlPu").getValue()));
            parameters.setGeneratorXdPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XdPu").getValue()));
            parameters.setGeneratorXpdPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XpdPu").getValue()));
            parameters.setGeneratorXppdPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XppdPu").getValue()));
            parameters.setGeneratorTpd0(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tpd0").getValue()));
            parameters.setGeneratorTppd0(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tppd0").getValue()));
            parameters.setGeneratorXqPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XqPu").getValue()));
            parameters.setGeneratorXpqPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XpqPu").getValue()));
            parameters.setGeneratorXppqPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XppqPu").getValue()));
            parameters.setGeneratorTpq0(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tpq0").getValue()));
            parameters.setGeneratorTppq0(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_Tppq0").getValue()));
            parameters.setGeneratorUNom(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UNom").getValue()));
            parameters.setGeneratorSNom(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_SNom").getValue()));
            parameters.setGeneratorPNomTurb(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_PNomTurb").getValue()));
            parameters.setGeneratorPNomAlt(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_PNomAlt").getValue()));
            parameters.setGeneratorSnTfo(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_SnTfo").getValue()));
            parameters.setGeneratorUNomHV(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UNomHV").getValue()));
            parameters.setGeneratorUNomLV(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UNomLV").getValue()));
            parameters.setGeneratorUBaseHV(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UBaseHV").getValue()));
            parameters.setGeneratorUBaseLV(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_UBaseLV").getValue()));
            parameters.setGeneratorRTfPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_RTfPu").getValue()));
            parameters.setGeneratorXTfPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("generator_XTfPu").getValue()));
            parameters.setVoltageRegulatorLagEfdMax(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_LagEfdMax").getValue()));
            parameters.setVoltageRegulatorLagEfdMin(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_LagEfdMin").getValue()));
            parameters.setVoltageRegulatorEfdMinPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_EfdMinPu").getValue()));
            parameters.setVoltageRegulatorEfdMaxPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_EfdMaxPu").getValue()));
            parameters.setVoltageRegulatorUsRefMinPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_UsRefMinPu").getValue()));
            parameters.setVoltageRegulatorUsRefMaxPu(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_UsRefMaxPu").getValue()));
            parameters.setVoltageRegulatorGain(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("voltageRegulator_Gain").getValue()));
            parameters.setGovernorKGover(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_KGover").getValue()));
            parameters.setGovernorPMin(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_PMin").getValue()));
            parameters.setGovernorPMax(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_PMax").getValue()));
            parameters.setGovernorPNom(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("governor_PNom").getValue()));
            parameters.setURefValueIn(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("URef_ValueIn").getValue()));
            parameters.setPmValueIn(Double.parseDouble(parametersDatabase.getParameterSet(parameterSetId).getParameter("Pm_ValueIn").getValue()));
            return parameters;
        }

        public int getGeneratorExcitationPu() {
            return generatorExcitationPu;
        }

        public void setGeneratorExcitationPu(int generatorExcitationPu) {
            this.generatorExcitationPu = generatorExcitationPu;
        }

        public double getGeneratorMdPuEfd() {
            return generatorMdPuEfd;
        }

        public void setGeneratorMdPuEfd(double generatorMdPuEfd) {
            this.generatorMdPuEfd = generatorMdPuEfd;
        }

        public double getGeneratorH() {
            return generatorH;
        }

        public void setGeneratorH(double generatorH) {
            this.generatorH = generatorH;
        }

        public double getGeneratorDPu() {
            return generatorDPu;
        }

        public void setGeneratorDPu(double generatorDPu) {
            this.generatorDPu = generatorDPu;
        }

        public double getGeneratorRaPu() {
            return generatorRaPu;
        }

        public void setGeneratorRaPu(double generatorRaPu) {
            this.generatorRaPu = generatorRaPu;
        }

        public double getGeneratorXlPu() {
            return generatorXlPu;
        }

        public void setGeneratorXlPu(double generatorXlPu) {
            this.generatorXlPu = generatorXlPu;
        }

        public double getGeneratorXdPu() {
            return generatorXdPu;
        }

        public void setGeneratorXdPu(double generatorXdPu) {
            this.generatorXdPu = generatorXdPu;
        }

        public double getGeneratorXpdPu() {
            return generatorXpdPu;
        }

        public void setGeneratorXpdPu(double generatorXpdPu) {
            this.generatorXpdPu = generatorXpdPu;
        }

        public double getGeneratorXppdPu() {
            return generatorXppdPu;
        }

        public void setGeneratorXppdPu(double generatorXppdPu) {
            this.generatorXppdPu = generatorXppdPu;
        }

        public double getGeneratorTpd0() {
            return generatorTpd0;
        }

        public void setGeneratorTpd0(double generatorTpd0) {
            this.generatorTpd0 = generatorTpd0;
        }

        public double getGeneratorTppd0() {
            return generatorTppd0;
        }

        public void setGeneratorTppd0(double generatorTppd0) {
            this.generatorTppd0 = generatorTppd0;
        }

        public double getGeneratorXqPu() {
            return generatorXqPu;
        }

        public void setGeneratorXqPu(double generatorXqPu) {
            this.generatorXqPu = generatorXqPu;
        }

        public double getGeneratorXpqPu() {
            return generatorXpqPu;
        }

        public void setGeneratorXpqPu(double generatorXpqPu) {
            this.generatorXpqPu = generatorXpqPu;
        }

        public double getGeneratorXppqPu() {
            return generatorXppqPu;
        }

        public void setGeneratorXppqPu(double generatorXppqPu) {
            this.generatorXppqPu = generatorXppqPu;
        }

        public double getGeneratorTpq0() {
            return generatorTpq0;
        }

        public void setGeneratorTpq0(double generatorTpq0) {
            this.generatorTpq0 = generatorTpq0;
        }

        public double getGeneratorTppq0() {
            return generatorTppq0;
        }

        public void setGeneratorTppq0(double generatorTppq0) {
            this.generatorTppq0 = generatorTppq0;
        }

        public double getGeneratorUNom() {
            return generatorUNom;
        }

        public void setGeneratorUNom(double generatorUNom) {
            this.generatorUNom = generatorUNom;
        }

        public double getGeneratorSNom() {
            return generatorSNom;
        }

        public void setGeneratorSNom(double generatorSNom) {
            this.generatorSNom = generatorSNom;
        }

        public double getGeneratorPNomTurb() {
            return generatorPNomTurb;
        }

        public void setGeneratorPNomTurb(double generatorPNomTurb) {
            this.generatorPNomTurb = generatorPNomTurb;
        }

        public double getGeneratorPNomAlt() {
            return generatorPNomAlt;
        }

        public void setGeneratorPNomAlt(double generatorPNomAlt) {
            this.generatorPNomAlt = generatorPNomAlt;
        }

        public double getGeneratorSnTfo() {
            return generatorSnTfo;
        }

        public void setGeneratorSnTfo(double generatorSnTfo) {
            this.generatorSnTfo = generatorSnTfo;
        }

        public double getGeneratorUNomHV() {
            return generatorUNomHV;
        }

        public void setGeneratorUNomHV(double generatorUNomHV) {
            this.generatorUNomHV = generatorUNomHV;
        }

        public double getGeneratorUNomLV() {
            return generatorUNomLV;
        }

        public void setGeneratorUNomLV(double generatorUNomLV) {
            this.generatorUNomLV = generatorUNomLV;
        }

        public double getGeneratorUBaseHV() {
            return generatorUBaseHV;
        }

        public void setGeneratorUBaseHV(double generatorUBaseHV) {
            this.generatorUBaseHV = generatorUBaseHV;
        }

        public double getGeneratorUBaseLV() {
            return generatorUBaseLV;
        }

        public void setGeneratorUBaseLV(double generatorUBaseLV) {
            this.generatorUBaseLV = generatorUBaseLV;
        }

        public double getGeneratorRTfPu() {
            return generatorRTfPu;
        }

        public void setGeneratorRTfPu(double generatorRTfPu) {
            this.generatorRTfPu = generatorRTfPu;
        }

        public double getGeneratorXTfPu() {
            return generatorXTfPu;
        }

        public void setGeneratorXTfPu(double generatorXTfPu) {
            this.generatorXTfPu = generatorXTfPu;
        }

        public double getVoltageRegulatorLagEfdMax() {
            return voltageRegulatorLagEfdMax;
        }

        public void setVoltageRegulatorLagEfdMax(double voltageRegulatorLagEfdMax) {
            this.voltageRegulatorLagEfdMax = voltageRegulatorLagEfdMax;
        }

        public double getVoltageRegulatorLagEfdMin() {
            return voltageRegulatorLagEfdMin;
        }

        public void setVoltageRegulatorLagEfdMin(double voltageRegulatorLagEfdMin) {
            this.voltageRegulatorLagEfdMin = voltageRegulatorLagEfdMin;
        }

        public double getVoltageRegulatorEfdMinPu() {
            return voltageRegulatorEfdMinPu;
        }

        public void setVoltageRegulatorEfdMinPu(double voltageRegulatorEfdMinPu) {
            this.voltageRegulatorEfdMinPu = voltageRegulatorEfdMinPu;
        }

        public double getVoltageRegulatorEfdMaxPu() {
            return voltageRegulatorEfdMaxPu;
        }

        public void setVoltageRegulatorEfdMaxPu(double voltageRegulatorEfdMaxPu) {
            this.voltageRegulatorEfdMaxPu = voltageRegulatorEfdMaxPu;
        }

        public double getVoltageRegulatorUsRefMinPu() {
            return voltageRegulatorUsRefMinPu;
        }

        public void setVoltageRegulatorUsRefMinPu(double voltageRegulatorUsRefMinPu) {
            this.voltageRegulatorUsRefMinPu = voltageRegulatorUsRefMinPu;
        }

        public double getVoltageRegulatorUsRefMaxPu() {
            return voltageRegulatorUsRefMaxPu;
        }

        public void setVoltageRegulatorUsRefMaxPu(double voltageRegulatorUsRefMaxPu) {
            this.voltageRegulatorUsRefMaxPu = voltageRegulatorUsRefMaxPu;
        }

        public double getVoltageRegulatorGain() {
            return voltageRegulatorGain;
        }

        public void setVoltageRegulatorGain(double voltageRegulatorGain) {
            this.voltageRegulatorGain = voltageRegulatorGain;
        }

        public double getGovernorKGover() {
            return governorKGover;
        }

        public void setGovernorKGover(double governorKGover) {
            this.governorKGover = governorKGover;
        }

        public double getGovernorPMin() {
            return governorPMin;
        }

        public void setGovernorPMin(double governorPMin) {
            this.governorPMin = governorPMin;
        }

        public double getGovernorPMax() {
            return governorPMax;
        }

        public void setGovernorPMax(double governorPMax) {
            this.governorPMax = governorPMax;
        }

        public double getGovernorPNom() {
            return governorPNom;
        }

        public void setGovernorPNom(double governorPNom) {
            this.governorPNom = governorPNom;
        }

        public double getURefValueIn() {
            return uRefValueIn;
        }

        public void setURefValueIn(double uRefValueIn) {
            this.uRefValueIn = uRefValueIn;
        }

        public double getPmValueIn() {
            return pmValueIn;
        }

        public void setPmValueIn(double pmValueIn) {
            this.pmValueIn = pmValueIn;
        }

        private int generatorExcitationPu;
        private double generatorMdPuEfd;
        private double generatorH;
        private double generatorDPu;
        private double generatorRaPu;
        private double generatorXlPu;
        private double generatorXdPu;
        private double generatorXpdPu;
        private double generatorXppdPu;
        private double generatorTpd0;
        private double generatorTppd0;
        private double generatorXqPu;
        private double generatorXpqPu;
        private double generatorXppqPu;
        private double generatorTpq0;
        private double generatorTppq0;
        private double generatorUNom;
        private double generatorSNom;
        private double generatorPNomTurb;
        private double generatorPNomAlt;
        private double generatorSnTfo;
        private double generatorUNomHV;
        private double generatorUNomLV;
        private double generatorUBaseHV;
        private double generatorUBaseLV;
        private double generatorRTfPu;
        private double generatorXTfPu;
        private double voltageRegulatorLagEfdMax;
        private double voltageRegulatorLagEfdMin;
        private double voltageRegulatorEfdMinPu;
        private double voltageRegulatorEfdMaxPu;
        private double voltageRegulatorUsRefMinPu;
        private double voltageRegulatorUsRefMaxPu;
        private double voltageRegulatorGain;
        private double governorKGover;
        private double governorPMin;
        private double governorPMax;
        private double governorPNom;
        private double uRefValueIn;
        private double pmValueIn;
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
