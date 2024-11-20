# Configuration

## DynaFlow configuration

You need to tell powsybl where to find DynaFlow, by adding this into you configuration file:
```yaml
dynaflow:
    homeDir: /path/to/dynaflow  # Directory obtained by unzipping the package, should contain "bin"
    debug: false
```

To use DynaFlow as a default for all power flow computations, you may configure the `load-flow`
module in your configuration file:
```yaml
load-flow:
    default-impl-name: DynaFlow
```

## Default parameters
The `dynaflow-default-parameters` module defines the default values for all specific parameters of a load flow run with DynaFlow.

## Optional parameters

**svcRegulationOn**  
`svcRegulationOn` defines if SVCs (Static Var Compensator) take part in the voltage regulation.  
The default value is `TRUE`.

**shuntRegulationOn**  
`shuntRegulationOn` defines if Shunts take part in the voltage regulation.  
The default value is `TRUE`.

**automaticSlackBusOn**  
`automaticSlackbusOn` defines if DynaFlow computes the slack bus (phase reference bus) by itself or if the slack bus is provided.  
The default value is `TRUE`.

**dsoVoltageLevel**  
`dsoVoltageLevel` defines the minimum voltage of level of loads.  
The default value is `45.0`.

**activePowerCompensation**  
`activePowerCompensation` determines whether the generators participate in the active power balancing proportionally to:
- `P`
- `TARGET_P`
- `PMAX`

The default value is `PMAX`.

**startTime**  
`startTime` defines the simulation start time (in s).  
The default value is `0`.

**stopTime**  
`stopTime` defines the simulation stop time (in s).  
The default value is `100`.

(timeStepDef)= 

**timeStep**  
`timeStep` defines the maximum time solver step value (in s).
The default value is `10`.

**chosenOutputs**  
`chosenOutputs` defines which outputs DynaFlow will produce  
Available values:
- `STEADYSTATE`: steady-state of the network
- `LOSTEQ`: lost equipments
- `TIMELINE`: simulation event timeline
- `CONSTRAINTS`

The default value is a list of all values.

**startingPointMode**  
`startingPointMode` indicates the starting point values considered in the simulation
Available values  **(TODO: describe them)**:
- `WARM`: starting point values for voltage, phase and injections are considered previously calculated.
- `FLAT`: starting point values considered are nominal value for bus voltages and set points values for injections.

The default value is `WARM`.

**precision**  
`precision` defines the real number precision  
The default value is `Nan`.

**assemblingPath**  
`assemblingPath` is used to indicates the file which defines the models' association.  
The default value is `null`.

**settingPath**  
`settingPath` is used to indicates the file which defines the model settings values.  
The default value is `null`.

**mergeLoads**  
`mergeLoads` indicates if loads connected to the same bus are merged.  
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
  "version" : "1.9",
  "useReactiveLimits" : true,
  "extensions" : {
    "DynaflowParameters" : {
      "svcRegulationOn" : true,
      "shuntRegulationOn" : false,
      "automaticSlackBusOn" : true,
      "dsoVoltageLevel" : 987.6,
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