# Configuration

The margin calculation reuses the `dynawo-simulation-default-parameters` [module](../dynamic_simulation/configuration.md#default-parameters) and the `dynawo-algorithms` [module](../dynamic_security_analysis/configuration.md#dynawo-algorithms-properties).

## Default parameters
The `margin-calculation-default-parameters` module defines the default values for all parameters of a margin calculation run with Dynawo.  

### Optional parameters

**startTime**  
`startTime` defines when the simulation begins, in seconds.
The default value of this property is `0`.

**stopTime**  
`stopTime` defines when the simulation stops, in seconds.
The default value of this property is `200`.

**debugDir**
This property specifies the directory path where debug files will be dumped. If `null`, no file will be dumped.

**loadIncrease.startTime**  
`loadIncrease.startTime` defines when the load variation starts, in seconds.
This property value should be between start time and margin calculation start time.
The default value of this property is `10`.

**loadIncrease.stopTime**  
`loadIncrease.stopTime` defines when the load variation stops, in seconds.
This property value should be between load increase start time and margin calculation start time.
The default value of this property is `50`.

**margin-calculation-start-time**  
`margin-calculation-start-time` defines when the margin calculation starts, in seconds.
This property value should be between start and stop time.
The default value of this property is `100`.

**contingencies-start-time**  
`contingencies-start-time` defines when the contingencies start, in seconds.
This property value should be between margin calculation start time and stop time.
The default value of this property is `120`.

**calculationType**  
`calculationType` defines which maximum load variation is calculated.  
The available `com.powsybl.dynawo.margincalculation.MarginCalculationParameters.CalculationType` values are:
- `GLOBAL_MARGIN`: search for the maximum load variation for all events
- `LOCAL_MARGIN`: search for the maximum load variation for each event

The default value of this property is `GLOBAL_MARGIN`.

**accuracy**  
`accuracy` defines the maximum difference between the result obtained and the exact margin, in percent.
The default value of this property is `2`.

**load-models-rule**  
`load-models-rule` defines which loads will be set with the default dynamic model in the first step of the simulation.  
The available `com.powsybl.dynawo.margincalculation.MarginCalculationParameters.LoadModelsRule` values are:
- `ALL_LOADS`: all loads will use the default model
- `TARGETED_LOADS`: only loads affected by a load variation will use the default model

The default value of this property is `ALL_LOADS`.


### Examples

**YAML configuration:**
```yaml
margin-calculation-default-parameters:
  startTime: 0
  loadIncrease.startTime: 10
  loadIncrease.stopTime: 50
  margin-calculation-start-time: 100
  contingencies-start-time: 120
  stopTime: 200
  calculationType: GLOBAL_MARGIN
  accuracy: 2
  load-models-rule: ALL_LOADS
  debugDir: "/tmp/debugDir"
```

**XML configuration:**
```xml
<margin-calculation-default-parameters>
    <startTime>0</startTime>
    <loadIncrease.startTime>10</loadIncrease.startTime>
    <loadIncrease.stopTime>50</loadIncrease.stopTime>
    <margin-calculation-start-time>100</margin-calculation-start-time>
    <contingenciesStartTime>120</contingenciesStartTime>
    <stopTime>200</stopTime>
    <calculationType>GLOBAL_MARGIN</calculationType>
    <accuracy>2</accuracy>
    <load-models-rule>ALL_LOADS</load-models-rule>
    <debugDir>/tmp/debugDir</debugDir>
</margin-calculation-default-parameters>
```
