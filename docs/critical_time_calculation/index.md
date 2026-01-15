# Critical Time calculation

```{toctree}
:hidden:
configuration.md
node_faults_supplier.md
```

PowSyBl-Dynaωo provides an implementation of the critical time calculation with [Dynaωo-algorithms](https://dynawo.github.io/about/dynalgo), a wrapper around [Dynaωo](https://dynawo.github.io) providing utility algorithms to calculate complex key values of a power system.

The calculation of critical time is a transient stability analysis used to determine the maximum time during which a fault can persist on the network without causing a loss of system stability after it has been cleared. 

The algorithm is as follows: the critical time is calculated by simulating a [node fault](../dynamic_simulation/event-models-configuration.md#node-fault) and adjusting the breaker time by dichotomy. After each test, we check whether the system remains stable once the fault has been eliminated. If stability is maintained, the time is increased; if not, it is reduced. This method enables rapid convergence towards the maximum time before loss of synchronism.
## Installation

Read this [documentation page](https://dynawo.github.io/install/dynalgo) to learn how to install and configure Dynaωo-algorithms.

## Inputs

The inputs of the critical time calculation are the following:
- a static network
- a set of dynamic models provided by the same [suppliers](../dynamic_simulation/dynamic-models-configuration) used for the dynawo dynamic simulation
- a set of [node faults](node_faults_supplier)
- a set of parameters for the [critical time calculation](/configuration) and for [Dynawo](../dynamic_simulation/configuration)

## Outputs

The outputs of a critical time calculation is a list of scenario results each containing:
- the status (`RESULT_FOUND`, `CT_BELOW_MIN_BOUND` or `CT_ABOVE_MAX_BOUND`)
- if case of `RESULT_FOUND`, the calculated critical time

## Going further
- [Run a critical time calculation through an iTools command](../iTools/critical-time-calculation.md): learn how to perform a critical time calculation from the command line.
- You may find an extensive documentation of the Dynawo project [here](https://github.com/dynawo/dynawo/releases/latest/download/DynawoDocumentation.pdf).  
