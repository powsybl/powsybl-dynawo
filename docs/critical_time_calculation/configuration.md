# Configuration

The critical time calculation reuses the `dynawo-simulation-default-parameters` [module](../dynamic_simulation/configuration.md#default-parameters) and the `dynawo-algorithms` [module](../dynamic_security_analysis/configuration.md#dynawo-algorithms-properties).

## Default parameters
The `critical-time-calculation-default-parameters` module defines the default values for all parameters of a critical time calculation run with Dynawo.  

### Optional parameters

**startTime**  
`startTime` defines when the simulation begins, in seconds.
The default value of this property is `0`.

**stopTime**  
`stopTime` defines when the simulation stops, in seconds.
The default value of this property is `200`.

**debugDir**
This property specifies the directory path where debug files will be dumped. If `null`, no file will be dumped.

**accuracy**  
`accuracy` defines the maximum difference between the result obtained and the exact margin, in percent.
The default value of this property is `0.001`.

**faultTimeMin**  
`faultTimeMin` defines the minimum time limit for the dichotomy calculation.
The default value of this property is `0`.

**faultTimeMax**  
`faultTimeMax` defines the maximum time limit for the dichotomy calculation.
The default value of this property is `5`.

**mode**  
`mode` defines how the dichotomy will occur.
The available `com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters.Mode` values are:
- `SIMPLE`: execute a classic dichotomy
- `COMPLEX`: execute a dichotomy without considering linearity issue as normal fails.

The default value of this property is `SIMPLE`.



### Examples

**YAML configuration:**
```yaml
critical-time-calculation-default-parameters:
  startTime: 0
  stopTime: 200
  accuracy: 0.001
  minValue: 0
  maxValue: 5
  mode: SIMPLE
  debugDir: "/tmp/debugDir"
```

**XML configuration:**
```xml
<critical-time-calculation-default-parameters>
    <startTime>0</startTime>
    <stopTime>200</stopTime>
    <accuracy>0.001</accuracy>
    <minValue>0</minValue>
    <maxValue>5</maxValue>
    <stopTime>200</stopTime>
    <mode>SIMPLE</mode>
    <debugDir>/tmp/debugDir</debugDir>
</critical-time-calculation-default-parameters>
```
