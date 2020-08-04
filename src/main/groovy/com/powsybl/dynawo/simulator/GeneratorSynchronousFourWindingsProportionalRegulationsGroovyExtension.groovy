/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawo.dyd.GeneratorSynchronousFourWindingsProportionalRegulations

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>GeneratorSynchronousFourWindingsProportionalRegulations</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorSynchronousFourWindingsProportionalRegulationsGroovyExtension implements DynamicModelGroovyExtension {

        static class ParametersSpec {
            Integer generator_ExcitationPu
            Double generator_MdPuEfd
            Double generator_H
            Double generator_DPu
            Double generator_RaPu
            Double generator_XlPu
            Double generator_XdPu
            Double generator_XpdPu
            Double generator_XppdPu
            Double generator_Tpd0
            Double generator_Tppd0
            Double generator_XqPu
            Double generator_XpqPu
            Double generator_XppqPu
            Double generator_Tpq0
            Double generator_Tppq0
            Double generator_UNom
            Double generator_SNom
            Double generator_PNomTurb
            Double generator_PNomAlt
            Double generator_SnTfo
            Double generator_UNomHV
            Double generator_UNomLV
            Double generator_UBaseHV
            Double generator_UBaseLV
            Double generator_RTfPu
            Double generator_XTfPu
            Double voltageRegulator_LagEfdMax
            Double voltageRegulator_LagEfdMin
            Double voltageRegulator_EfdMinPu
            Double voltageRegulator_EfdMaxPu
            Double voltageRegulator_UsRefMinPu
            Double voltageRegulator_UsRefMaxPu
            Double voltageRegulator_Gain
            Double governor_KGover
            Double governor_PMin
            Double governor_PMax
            Double governor_PNom
            Double URef_ValueIn
            Double Pm_ValueIn

            void generator_ExcitationPu(int generator_ExcitationPu) {
                this.generator_ExcitationPu = generator_ExcitationPu
            }

            void generator_MdPuEfd(double generator_MdPuEfd) {
                this.generator_MdPuEfd = generator_MdPuEfd
            }

            void generator_H(double generator_H) {
                this.generator_H = generator_H
            }

            void generator_DPu(double generator_DPu) {
                this.generator_DPu = generator_DPu
            }

            void generator_RaPu(double generator_RaPu) {
                this.generator_RaPu = generator_RaPu
            }

            void generator_XlPu(double generator_XlPu) {
                this.generator_XlPu = generator_XlPu
            }

            void generator_XdPu(double generator_XdPu) {
                this.generator_XdPu = generator_XdPu
            }

            void generator_XpdPu(double generator_XpdPu) {
                this.generator_XpdPu = generator_XpdPu
            }

            void generator_XppdPu(double generator_XppdPu) {
                this.generator_XppdPu = generator_XppdPu
            }

            void generator_Tpd0(double generator_Tpd0) {
                this.generator_Tpd0 = generator_Tpd0
            }

            void generator_Tppd0(double generator_Tppd0) {
                this.generator_Tppd0 = generator_Tppd0
            }

            void generator_XqPu(double generator_XqPu) {
                this.generator_XqPu = generator_XqPu
            }

            void generator_XpqPu(double generator_XpqPu) {
                this.generator_XpqPu = generator_XpqPu
            }

            void generator_XppqPu(double generator_XppqPu) {
                this.generator_XppqPu = generator_XppqPu
            }

            void generator_Tpq0(double generator_Tpq0) {
                this.generator_Tpq0 = generator_Tpq0
            }

            void generator_Tppq0(double generator_Tppq0) {
                this.generator_Tppq0 = generator_Tppq0
            }

            void generator_UNom(double generator_UNom) {
                this.generator_UNom = generator_UNom
            }

            void generator_SNom(double generator_SNom) {
                this.generator_SNom = generator_SNom
            }

            void generator_PNomTurb(double generator_PNomTurb) {
                this.generator_PNomTurb = generator_PNomTurb
            }

            void generator_PNomAlt(double generator_PNomAlt) {
                this.generator_PNomAlt = generator_PNomAlt
            }

            void generator_SnTfo(double generator_SnTfo) {
                this.generator_SnTfo = generator_SnTfo
            }

            void generator_UNomHV(double generator_UNomHV) {
                this.generator_UNomHV = generator_UNomHV
            }

            void generator_UNomLV(double generator_UNomLV) {
                this.generator_UNomLV = generator_UNomLV
            }

            void generator_UBaseHV(double generator_UBaseHV) {
                this.generator_UBaseHV = generator_UBaseHV
            }

            void generator_UBaseLV(double generator_UBaseLV) {
                this.generator_UBaseLV = generator_UBaseLV
            }

            void generator_RTfPu(double generator_RTfPu) {
                this.generator_RTfPu = generator_RTfPu
            }

            void generator_XTfPu(double generator_XTfPu) {
                this.generator_XTfPu = generator_XTfPu
            }

            void voltageRegulator_LagEfdMax(double voltageRegulator_LagEfdMax) {
                this.voltageRegulator_LagEfdMax = voltageRegulator_LagEfdMax
            }

            void voltageRegulator_LagEfdMin(double voltageRegulator_LagEfdMin) {
                this.voltageRegulator_LagEfdMin = voltageRegulator_LagEfdMin
            }

            void voltageRegulator_EfdMinPu(double voltageRegulator_EfdMinPu) {
                this.voltageRegulator_EfdMinPu = voltageRegulator_EfdMinPu
            }

            void voltageRegulator_EfdMaxPu(double voltageRegulator_EfdMaxPu) {
                this.voltageRegulator_EfdMaxPu = voltageRegulator_EfdMaxPu
            }

            void voltageRegulator_UsRefMinPu(double voltageRegulator_UsRefMinPu) {
                this.voltageRegulator_UsRefMinPu = voltageRegulator_UsRefMinPu
            }

            void voltageRegulator_UsRefMaxPu(double voltageRegulator_UsRefMaxPu) {
                this.voltageRegulator_UsRefMaxPu = voltageRegulator_UsRefMaxPu
            }

            void voltageRegulator_Gain(double voltageRegulator_Gain) {
                this.voltageRegulator_Gain = voltageRegulator_Gain
            }

            void governor_KGover(double governor_KGover) {
                this.governor_KGover = governor_KGover
            }

            void governor_PMin(double governor_PMin) {
                this.governor_PMin = governor_PMin
            }

            void governor_PMax(double governor_PMax) {
                this.governor_PMax = governor_PMax
            }

            void governor_PNom(double governor_PNom) {
                this.governor_PNom = governor_PNom
            }

            void URef_ValueIn(double URef_ValueIn) {
                this.URef_ValueIn = URef_ValueIn
            }

            void Pm_ValueIn(double Pm_ValueIn) {
                this.Pm_ValueIn = Pm_ValueIn
            }
        }

    static class GeneratorSynchronousFourWindingsProportionalRegulationsSpec {
        String dynamicModelId
        String staticId
        String parameterSetId

        final ParametersSpec parametersSpec = new ParametersSpec()

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }

        void parameters(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parametersSpec
            cloned()
        }
    }

    String getName() {
        return "dynawo"
    }
    
    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.GeneratorSynchronousFourWindingsProportionalRegulations = { Closure<Void> closure ->
            def cloned = closure.clone()
            GeneratorSynchronousFourWindingsProportionalRegulationsSpec generatorSynchronousFourWindingsProportionalRegulationsSpec = new GeneratorSynchronousFourWindingsProportionalRegulationsSpec()

            cloned.delegate = generatorSynchronousFourWindingsProportionalRegulationsSpec
            cloned()

            if (!generatorSynchronousFourWindingsProportionalRegulationsSpec.staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!generatorSynchronousFourWindingsProportionalRegulationsSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            //GeneratorSynchronousFourWindingsProportionalRegulations.Parameters parameters = GeneratorSynchronousFourWindingsProportionalRegulations.Parameters.load(parametersDB, generatorSynchronousFourWindingsProportionalRegulationsSpec.parameterSetId)
            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_ExcitationPu) {
                //parameters.setGeneratorExcitationPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_ExcitationPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_MdPuEfd) {
                //parameters.setGeneratorMdPuEfd(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_MdPuEfd)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_H) {
                //parameters.setGeneratorH(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_H)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_DPu) {
                //parameters.setGeneratorDPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_DPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_RaPu) {
                //parameters.setGeneratorRaPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_RaPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XlPu) {
                //parameters.setGeneratorXlPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XlPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XdPu) {
                //parameters.setGeneratorXdPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XdPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XpdPu) {
                //parameters.setGeneratorXpdPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XpdPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XppdPu) {
                //parameters.setGeneratorXppdPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XppdPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tpd0) {
                //parameters.setGeneratorTpd0(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tpd0)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tppd0) {
                //parameters.setGeneratorTppd0(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tppd0)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XqPu) {
                //parameters.setGeneratorXqPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XqPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XpqPu) {
                //parameters.setGeneratorXpqPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XpqPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XppqPu) {
                //parameters.setGeneratorXppqPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XppqPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tpq0) {
                //parameters.setGeneratorTpq0(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tpq0)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tppq0) {
                //parameters.setGeneratorTppq0(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_Tppq0)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UNom) {
                //parameters.setGeneratorUNom(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UNom)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_SNom) {
                //parameters.setGeneratorSNom(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_SNom)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_PNomTurb) {
                //parameters.setGeneratorPNomTurb(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_PNomTurb)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_PNomAlt) {
                //parameters.setGeneratorPNomAlt(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_PNomAlt)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_SnTfo) {
                //parameters.setGeneratorSnTfo(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_SnTfo)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UNomHV) {
                //parameters.setGeneratorUNomHV(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UNomHV)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UNomLV) {
                //parameters.setGeneratorUNomLV(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UNomLV)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UBaseHV) {
                //parameters.setGeneratorUBaseHV(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UBaseHV)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UBaseLV) {
                //parameters.setGeneratorUBaseLV(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_UBaseLV)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_RTfPu) {
                //parameters.setGeneratorRTfPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_RTfPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XTfPu) {
                //parameters.setGeneratorXTfPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.generator_XTfPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_LagEfdMax) {
                //parameters.setVoltageRegulatorLagEfdMax(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_LagEfdMax)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_LagEfdMin) {
                //parameters.setVoltageRegulatorLagEfdMin(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_LagEfdMin)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_EfdMinPu) {
                //parameters.setVoltageRegulatorEfdMinPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_EfdMinPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_EfdMaxPu) {
                //parameters.setVoltageRegulatorEfdMaxPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_EfdMaxPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_UsRefMinPu) {
                //parameters.setVoltageRegulatorUsRefMinPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_UsRefMinPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_UsRefMaxPu) {
                //parameters.setVoltageRegulatorUsRefMaxPu(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_UsRefMaxPu)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_Gain) {
                //parameters.setVoltageRegulatorGain(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.voltageRegulator_Gain)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_KGover) {
                //parameters.setGovernorKGover(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_KGover)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_PMin) {
                //parameters.setGovernorPMin(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_PMin)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_PMax) {
                //parameters.setGovernorPMax(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_PMax)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_PNom) {
                //parameters.setGovernorPNom(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.governor_PNom)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.URef_ValueIn) {
                //parameters.setURefValueIn(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.URef_ValueIn)
            }

            if (generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.Pm_ValueIn) {
                //parameters.setPmValueIn(generatorSynchronousFourWindingsProportionalRegulationsSpec.parametersSpec.Pm_ValueIn)
            }

            String dynamicModelId = generatorSynchronousFourWindingsProportionalRegulationsSpec.dynamicModelId ? generatorSynchronousFourWindingsProportionalRegulationsSpec.dynamicModelId : generatorSynchronousFourWindingsProportionalRegulationsSpec.staticId
            consumer.accept(new GeneratorSynchronousFourWindingsProportionalRegulations(dynamicModelId, generatorSynchronousFourWindingsProportionalRegulationsSpec.staticId, generatorSynchronousFourWindingsProportionalRegulationsSpec.parameterSetId))
        }
    }

}
