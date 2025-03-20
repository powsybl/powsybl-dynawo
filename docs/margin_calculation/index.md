# Margin calculation

```{toctree}
:hidden:
configuration.md
load_variations_supplier.md
```

PowSyBl-Dynaωo provides an implementation of the margin calculation with [Dynaωo-algorithms](https://dynawo.github.io/about/dynalgo), a wrapper around [Dynaωo](https://dynawo.github.io) providing utility algorithms to calculate complex key values of a power system.

The margin calculation is a simulation of a same base power system with a load variation and subject to different events to compute the maximum load increase in a specific region before the voltage collapses.

The algorithm is as follows: we first simulate the maximum load variation(100%) in the first step, and then use the results as the starting point for each event simulation in the final step. If one of those simulation fails, a dichotomy is applied to find out the maximum load variation for which all events (global margin) or each event (local margin) pass.

## Installation

Read this [documentation page](https://dynawo.github.io/install/dynalgo) to learn how to install and configure Dynaωo-algorithms.

## Inputs

The inputs of the margin calculation are the following:
- a static network
- a set of dynamic models provided by the same [suppliers](../dynamic_simulation/dynamic-models-mapping) used for the dynawo dynamic simulation
- a set of [contingencies](inv:powsyblcore:*:*#simulation/security/contingency-dsl)
- a set of [load variations](load_variations_supplier)
- a set of parameters for the [margin calculation](/configuration) and for [Dynawo](../dynamic_simulation/configuration)

## Outputs

The outputs of a margin calculation is a list of load increase result each containing:
- the load increase level
- the status (`CONVERGED`, `DIVERGED` or `CRITERIA_FAILURE`)
- if the base case `CONVERGED`, alist of scenario result for each contingency
- in case of `CRITERIA_FAILURE`, the list of failed criterion

## Going further
- [Run a margin calculation through an iTools command](../iTools/margin-calculation.md): learn how to perform a margin calculation from the command line
You may find an extensive documentation of the Dynawo project [here](https://github.com/dynawo/dynawo/releases/latest/download/DynawoDocumentation.pdf).  
