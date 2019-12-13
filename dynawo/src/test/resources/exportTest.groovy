/**
 * Copyright (c) 2019 RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License v. 2.0. If a copy of the MPL was not distributed with this
 * file You can obtain one at http://mozilla.org/MPL/2.0/.
 */
job('j1') {
    solver {
        lib 'lib'
        file 'file'
        id '2'
    }
    modeler {
        compile 'compile'
        iidm 'iidm'
        parameters 'parameters'
        parameterId '1'
        dyd 'dyd'
        useStandardModelsPreCompiledModels false
        useStandardModelsModelicaModels false
        preCompiledModelsDir 'preCompiledModelsDir'
        modelicaModelsDir 'modelicaModelsDir'
        initialState 'initialState'
    }
    simulation {
        startTime 0
        stopTime 30
        activeCriteria false
        precision 1e-6
    }
    outputs {
        directory 'directory'
        curve 'curve'
        appenders {
            appender {
                tag 'tag'
                file 'file'
                lvlFilter 'lvlFilter'
            }
        }
    }
}
solverParameterSet ('1') {
    parameters {
        parameter {
            name 'name'
            type 'type'
            value 'value'
        }
    }
}
parameterSet ('1') {
    parameters {
        parameter {
            name 'name1'
            type 'type1'
            origData 'origData1'
            origName 'origName1'
            componentId 'componentId1'
        }
        parameter {
            name 'name2'
            type 'type2'
            value 'value2'
        }
    }
    parameterTables {
        parameterTable {
            name 'nameTable'
            type 'typeTable'
            parameterRows {
                parameterRow {
                    row 1
                    column 1
                    value 'valueRow'
                }
            }
        }
    }
}
blackBoxModel ('bbid') {
    lib 'bblib'
    parametersFile 'parametersFile'
    parametersId '1'
    staticId 'staticId'
    staticRefs {
        staticRef {
            var 'var1'
            staticVar 'staticVar1'
        }
        staticRef {
            var 'var2'
            staticVar 'staticVar2'
        }
    }
    macroStaticRefs {
        macroStaticRef ('macroStaticid')
    }
}
modelicaModel ('mid') {
    unitDynamicModels {
        unitDynamicModel ('udmid') {
            name 'name'
            moFile 'moFile'
            initName 'initName'
            parametersFile 'parFile'
            parametersId '1'
        }
    }
    connections {
        connection {
            id1 'id1'
            var1 'var1'
            id2 'id2'
            var2 'var2'
        }
    }
    initConnections {
        initConnection {
            id1 'id1'
            var1 'var1'
            id2 'id2'
            var2 'var2'
        }
    }
    staticRefs {
        staticRef {
            var 'var1'
            staticVar 'staticVar1'
        }
        staticRef {
            var 'var2'
            staticVar 'staticVar2'
        }
    }
    macroStaticRefs {
        macroStaticRef ('macroStaticid')
    }
}
modelTemplate ('mtid') {
    unitDynamicModels {
        unitDynamicModel ('udmid') {
            name 'name'
            moFile 'moFile'
            initName 'initName'
            parametersFile 'parFile'
            parametersId '1'
        }
    }
    connections {
        connection {
            id1 'id1'
            var1 'var1'
            id2 'id2'
            var2 'var2'
        }
    }
    initConnections {
        initConnection {
            id1 'id1'
            var1 'var1'
            id2 'id2'
            var2 'var2'
        }
    }
}
modelTemplateExpansion ('mtid') {
    templateId 'templateId'
    parametersFile 'parametersFile'
    parametersId '1'
}
connection {
    id1 'id1'
    var1 'var1'
    id2 'id2'
    var2 'var2'
}
initConnection {
    id1 'id1'
    var1 'var1'
    id2 'id2'
    var2 'var2'
}
macroConnector ('mcid') {
    connections {
        connection {
            var1 'var1'
            var2 'var2'
        }
    }
}
macroStaticRef ('msrid') {
    staticRefs {
        staticRef {
            var 'var'
            staticVar 'staticVar'
        }
    }
}
macroConnection {
    connector 'connector'
    id1 'id1'
    id2 'id2'
}
curve {
    model 'model'
    variable 'variable'
}
