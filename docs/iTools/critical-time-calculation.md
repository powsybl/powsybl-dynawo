# iTools critical-time-calculation

The `critical-time-calculation` command loads a network, applies dynamic models, [node faults](../dynamic_simulation/event-models-configuration.md#node-fault), and runs a [critical-time-calculation](../critical_time_calculation/index.md).
In the end, the results will be exported to files.

## Usage
```
usage: itools [OPTIONS] critical-time-calculation --case-file <FILE>
       --dynamic-models-file <FILE> [--help] [-I <property=value>]
       [--import-parameters <IMPORT_PARAMETERS>] --node-faults-file <FILE>
       [--output-file <FILE>] [--output-log-file <FILE>] [--parameters-file
       <FILE>]

Available options are:
    --config-name <CONFIG_NAME>   Override configuration file name

Available arguments are:
    --case-file <FILE>                        the case path
    --dynamic-models-file <FILE>              dynamic models description as a
                                              Groovy file: defines the dynamic
                                              models to be associated to chosen
                                              equipments of the network
    --help                                    display the help and quit
 -I <property=value>                          use value for given importer
                                              parameter
    --import-parameters <IMPORT_PARAMETERS>   the importer configuration file
    --node-faults-file <FILE>                 node faults description as a JSON
                                              file
    --output-file <FILE>                      critical time calculation results
                                              output path
    --output-log-file <FILE>                  critical time calculation logs
                                              output path
    --parameters-file <FILE>                  critical time calculation
                                              parameters as a JSON file

```

### Required options

`--case-file`  
This option defines the path of the case file on which the simulation is run. The [supported formats](inv:powsyblcore:*:*#grid_exchange_formats/index.md) depend on the execution class path. 

`--dynamic-models-file`  
This option defines the path of the file used to associate dynamic models to static equipments of the network or add dynamic automation systems. At the moment, only groovy scripts are supported. The [dynamic models DSL](inv:powsyblcore:*:*#simulation/dynamic/index.md#dynamic-models-configuration) depends on the simulator used.

`--node-faults-file`  
This option defines the path of the node faults files. This file is a JSON that respects the [node faults](../critical_time_calculation/node_faults_supplier.md) syntax.

### Optional options

`--import-parameters`  
This option defines the path of the importer's configuration file. It's possible to overload one or many parameters using the `-I property=value` syntax. The list of supported properties depends on the [input format](inv:powsyblcore:*:*#grid_exchange_formats/index.md).

`--output-file`  
This option defines the path where to export the [results](#results) of the simulation.

`--parameters-file`  
This option defines the path of the [parameters](#parameters) file of the simulation. If this option is not used, the simulation is run with the default parameters. 

## Parameters
The available parameters are described [here](../critical_time_calculation/configuration).

## Results
The expected results are described [here](../critical_time_calculation/index#outputs)

## Examples
The following example shows how to run a critical time calculation, using the default configuration:
```
$> itools critical-time-calculation --case-file /network.xiidm --dynamic-models-file /dynamicModels.groovy --node-faults-file /nodeFaults.json
Loading network '/network.xiidm'
Critical Time Calculation Tool
Critical Time Calculation results:
+-------------+--------------------+---------------+
| Id          | Status             | Critical Time |
+-------------+--------------------+---------------+
| NodeFault_0 | RESULT_FOUND       | 1,00000       |
| NodeFault_1 | CT_BELOW_MIN_BOUND | inv           |
| NodeFault_2 | CT_ABOVE_MAX_BOUND | inv           |
+-------------+--------------------+---------------+
```
