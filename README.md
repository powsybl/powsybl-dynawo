# powsybl-dynawo

[![Actions Status](https://github.com/powsybl/powsybl-dynawo/workflows/CI/badge.svg)](https://github.com/powsybl/powsybl-dynawo/actions)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.powsybl%3Apowsybl-dynawo&metric=coverage)](https://sonarcloud.io/component_measures?id=com.powsybl%3Apowsybl-dynawo&metric=coverage)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.powsybl%3Apowsybl-dynawo&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.powsybl%3Apowsybl-dynawo)
[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)
[![Javadocs](https://www.javadoc.io/badge/com.powsybl/powsybl-dynawo.svg?color=blue)](https://www.javadoc.io/doc/com.powsybl/powsybl-dynawo)
[![Slack](https://img.shields.io/badge/slack-powsybl-blueviolet.svg?logo=slack)](https://join.slack.com/t/powsybl/shared_invite/zt-rzvbuzjk-nxi0boim1RKPS5PjieI0rA)

[Dynawo](https://dynawo.github.io) is an hybrid C++/Modelica open source suite of simulation tools for power systems. This integration module allows to use [DynaFlow](https://dynawo.github.io/about/dynaflow) for [power flow simulations](https://www.powsybl.org/pages/documentation/simulation/powerflow) and [DynaWaltz](https://dynawo.github.io/about/dynawaltz) for [time domain simulations](https://www.powsybl.org/pages/documentation/simulation/timedomain).

## DynaFlow

To use DynaFlow as your power flow engine, add the `com.powsybl:powsybl-dynawo` module to your dependencies.

```java
Network network = Importers.loadNetwork("/path/to/the/casefile.xiidm");
LoadFlowParameters parameters = LoadFlowParameters.load();
LoadFlow.find("DynaFlow").run(network, parameters);
```

To learn more about the usage of DynaFlow, read the [dedicated page](https://www.powsybl.org/pages/documentation/simulation/powerflow/dynaflow.html) on our website.

## DynaWaltz

To use DynaWaltz as your time domain engine, add the `com.powsybl:powsybl-dynawo` module to your dependencies. To run a dynamic simulation, you need:
- a case file
- a `DynamicModelsSupplier` to associate dynamic models to the equipment of the network
- an `EventModelsSuppler` to configure events simulated during the simulation (optional)
- a `CurvesSupplier` to follow the evolution of dynamic variables during the simulation (optional)
- a set of parameters to configure the simulation (optional)

In `powsybl-dynawo`, the inputs can be configured using Groovy scripts. The simulation parameters can be configured either in the `config.yml` file or using a Json file.

```java
Network network = Network.read("/path/to/the/casefile.xiidm");

// Load the dynamic models mapping
GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
    Paths.get("/path/to/dynamicModelsMapping.groovy"),
    GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

// Load the events
GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
    Paths.get("/path/to/event.groovy"),
    GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME));

// Configure the curves
GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
    Paths.get("/path/to/curves.groovy"),
    GroovyExtension.find(CurveGroovyExtension.class, DynaWaltzProvider.NAME));

// Load the parameters
DynamicSimulationParameters parameters = DynamicSimulationParameters.load();

// Run the simulation and display the results
DynamicSimulationResult result = DynamicSimulation.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier, parameters);
System.out.println(result.isOk());
System.out.println(result.getLogs());
```

To learn more about the usage of DynaWaltz, read the [dedicated page](https://www.powsybl.org/pages/documentation/simulation/timedomain/dynawo) on our website.

### Examples

This is an example of a dynamic models mapping file:
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
}

for (Line line : network.lines) {
    CurrentLimitAutomaton {
        staticId line.id
        dynamicModelId "BBM_" + line.id
        parameterSetId "CLA"
    }
}
```

Note that this mapping file refers to parameter set ids which should be found in the Dynawo parameters file.

Other examples can be found in the [resources](https://github.com/powsybl/powsybl-dynawo/tree/main/dynawaltz-dsl/src/test/resources) of this project.