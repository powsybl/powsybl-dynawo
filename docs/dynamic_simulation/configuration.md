# Configuration

## Dynawo properties
The `dynawo` module defines the required parameters to run with Dynawo.

**homeDir**  
Use the `homeDir` property to defines the installation directory of the dynawo simulator.

**debug**  
Use the `debug` property to specify if the temporary folder where the inputs are generated should be kept after the simulation.

### Examples

**YAML configuration:**
```yaml
dynawo:
  homeDir: /home/user/dynawo
  debug: false
```

**XML configuration:**
```xml
<dynawo>
  <homeDir>/home/user/dynawo</homeDir>
  <debug>false</debug>
</dynawo>
```

## Default parameters
The `dynawo-simulation-default-parameters` module defines the default values for all specific parameters of a dynamic simulation run with Dynawo.

### Required parameters

#### parametersFile
`parametersFile` defines the path of the main parameters file.  
The default value is `models.par`.

#### network.parametersFile
`network.parametersFile` defines the path of the network parameters file.  
The default value is `network.par`.

#### solver.parametersFile
`solver.parametersFile` defines the path of the solver parameters file.  
The default value is `solvers.par`.

### Optional parameters

#### network.parametersId
`network.parametersId` defines the network parameters set id stored in `network.parametersFile`.  
The default value is `Network`.

#### solver.parametersId
`solver.parametersId` defines the solver parameters set id stored in `solver.parametersFile`.  
The default value is `SIM`.

#### solver.type
`solver.type` defines the solver used in the simulation.  
The available `com.powsybl.dynawo.DynawoSimulationParameters.SolverType` values are:
- `SIM`: the simplified solver (fixed time step solver)
- `IDA`: the IDA solver (variable time step solver)

The default value is `SIM`.

#### dump.export
`dump.export` defines if the dynamic simulation result should be exported as a dump.  
The default value is `FALSE`.

#### dump.useAsInput
`dump.useAsInput` defines if a previous simulation result dump should be used as input for the current simulation.  
The default value is `FALSE`.

#### dump.exportFolder
`dump.exportFolder` defines the folder name where the dump is exported.  
The default value is `null`.

#### dump.fileName
`dump.fileName` defines the dump file name.  
The default value is `null`.

#### useModelSimplifiers
`useModelSimplifiers` defines if simplifiers are used before macro connection computation (TODO: link).  
The default value is `FALSE`.

#### mergeLoads
`mergeLoads` indicates if loads connected to the same bus are merged (except fictitious load).  
The default value is `FALSE`.

#### timeline.exportMode
`timeline.exportMode` defines the file extension of the timeline export.  
The available `com.powsybl.dynawo.commons.ExportMode` values are:
- `CSV`
- `TXT`: same format as `CSV` but with `|` separator
- `XML`

The default value is `TXT`.

#### precision
`precision` defines the simulation step precision.  
The default value is `1e-6`.

#### log.levelFilter
`log.levelFilter` defines the log level for Dynawo log.  
The available `com.powsybl.dynawo.DynawoSimulationParameters.LogLevel` values are:
- `DEBUG`
- `INFO`
- `WARN`
- `ERROR`

The default value is `INFO`.

#### log.specificLogs
`log.specificLogs` defines as a list the specifics logs to return besides the regular Dynawo log.  
The available `com.powsybl.dynawo.DynawoSimulationParameters.SpecificLog` values are:
- `NETWORK`
- `MODELER`
- `PARAMETERS`
- `VARIABLES`
- `EQUATIONS`

The default value is an empty list.

#### criteria.file
`criteria.file` defines the simulation criteria file.  
The default value is `null`.

#### additionalModelsFile
`additionalModelsFile` defines the JSON file listing additional models used in the simulation.  
The file must follow the `models.json` pattern defined [here](dynamic-models-configuration.md#supported-models).
The models added in the file cannot overload an existing model and must belong to an existing category otherwise they will be skipped.  
The default value is `null`.

### Examples

**YAML configuration:**
```yaml
dynawo-simulation-default-parameters:
  parametersFile: /home/user/parametersFile
  network.parametersFile: /home/user/networkParametersFile
  network.parametersId: Network
  solver.type: SIM
  solver.parametersFile: /home/user/solverParametersFile
  solver.parametersId: SIM
  dump.export: false
  dump.useAsInput: false
  dump.exportFolder: /home/user/dumps
  dump.fileName: dump.dmp
  useModelSimplifiers: false
  mergeLoads: false
  timeline.exportMode: XML
  precision: 10e-6
  log.levelFilter: INFO
  log.specificLogs:
    - NETWORK
    - PARAMETERS
  criteria.file: /home/user/criteria.crt
```

**XML configuration:**
```xml
<dynawo-simulation-default-parameters>
  <parametersFile>/home/user/parametersFile</parametersFile>
  <network.parametersFile>/home/user/networkParametersFile</network.parametersFile>
  <network.parametersId>NETWORK</network.parametersId>
  <solver.type>SIM</solver.type>
  <solver.parametersFile>/home/user/solverParametersFile</solver.parametersFile>
  <solver.parametersId>SIM</solver.parametersId>
  <dump.export>false</dump.export>
  <dump.useAsInput>false</dump.useAsInput> 
  <dump.exportFolder>/home/user/dumps</dump.exportFolder>
  <dump.fileName>dump.dmp</dump.fileName>
  <useModelSimplifiers>false</useModelSimplifiers>
  <mergeLoads>false</mergeLoads>
  <timeline.exportMode>XML</timeline.exportMode>
  <precision>10e-6</precision>
  <log.levelFilter>INFO</log.levelFilter>
  <log.specificLogs>NETWORK, PARAMETERS</log.specificLogs>
  <criteria.file>/home/user/criteria.crt</criteria.file>
</dynawo-simulation-default-parameters>
```
