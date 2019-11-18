/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.job;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoModeler {

    private final String compileDir;
    private final String preCompiledModelsDir;
    private final boolean useStandardModelsPreCompiledModels;
    private final String modelicaModelsDir;
    private final boolean useStandardModelsModelicaModels;
    private final String iidm;
    private final String parameters;
    private final String parameterId;
    private final String dyd;
    private final String initialState;

    public DynawoModeler(String compile, String iidm, String parameters, String parameterId, String dyd) {
        this(compile, null, true, null, true, iidm, parameters, parameterId, dyd, null);
    }

    public DynawoModeler(String compile, String preCompiledModelsDir, boolean useStandardModelsPreCompiledModels,
        String modelicaModelsDir, boolean useStandardModelsModelicaModels, String iidm, String parameters,
        String parameterId, String dyd, String initialState) {
        this.compileDir = compile;
        this.preCompiledModelsDir = preCompiledModelsDir;
        this.useStandardModelsPreCompiledModels = useStandardModelsPreCompiledModels;
        this.modelicaModelsDir = modelicaModelsDir;
        this.useStandardModelsModelicaModels = useStandardModelsModelicaModels;
        this.iidm = iidm;
        this.parameters = parameters;
        this.parameterId = parameterId;
        this.dyd = dyd;
        this.initialState = initialState;
    }

    public String getCompileDir() {
        return compileDir;
    }

    public String getPreCompiledModelsDir() {
        return preCompiledModelsDir;
    }

    public boolean isUseStandardModelsPreCompiledModels() {
        return useStandardModelsPreCompiledModels;
    }

    public String getModelicaModelsDir() {
        return modelicaModelsDir;
    }

    public boolean isUseStandardModelsModelicaModels() {
        return useStandardModelsModelicaModels;
    }

    public String getIidm() {
        return iidm;
    }

    public String getParameters() {
        return parameters;
    }

    public String getParameterId() {
        return parameterId;
    }

    public String getDyd() {
        return dyd;
    }

    public String getInitialState() {
        return initialState;
    }

}
