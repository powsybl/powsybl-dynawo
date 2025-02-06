# iTools margin-calculation

The `margin-calculation` command loads a network, apply dynamic models, contingencies, loads variations and run a [margin calculation](../margin_calculation/index.md).
In the end, the results and the modified network can be exported to files.

## Usage
```
usage: itools [OPTIONS] margin-calculation --case-file <FILE>
       --contingencies-file <FILE> --dynamic-models-file <FILE> [--help] [-I
       <property=value>] [--import-parameters <IMPORT_PARAMETERS>]
       --load-variations-file <FILE> [--output-file <FILE>] [--output-log-file
       <FILE>] [--parameters-file <FILE>]

Available options are:
    --config-name <CONFIG_NAME>   Override configuration file name

Available arguments are:
    --case-file <FILE>                        the case path
    --contingencies-file <FILE>               contingencies description as a
                                              Groovy file
    --dynamic-models-file <FILE>              dynamic models description as a
                                              Groovy file: defines the dynamic
                                              models to be associated to chosen
                                              equipments of the network
    --help                                    display the help and quit
 -I <property=value>                          use value for given importer
                                              parameter
    --import-parameters <IMPORT_PARAMETERS>   the importer configuration file
    --load-variations-file <FILE>             load variations description as a
                                              JSON file
    --output-file <FILE>                      margin calculation results output
                                              path
    --output-log-file <FILE>                  margin calculation logs output
                                              path
    --parameters-file <FILE>                  margin calculation parameters as a
                                              JSON file

```

### Required options

`--case-file`  
This option defines the path of the case file on which the simulation is run. The [supported formats](inv:powsyblcore:*:*#grid_exchange_formats/index.md) depend on the execution class path. 

`--dynamic-models-file`  
This option defines the path of the mapping file used to associate dynamic models to static equipments of the network or add dynamic automation systems. At the moment, only groovy scripts are supported. The [dynamic models DSL](inv:powsyblcore:*:*#simulation/dynamic/index.md#dynamic-models-mapping) depends on the simulator used.

`--contingencies-file`  
This option defines the path of the contingency files. This file is a groovy script that respects the [contingency DSL](inv:powsyblcore:*:*#simulation/security/contingency-dsl.md) syntax.

`--load-variations-file`  
This option defines the path of the load variations files. This file is a JSON that respects the [load variations](../margin_calculation/load_variations_supplier.md) syntax.

### Optional options

`--import-parameters`  
This option defines the path of the importer's configuration file. It's possible to overload one or many parameters using the `-I property=value` syntax. The list of supported properties depends on the [input format](inv:powsyblcore:*:*#grid_exchange_formats/index.md).

`--output-file`  
This option defines the path where to export the [results](#results) of the simulation.

`--parameters-file`  
This option defines the path of the [parameters](#parameters) file of the simulation. If this option is not used, the simulation is run with the default parameters. 

## Parameters
The available parameters are described [here](../margin_calculation/configuration).

## Results
The expected results are described [here](../margin_calculation/index#outputs)

## Examples
The following example shows how to run a margin calculation, using the default configuration:
```
$> itools margin-calculation --case-file /network.xiidm --dynamic-models-file /dynamicModels.groovy --contingencies-file /contingencies.groovy --load-variations-file /loadsVariations.json
Loading network '/network.xiidm'
Margin Calculation Tool
Margin calculation results:
+------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
| Load level | Status                 | Failed criteria     | Failed criteria time | Scenarios       | Scenarios Status       | Scenarios failed criteria    | Scenarios failed criteria time |
+------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
| 100.000    | CRITERIA_NON_RESPECTED | Failed criteria (2) |                      |                 |                        |                              |                                |
|            |                        | total load power    | 10.0000              |                 |                        |                              |                                |
|            |                        | total load power    | 20.0000              |                 |                        |                              |                                |
| 50.0000    | DIVERGENCE             |                     |                      | Scenarios (2)   |                        |                              |                                |
|            |                        |                     |                      | Disconnect line | CRITERIA_NON_RESPECTED | Scenario failed criteria (2) |                                |
|            |                        |                     |                      |                 |                        | Safety risk                  | 10.0000                        |
|            |                        |                     |                      |                 |                        | Safety risk                  | 20.0000                        |
|            |                        |                     |                      | Disconnect gen  | CONVERGENCE            |                              |                                |
| 25.0000    | CONVERGENCE            |                     |                      |                 |                        |                              |                                |
+------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
```
