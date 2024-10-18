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
`activePowerCompensation` defines which type of power compensation applies.
Available values **(TODO: describe them)**:
- `P`
- `TARGET_P`
- `PMAX`

The default value is `PMAX`.

**settingPath**  
`settingPath` is used to indicates the file which defines the model settings values.  
The default value is `null`.

**assemblingPath**  
`assemblingPath` is used to indicates the file which defines the models' association.  
The default value is `null`.

**startTime**  
`startTime` defines the simulation start time (in s).  
The default value is `0`.

**stopTime**  
`stopTime` defines the simulation stop time (in s).  
The default value is `100`.

**precision**  
**(TODO: description)**  
The default value is `Nan`.

**chosenOutputs**  
**(TODO: description)**   
Available values **(TODO: describe them)**:
- `STEADYSTATE`
- `LOSTEQ`
- `TIMELINE`
- `CONSTRAINTS`

The default value is a list of all of them.

**timeStep**  
**(TODO: description)**  
The default value is `10`.

**mergeLoads**  
`mergeLoads` is used to indicates if loads connected to the same bus are merged.  
The default value is `TRUE`.

**startingPointMode**  
**(TODO: description)**
Available values  **(TODO: describe them)**:
- `WARM`
- `FLAT`

The default value is `WARM`.

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