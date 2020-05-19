# powsybl-dynawo
[Dynawo](https://dynawo.github.io) integration in [PowSyBl](https://www.powsybl.org)

## Running Dynawo from main
Maven exec plugin is used to launch the `Main` class.  
Arguments:
 - A network file (mandatory)
 - A dynamic model file (mandatory)
 - A JSON parameters file (optional)

Sample invocation from the command line:
```
$> mvn exec:java -Dexec.args="path/to/ieee57cdf.xiidm path/to/ieee57cdf.dyd"
```

The package `powsybl-config-classic` is used for runtime. Tests use `powsybl-config-test`.

Sample contents of `${HOME}/.itools/config.yml`
```
dynawo:
    homeDir: dynawoHomeDir

dynawo-default-parameters:
    parametersFile: parametersFileLocation
    network.parametersFile: networkParametersFileLocation
    solver.parametersFile: solverParametersFileLocation
```
