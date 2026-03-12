# Configuration

## DynaFlow properties

The `dynaflow` module defines the required parameters to run Dynaflow.

**homeDir**  
Use the `homeDir` property to define the installation directory of the DynaFlow wrapper.

**debug**  
Use the `debug` property to specify if the temporary folder where the inputs are generated should be kept after the simulation.

### Examples

**YAML configuration:**
```yaml
dynaflow:
    homeDir: /path/to/dynaflow  # Directory obtained by unzipping the package, should contain "bin"
    debug: false
```

**XML configuration:**
```xml
<dynaflow>
  <homeDir>/home/user/dynawo</homeDir>
  <debug>false</debug>
</dynaflow>
```

To use DynaFlow as a default for all power flow computations, you may configure the `load-flow`
module in your configuration file:
```yaml
load-flow:
    default-impl-name: DynaFlow
```

## Default parameters
The `dynaflow-default-parameters` module defines the default values for all specific parameters of a load flow run with DynaFlow.

### Optional parameters

#### svcRegulationOn
`svcRegulationOn` defines if SVCs (Static Var Compensator) take part in the voltage regulation.  
The default value is `TRUE`.

#### dsoVoltageLevel
`dsoVoltageLevel` defines the minimum voltage level of loads.  
The default value is `45.0`.

#### tfoVoltageLevel
`tfoVoltageLevel` defines the maximum voltage level for which generator transformers are considered to be in the iidm file.  
The default value is `100.0`.

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

## Unsupported generic parameters
Some [Load flow parameters](inv:powsyblcore:*:*#loadflow-generic-parameters) are not fully supported.

DC load flow is not supported.<br>
The `writeSlackBus` and `VoltageInitMode` parameters will be ignored.<br>
The following parameters will be ignored and IIDM setup will be used instead:
* `countriesToBalance`
* `hvdcAcEmulation`
* `phaseShifterRegulationOn`
* `transformerVoltageControlOn`
* `twtSplitShuntAdmittance`

The following `BalanceType` values will be replaced by `PROPORTIONAL_TO_GENERATION_P_MAX`:
* `PROPORTIONAL_TO_GENERATION_REMAINING_MARGIN`
* `PROPORTIONAL_TO_GENERATION_PARTICIPATION_FACTOR`
* `PROPORTIONAL_TO_CONFORM_LOAD`

The following `ComponentMode` values will be replaced by `MAIN_SYNCHRONOUS`:
* `MAIN_CONNECTED`
* `ALL_CONNECTED`

You can find more information about DynaFlow parameters in DynaFlow [documentation](https://github.com/dynawo/dynaflow-launcher/releases/download/v1.7.0/DynaflowLauncherDocumentation.pdf).

## Example

You may define those parameters in your configuration file:
```yaml
dynaflow-default-parameters:
    svcRegulationOn: true
    dsoVoltageLevel: 987.6
    tfoVoltageLevel: 100
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
      "dsoVoltageLevel" : 987.6,
      "tfoVoltageLevel" : 100.0,
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
