# Configuration

## DynaFlow properties
The `dynaflow` module defines the required parameters to run a load flow with DynaFlow.

### homeDir
Use the `homeDir` property to define the installation directory of the dynaflow wrapper.

### debug
Use the `debug` property to specify if the temporary folder where the inputs are generated should be kept after the simulation.


### Examples
```yaml
dynaflow:
    homeDir: /path/to/dynaflow  # Directory obtained by unzipping the package should contain "bin"
    debug: false
```

**XML configuration:**
```xml
<dynaflow>
  <homeDir>/path/to/dynaflow</homeDir>
  <debug>false</debug>
</dynaflow>
```

## Default parameters
The `dynaflow-default-parameters` module defines the default values for all specific parameters of a load flow run with DynaFlow.

### Optional parameters

#### svcRegulationOn
`svcRegulationOn` defines if SVCs (Static Var Compensator) take part in the voltage regulation.  
The default value is `TRUE`.

#### shuntRegulationOn
`shuntRegulationOn` defines if Shunts take part in the voltage regulation.  
The default value is `TRUE`.

#### automaticSlackBusOn
`automaticSlackbusOn` defines if DynaFlow computes the slack bus (phase reference bus) by itself or if the slack bus is provided.  
The default value is `TRUE`.

#### dsoVoltageLevel
`dsoVoltageLevel` defines the minimum voltage of level of loads.  
The default value is `45.0`.

#### tfoVoltageLevel
`tfoVoltageLevel` defines the maximum voltage level for which generator transformers are considered to be in the iidm file.  
The default value is `100.0`.

#### activePowerCompensation
`activePowerCompensation` determines whether the generators participate in the active power balancing proportionally to:
- `P`
- `TARGET_P`
- `PMAX`

The default value is `PMAX`.

#### startTime
`startTime` defines the simulation start time (in s).  
The default value is `0`.

#### stopTime
`stopTime` defines the simulation stop time (in s).  
The default value is `100`.

#### timeStep
`timeStep` defines the maximum time solver step value (in s).
The default value is `10`.

#### chosenOutputs
`chosenOutputs` defines which outputs DynaFlow will produce  
Available values:
- `STEADYSTATE`: steady-state of the network
- `LOSTEQ`: lost equipments
- `TIMELINE`: simulation event timeline
- `CONSTRAINTS`

The default value is a list of all values.

#### startingPointMode
`startingPointMode` indicates the starting point values considered in the simulation
Available values:
- `WARM`: starting point values for voltage, phase and injections are the ones in the network.
- `FLAT`: starting point values are the nominal value for bus voltages and the set points values for injections.

The default value is `WARM`.

#### precision
`precision` defines the real number precision  
The default value is `NaN`.

#### assemblingPath
`assemblingPath` indicates the file which defines the models' association.  
The default value is `null`.

#### settingPath
`settingPath` indicates the file which defines the model settings values.  
The default value is `null`.

#### mergeLoads
`mergeLoads` indicates if loads connected to the same bus are merged (except fictitious load).  
The default value is `TRUE`.

## Generic parameters
Furthermore, DynaFlow only supports `useReactiveLimits` generic parameter, the other parameters are ignored.
You may have a description of these parameters [here](inv:powsyblcore:*:*:#loadflow-generic-parameters).

## Example

You may define those parameters in your configuration file:
```yaml
dynaflow-default-parameters:
    svcRegulationOn: true
    shuntRegulationOn: false
    automaticSlackBusOn: true
    dsoVoltageLevel: 987.6
    tfoVoltageLevel: 100
    activePowerCompensation: "P"
    settingPath: "path/to/settingFile"
    assemblingPath: "path/to/assemblingFile"
    startTime: 0.0
    stopTime: 100.0
    precision: 1.0
    chosenOutputs: [ "STEADYSTATE", "LOSTEQ", "TIMELINE", "CONSTRAINTS" ]
    timeStep: 2.6
    mergeLoads: true
    startingPointMode: "WARM"
```


Alternatively, you can provide parameters as a JSON file where supported
(for example when using `itools loadflow` command):
```json
{
  "version" : "1.10",
  "useReactiveLimits" : true,
  "extensions" : {
    "DynaflowParameters" : {
      "svcRegulationOn" : true,
      "shuntRegulationOn" : false,
      "automaticSlackBusOn" : true,
      "dsoVoltageLevel" : 987.6,
      "tfoVoltageLevel" : 100.0,
      "activePowerCompensation" : "P",
      "settingPath" : "path/to/settingFile",
      "assemblingPath" : "path/to/assemblingFile",
      "startTime" : 0.0,
      "stopTime" : 100.0,
      "precision" : 1.0,
      "chosenOutputs" : [ "STEADYSTATE", "LOSTEQ", "TIMELINE", "CONSTRAINTS" ],
      "timeStep" : 2.6,
      "mergeLoads" : true,
      "startingPointMode" : "WARM"
    }
  }
}
```