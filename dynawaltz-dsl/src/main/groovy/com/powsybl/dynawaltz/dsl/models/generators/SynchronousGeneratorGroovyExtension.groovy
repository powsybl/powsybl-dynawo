/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
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

        EquipmentConfig equipmentConfig

        SynchronousGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network)
            this.equipmentConfig = equipmentConfig
        }

        SynchronizedGenerator build() {
            checkData()
            def epsilon = 0.0001
            if (generator && generator.getTerminal().isConnected() &&
                    generator.getTerminal().getBusBreakerView().getBus().getConnectedComponent().getNum() == 0 &&
                    !generator.getTerminal().getBusBreakerView().getBus().getV().isNaN()) {
                if (!generator.voltageRegulatorOn) {
                    if (!generator.getExtensionByName("activePowerControl").isParticipate()) {
                        println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " is out of voltage and frequency control.")
                    } else if (Math.abs(generator.getTerminal().getP()) < epsilon) { // <=> Si Pc = 0
                        println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " is out of voltage control with Pc == 0.")
                    } else if (equipmentConfig.lib != "GeneratorPQInfiniteLimits") { // hors reglage de tension seulement
                        // Il faut remplacer le parameterSetId par un nouveau et supprimer l'ancien
                        println("Dynamic data substitution for " + staticId + " (= groupPfQ because out of voltage control)")
                        new SynchronizedGenerator(dynamicModelId, generator, parameterSetId, "GeneratorPQInfiniteLimits")
                    } else { // le groupe est inchange
                        if (equipmentConfig.isControllable()) {
                            new SynchronousGeneratorControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                        } else {
                            new SynchronousGenerator(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                        }
                    }
                } else if (!generator.getExtensionByName("activePowerControl").isParticipate() || Math.abs(generator.getTerminal().getP()) < epsilon) { // hors reglage de frequence seulement
                    def parameterName = (equipmentConfig.lib == "GeneratorPQInfiniteLimits" || equipmentConfig.lib == "GeneratorPV") ? "generator_AlphaPuPNom" : "governor_KGover"
                    // dynaWaltzParameters.getModelParameters(getParameterSetId()).getDouble(parameterName).setValue(0.)
                    println("Parameter " + parameterName + " modified for equipment " + staticId + " (= 0 because out of frequency regulation)")
                    if (equipmentConfig.isControllable()) {
                        new SynchronousGeneratorControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                    } else {
                        new SynchronousGenerator(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                    }
                } else if (equipmentConfig.lib != "GeneratorPQInfiniteLimits" && !isDiagramCoherent(generator)) {
                    // Il faut remplacer le parameterSetId par un nouveau et supprimer l'ancien
                    println("Dynamic data substitution for equipment " + staticId + " (= groupPfQ because of inconsistent diagram")
                    new SynchronizedGenerator(dynamicModelId, generator, parameterSetId, "GeneratorPQInfiniteLimits")
                } else { // le groupe est inchange
                    if (equipmentConfig.isControllable()) {
                        new SynchronousGeneratorControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                    } else {
                        new SynchronousGenerator(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                    }
                }
            } else {
                if (!generator) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " not present in network.")
                } else if (!generator.getTerminal().isConnected()) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " not connected.")
                } else if (generator.getTerminal().getBusBreakerView().getBus().getConnectedComponent().getNum() > 0) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " not in main connected component.")
                } else if (generator.getTerminal().getBusBreakerView().getBus().getV().isNaN()) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + "'s voltage level if off.")
                }
            }
        }

        boolean isDiagramCoherent(Generator generator) {
            true
        }
    }
}

// Modification du Sn ?
// Limitation temporaire des groupes
