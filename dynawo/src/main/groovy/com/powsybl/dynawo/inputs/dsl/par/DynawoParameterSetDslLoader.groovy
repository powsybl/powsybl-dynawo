/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl.par

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.inputs.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.inputs.model.par.Parameter
import com.powsybl.dynawo.inputs.model.par.ParameterRow
import com.powsybl.dynawo.inputs.model.par.ParameterSet
import com.powsybl.dynawo.inputs.model.par.ParameterTable
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoParameterSetDslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoParameterSetDslLoader.class)

    static class ParameterRowSpec {

        int row
        int column
        String value

        ParameterRowSpec row(int row) {
            assert row != null
            this.row = row
            return this
        }

        ParameterRowSpec column(int column) {
            assert column != null
            this.column = column
            return this
        }

        ParameterRowSpec value(String value) {
            assert value != null
            this.value = value
            return this
        }
    }

    static class ParameterRowsSpec {
    }

    static class ParameterTableSpec {

        String type
        String name
        final ParameterRowsSpec parameterRowsSpec = new ParameterRowsSpec()

        ParameterTableSpec name(String name) {
            assert name != null
            this.name = name
            return this
        }

        ParameterTableSpec type(String type) {
            assert type != null
            this.type = type
            return this
        }

        ParameterTableSpec parameterRows(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parameterRowsSpec
            cloned()
            return this
        }
    }

    static class ParameterTablesSpec {
    }

    static class ParameterSpec {

        String name;
        String type;
        String value;
        String origName;
        String origData;
        String componentId;

        ParameterSpec name(String name) {
            assert name != null
            this.name = name
            return this
        }

        ParameterSpec type(String type) {
            assert type != null
            this.type = type
            return this
        }

        ParameterSpec value(String value) {
            assert value != null
            this.value = value
            return this
        }

        ParameterSpec origName(String origName) {
            assert origName != null
            this.origName = origName
            return this
        }

        ParameterSpec origData(String origData) {
            assert origData != null
            this.origData = origData
            return this
        }

        ParameterSpec componentId(String componentId) {
            assert componentId != null
            this.componentId = componentId
            return this
        }
    }

    static class ParametersSpec {
    }

    static class ParameterSetSpec {

        String id
        final ParametersSpec parametersSpec = new ParametersSpec()
        final ParameterTablesSpec parameterTablesSpec = new ParameterTablesSpec()

        ParameterSetSpec parameters(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parametersSpec
            cloned()
            return this
        }

        ParameterSetSpec parameterTables(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parameterTablesSpec
            cloned()
            return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<ParameterSet> consumer, DynawoDslLoaderObserver observer) {

        // parameterSets
        binding.parameterSet = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSetSpec parameterSetSpec = new ParameterSetSpec()

            List<Parameter> parameters = new ArrayList<>()
            addParameters(parameterSetSpec.parametersSpec.metaClass, parameters, binding)

            List<ParameterTable> parameterTables = new ArrayList<>()
            addParameterTables(parameterSetSpec.parameterTablesSpec.metaClass, parameterTables, binding)

            cloned.delegate = parameterSetSpec
            cloned()

            // create parameterSet
            ParameterSet parameterSet = new ParameterSet(id)
            parameterSet.addParameters(parameters)
            parameterSet.addParameterTables(parameterTables)
            consumer.accept(parameterSet)

            LOGGER.debug("Found parameterSet '{}'", id)
            observer?.parameterSetFound(id)
        }
    }

    static void addParameterTables(MetaClass parameterTablesSpecMetaClass, List<ParameterTable> parameterTables, Binding binding) {

        parameterTablesSpecMetaClass.parameterTable = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterTableSpec parameterTableSpec = new ParameterTableSpec()

            List<ParameterRow> parameterRows = new ArrayList<>()
            addParameterRows(parameterTableSpec.parameterRowsSpec.metaClass, parameterRows, binding)

            cloned.delegate = parameterTableSpec
            cloned()
            ParameterTable parameterTable = new ParameterTable(parameterTableSpec.name, parameterTableSpec.type)
            parameterTable.addParameterRows(parameterRows)
            parameterTables.add(parameterTable)
        }
    }

    static void addParameterRows(MetaClass parameterRowsSpecMetaClass, List<Parameter> parameterRows, Binding binding) {

        parameterRowsSpecMetaClass.parameterRow = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterRowSpec parameterRowSpec = new ParameterRowSpec()
            cloned.delegate = parameterRowSpec
            cloned()
            ParameterRow parameterRow = new ParameterRow(parameterRowSpec.row, parameterRowSpec.column, parameterRowSpec.value)
            parameterRows.add(parameterRow)
        }
    }

    static void addParameters(MetaClass parametersSpecMetaClass, List<Parameter> parameters, Binding binding) {

        parametersSpecMetaClass.parameter = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSpec parameterSpec = new ParameterSpec()
            cloned.delegate = parameterSpec
            cloned()
            if (parameterSpec.origName != null) {
                if (parameterSpec.componentId != null) {
                    Parameter parameter = new Parameter(parameterSpec.name, parameterSpec.type, parameterSpec.origData, parameterSpec.origName, parameterSpec.componentId)
                    parameters.add(parameter)
                } else {
                    Parameter parameter = new Parameter(parameterSpec.name, parameterSpec.type, parameterSpec.origData, parameterSpec.origName)
                    parameters.add(parameter)
                }
            } else {
                Parameter parameter = new Parameter(parameterSpec.name, parameterSpec.type, parameterSpec.value)
                parameters.add(parameter)
            }
        }
    }
}
