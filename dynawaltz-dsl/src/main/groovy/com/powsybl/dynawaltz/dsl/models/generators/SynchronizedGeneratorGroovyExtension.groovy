/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorControllable
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class SynchronizedGeneratorGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String SYNCHRONIZED_GENERATORS = "synchronizedGenerators"

    SynchronizedGeneratorGroovyExtension() {
        super(SYNCHRONIZED_GENERATORS)
    }

    protected SynchronizedGeneratorGroovyExtension(URL config) {
        super(SYNCHRONIZED_GENERATORS, config)
    }

    @Override
    protected SynchronizedGeneratorBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new SynchronizedGeneratorBuilder(network, equipmentConfig)
    }

    static class SynchronizedGeneratorBuilder extends AbstractGeneratorBuilder {

        SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network, equipmentConfig)
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
                        } else if (Math.abs(generator.getTerminal().getP()) < epsilon) { // <=> Si Pc = 0
                            println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + dslEquipment.staticId + " is out of voltage control with Pc == 0.")
                        } else if (equipmentConfig.lib != "GeneratorPQInfiniteLimits") {
                            // hors reglage de tension seulement
                            // Il faut remplacer le parameterSetId par un nouveau et supprimer l'ancien
                            println("Dynamic data substitution for " + dslEquipment.staticId + " (= groupPfQ because out of voltage control)")
                            new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, "GeneratorPQInfiniteLimits")
                        } else { // le groupe est inchange
                            if (equipmentConfig.isControllable()) {
                                new SynchronizedGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                            } else {
                                new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                            }
                        }
                    } else if (!equipment.getExtensionByName("activePowerControl").isParticipate() || Math.abs(equipment.getTerminal().getP()) < epsilon) {
                        // hors reglage de frequence seulement
                        def parameterName = (equipmentConfig.lib == "GeneratorPQInfiniteLimits" || equipmentConfig.lib == "GeneratorPV") ? "generator_AlphaPuPNom" : "governor_KGover"
                        // dynaWaltzParameters.getModelParameters(getParameterSetId()).getDouble(parameterName).setValue(0.)
                        println("Parameter " + parameterName + " modified for equipment " + dslEquipment.staticId + " (= 0 because out of frequency regulation)")
                        if (equipmentConfig.isControllable()) {
                            new SynchronizedGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                        } else {
                            new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                        }
                    } else if (equipmentConfig.lib != "GeneratorPQInfiniteLimits" && !isDiagramCoherent(equipment)) {
                        // Il faut remplacer le parameterSetId par un nouveau et supprimer l'ancien
                        println("Dynamic data substitution for equipment " + dslEquipment.staticId + " (= groupPfQ because of inconsistent diagram")
                        new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, "GeneratorPQInfiniteLimits")
                    } else { // le groupe est inchange
                        if (equipmentConfig.isControllable()) {
                            new SynchronizedGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                        } else {
                            new SynchronizedGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
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
