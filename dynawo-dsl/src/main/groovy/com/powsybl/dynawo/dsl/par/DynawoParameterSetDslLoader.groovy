/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.par

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.par.DynawoParameter
import com.powsybl.dynawo.par.DynawoParameterRow
import com.powsybl.dynawo.par.DynawoParameterSet
import com.powsybl.dynawo.par.DynawoParameterTable
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

        void row(int row) {
            this.row = row
        }

        void column(int column) {
            this.column = column
        }

        void value(String value) {
            this.value = value
        }
    }

    static class ParameterRowsSpec {
    }

    static class ParameterTableSpec {

        String type
        String name
        final ParameterRowsSpec parameterRowsSpec = new ParameterRowsSpec()

        void name(String name) {
            this.name = name
        }

        void type(String type) {
            this.type = type
        }

        void parameterRows(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parameterRowsSpec
            cloned()
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

        void name(String name) {
            this.name = name
        }

        void type(String type) {
            this.type = type
        }

        void value(String value) {
            this.value = value
        }

        void origName(String origName) {
            this.origName = origName
        }

        void origData(String origData) {
            this.origData = origData
        }
    }

    static class ParametersSpec {
    }

    static class ParameterSetSpec {

        int id
        final ParametersSpec parametersSpec = new ParametersSpec()
        final ParameterTablesSpec parameterTablesSpec = new ParameterTablesSpec()

        void parameters(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parametersSpec
            cloned()
        }

        void parameterTables(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parameterTablesSpec
            cloned()
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoParameterSet> consumer, DynawoDslLoaderObserver observer) {

        // parameterSets
        binding.parameterSet = { int id, Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSetSpec parameterSetSpec = new ParameterSetSpec()

            List<DynawoParameter> parameters = new ArrayList<>()
            addParameters(parameterSetSpec.parametersSpec.metaClass, parameters, binding)

            List<DynawoParameterTable> parameterTables = new ArrayList<>()
            addParameterTables(parameterSetSpec.parameterTablesSpec.metaClass, parameterTables, binding)

            cloned.delegate = parameterSetSpec
            cloned()

            // create parameterSet
            DynawoParameterSet parameterSet = new DynawoParameterSet(id)
            parameterSet.addParameters(parameters)
            parameterSet.addParameterTables(parameterTables)
            consumer.accept(parameterSet)

            LOGGER.debug("Found parameterSet '{}'", id)
            observer?.parameterSetFound(id)
        }
    }

    static void addParameterTables(MetaClass parameterTablesSpecMetaClass, List<DynawoParameterTable> parameterTables, Binding binding) {

        parameterTablesSpecMetaClass.parameterTable = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterTableSpec parameterTableSpec = new ParameterTableSpec()

            List<DynawoParameterRow> parameterRows = new ArrayList<>()
            addParameterRows(parameterTableSpec.parameterRowsSpec.metaClass, parameterRows, binding)

            cloned.delegate = parameterTableSpec
            cloned()
            DynawoParameterTable parameterTable = new DynawoParameterTable(parameterTableSpec.name, parameterTableSpec.type)
            parameterTable.addParameterRows(parameterRows)
            parameterTables.add(parameterTable)
        }
    }

    static void addParameterRows(MetaClass parameterRowsSpecMetaClass, List<DynawoParameter> parameterRows, Binding binding) {

        parameterRowsSpecMetaClass.parameterRow = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterRowSpec parameterRowSpec = new ParameterRowSpec()
            cloned.delegate = parameterRowSpec
            cloned()
            DynawoParameterRow parameterRow = new DynawoParameterRow(parameterRowSpec.row, parameterRowSpec.column, parameterRowSpec.value)
            parameterRows.add(parameterRow)
        }
    }

    static void addParameters(MetaClass parametersSpecMetaClass, List<DynawoParameter> parameters, Binding binding) {

        parametersSpecMetaClass.parameter = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSpec parameterSpec = new ParameterSpec()
            cloned.delegate = parameterSpec
            cloned()
            if (parameterSpec.origName != null) {
                DynawoParameter parameter = new DynawoParameter(parameterSpec.name, parameterSpec.type, parameterSpec.origData, parameterSpec.origName)
                parameters.add(parameter)
            } else {
                DynawoParameter parameter = new DynawoParameter(parameterSpec.name, parameterSpec.type, parameterSpec.value)
                parameters.add(parameter)
            }
        }
    }
}
