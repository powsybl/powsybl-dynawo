# powsybl-dynawo
[Dynawo](https://dynawo.github.io) integration in [PowSyBl](https://www.powsybl.org)

## Running Dynawo from the command line
The PowSybl DynamicSimulation tool is used to launch the Dynawo Simulator.  
Arguments:
 - `case-file`: A network file (mandatory).
 - `dynamic-models-file`: A GROOVY dynamic model and automaton file (mandatory), only processes models of type `LoadAlphaBeta`, `GeneratorSynchronousFourWindingsProportionalRegulations`, `GeneratorSynchronousThreeWindingsProportionalRegulations` and `OmegaRef`, and automatons of type `CurrentLimitAutomaton`.
 - `event-models-file`: A GROOVY event model file (optional), only processes models of type `EventQuadripoleDisconnection`.
 - `curves-file`: A GROOVY curves file (optional).
 - `parameters-file`: A JSON parameters file (optional).

Sample invocation from the command line:
```
$> itools dynamic-simulation --case-file network.xiidm --dynamic-models-file dynamic_models.groovy [--event-models-file event_models.groovy] [--curves-file curves.groovy] [--parameters-file parameters.json]"
```

The package `powsybl-config-classic` is used for runtime. Tests use `powsybl-config-test`.

Sample contents of `${HOME}/.itools/config.yml`
```
dynamic-simulation-default-parameters:
    startTime: 1  % Instant of time at which the dynamic simulation begins.
    stopTime: 100  % Instant of time at which the dynamic simulation ends.

dynawo:
  homeDir: /home/dynawo  % Path of the Dynawo installation.
  debug: false  % flag to activate the debug mode of the Dynawo Simulator.

dynawo-default-parameters:
    parametersFile: /work/unittests/models.par  % Path of the file which contains the parameters of the dynamic models.
    network.parametersFile: /work/unittests/network.par  % Path of the file which contains the parameters of the network.
    network.parametersId: "1"  % Parameters Id for the selected network.
    solver.type: IDA  % Selected solver type.
    solver.parametersFile: /work/unittests/solver.par  % Path of the file which contains the parameters of the solver.
    solver.parametersId: "1"  % Parameters Id for the selected solver.
```

Sample contents of `dynamic-models-file`
```
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Generator


for (Load load : network.loads) {
    LoadAlphaBeta {
        staticId load.id
        parameterSetId "LAB"
    }
}

for (Generator gen : network.generators) {
    GeneratorSynchronousThreeWindingsProportionalRegulations {
        staticId gen.id
        dynamicModelId "BBM_" + gen.id
        parameterSetId "GSTWPR"
    }
    OmegaRef {
        generatorDynamicModelId "BBM_" + gen.id
    }
}

for (Line line : network.lines) {
    CurrentLimitAutomaton {
        staticId line.id
        dynamicModelId "BBM_" + line.id
        parameterSetId "CLA"
    }
}
```
