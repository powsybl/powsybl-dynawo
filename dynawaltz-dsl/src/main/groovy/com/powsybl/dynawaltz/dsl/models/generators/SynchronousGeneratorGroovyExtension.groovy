/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.EnumGeneratorComponent
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator
import com.powsybl.dynawaltz.models.generators.SynchronousGenerator
import com.powsybl.dynawaltz.models.generators.SynchronousGeneratorControllable
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class SynchronousGeneratorGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String SYNCHRONOUS_GENERATORS = "synchronousGenerators"

    SynchronousGeneratorGroovyExtension() {
        super(SYNCHRONOUS_GENERATORS)
    }

    protected SynchronousGeneratorGroovyExtension(URL config) {
        super(SYNCHRONOUS_GENERATORS, config)
    }

    @Override
    protected SynchronousGeneratorBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new SynchronousGeneratorBuilder(network, equipmentConfig)
    }

    static class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder {

        SynchronousGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network, equipmentConfig)
        }

        protected EnumGeneratorComponent getGeneratorComponent() {
            def aux = equipmentConfig.hasAuxiliary()
            def transfo = equipmentConfig.hasTransformer()
            if (aux && transfo) {
                return EnumGeneratorComponent.AUXILIARY_TRANSFORMER
            } else if (transfo) {
                return EnumGeneratorComponent.TRANSFORMER
            } else if (aux) {
                throw new DslException("Generator component auxiliary without transformer is not supported")
            }
            EnumGeneratorComponent.NONE
        }

        @Override
        SynchronizedGenerator build() {
            if (isInstantiable()) {
                def epsilon = 0.0001
                if (equipment && equipment.getTerminal().isConnected() &&
                        equipment.getTerminal().getBusBreakerView().getBus().getConnectedComponent().getNum() == 0 &&
                        !equipment.getTerminal().getBusBreakerView().getBus().getV().isNaN()) {
                    if (!equipment.voltageRegulatorOn) {
                        if (!equipment.getExtensionByName("activePowerControl").isParticipate()) {
                            println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + " is out of voltage and frequency control.")
                        } else if (Math.abs(equipment.getTerminal().getP()) < epsilon) { // <=> Si Pc = 0
                            println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + " is out of voltage control with Pc == 0.")
                        } else if (equipmentConfig.lib != "GeneratorPQInfiniteLimits") {
                            // hors reglage de tension seulement
                            // Il faut remplacer le parameterSetId par un nouveau et supprimer l'ancien
                            println("Dynamic data substitution for " + dslEquipment.staticId + " (= groupPfQ because out of voltage control)")
                            new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, "GeneratorPQInfiniteLimits")
                        } else { // le groupe est inchange
                            if (equipmentConfig.isControllable()) {
                                new SynchronousGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                            } else {
                                new SynchronousGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                            }
                        }
                    } else if (!equipment.getExtensionByName("activePowerControl").isParticipate() || Math.abs(equipment.getTerminal().getP()) < epsilon) {
                        // hors reglage de frequence seulement
                        def parameterName = (equipmentConfig.lib == "GeneratorPQInfiniteLimits" || equipmentConfig.lib == "GeneratorPV") ? "generator_AlphaPuPNom" : "governor_KGover"
                        // dynaWaltzParameters.getModelParameters(getParameterSetId()).getDouble(parameterName).setValue(0.)
                        println("Parameter " + parameterName + " modified for equipment " + dslEquipment.staticId + " (= 0 because out of frequency regulation)")
                        if (equipmentConfig.isControllable()) {
                            new SynchronousGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                        } else {
                            new SynchronousGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                        }
                    } else if (equipmentConfig.lib != "GeneratorPQInfiniteLimits" && !isDiagramCoherent(equipment)) {
                        // Il faut remplacer le parameterSetId par un nouveau et supprimer l'ancien
                        println("Dynamic data substitution for equipment " + dslEquipment.staticId + " (= groupPfQ because of inconsistent diagram")
                        new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, "GeneratorPQInfiniteLimits")
                    } else { // le groupe est inchange
                        if (equipmentConfig.isControllable()) {
                            new SynchronousGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                        } else {
                            new SynchronousGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                        }
                    }
                } else {
                    if (!equipment) {
                        println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + " not present in network.")
                    } else if (!equipment.getTerminal().isConnected()) {
                        println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + " not connected.")
                    } else if (equipment.getTerminal().getBusBreakerView().getBus().getConnectedComponent().getNum() > 0) {
                        println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + " not in main connected component.")
                    } else if (equipment.getTerminal().getBusBreakerView().getBus().getV().isNaN()) {
                        println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + "'s voltage level if off.")
                    }
                    null
                }
            } else {
                null
            }
        }

        boolean isDiagramCoherent(Generator generator) {
            true
        }
    }
}
