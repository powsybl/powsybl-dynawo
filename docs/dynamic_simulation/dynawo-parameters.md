---
layout: default
---

# Dynawo parameters
The `dynawo-default-parameters` module defines the default values for all specific parameters of a dynamic simulation run with Dynawo. 

## Required parameters

**parametersFile**  
`parametersFile` defines the path of the main parameters file.  
The default value is `models.par`.

**network.parametersFile**  
`network.parametersFile` defines the path of the network parameters file.  
The default value is `network.par`.

**solver.parametersFile**  
`solver.parametersFile` defines the path of the solver parameters file.  
The default value is `solvers.par`.

## Optional parameters

**network.parametersId**  
`network.parametersId` defines the network parameters set id stored in `network.parametersFile`.  
The default value is `1`.

**solver.parametersId**  
`solver.parametersId` defines the solver parameters set id stored in `solver.parametersFile`.  
The default value is `1`.

**solver.type**  
`solver.type` defines the solver used in the simulation.  
The available `com.powsybl.dynawaltz.DynaWaltzParameters.SolverType` values are:
- `SIM`: the simplified solver (fixed time step solver)
- `IDA`: the IDA solver (variable time step solver)

The default value is `SIM`.

**dump.export**  
`dump.export` defines if the dynamic simulation result should be exported as a dump.  
The default value is `FALSE`.

**dump.useAsInput**  
`dump.useAsInput` defines if a previous simulation result dump should be used as input for the current simulation.  
The default value is `FALSE`.

**dump.exportFolder**  
`dump.exportFolder` defines the folder name where the dump is exported.  
The default value is `null`.

**dump.fileName**  
`dump.fileName` defines the dump file name.  
The default value is `null`.

**useModelSimplifiers**  
`useModelSimplifiers` defines if simplifiers are used before macro connection computation **(TODO: link)**.  
The default value is `FALSE`.

**mergeLoads**  
`mergeLoads` defines if loads connected to the same bus are merged or not.  
The default value is `FALSE`.

**timeline.exportMode**  
`timeline.exportMode` defines the file extension of the timeline export.  
The available `com.powsybl.dynawaltz.DynaWaltzParameters.ExportMode` values are:
- `CSV`
- `TXT`: same format as `CSV` but with `|` separator
- `XML`  

The default value is `TXT`.

**precision**  
`precision` defines the simulation step precision.  
The default value is `1e-6`.

## Examples

**YAML configuration:**
```yaml
dynawo-default-parameters:
  parametersFile: /home/user/parametersFile
  network.parametersFile: /home/user/networkParametersFile
  network.parametersId: 1
  solver.type: SIM
  solver.parametersFile: /home/user/solverParametersFile
  solver.parametersId: 1
  dump.export: false
  dump.useAsInput: false
  dump.exportFolder: /home/user/dumps
  dump.fileName: dump.dmp
  useModelSimplifiers: false
  mergeLoads: false
  timeline.exportMode: XML
  precision: 10e-6
```

**XML configuration:**
```xml
<dynawo-default-parameters>
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
</dynawo-default-parameters>
```
