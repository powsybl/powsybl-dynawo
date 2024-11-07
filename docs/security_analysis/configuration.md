# Configuration

The security analysis reuse the `dynaflow` [module](../load_flow/configuration.md#dynaflow-configuration)
and the `dynaflow-default-parameters` [module](../load_flow/configuration.md#default-parameters).

## Default parameters
The `dynaflow-security-analysis-default-parameters` module defines the default values for all specific parameters of a security analysis run with DynaFlow.  

### Optional parameters

**contingenciesStartTime**  
`contingenciesStartTime` defines when the contingencies start, in seconds.
If the start time occurs between two :ref:`time steps <timeStepDef>`: the contingencies will start on the next time step.
The default value of this property is 10.

### Examples

**YAML configuration:**
```yaml
dynaflow-security-analysis-default-parameters:
  contingenciesStartTime: 20
```

**XML configuration:**
```xml
<dynaflow-security-analysis-default-parameters>
  <contingenciesStartTime>20</contingenciesStartTime>
</dynaflow-security-analysis-default-parameters>
```