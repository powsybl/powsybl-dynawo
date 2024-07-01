# Configuration

## Dynawo-algorithms properties
The `dynawo-algorithms` module defines the required parameters to run with Dynawo.

**homeDir**  
Use the `homeDir` property to defines the installation directory of the dynawo simulator.

**debug**  
Use the `debug` property to specify if the temporary folder where the inputs are generated should be kept after the simulation.

### Examples

**YAML configuration:**
```yaml
dynawo-algorithms:
  homeDir: /home/user/dynawo-algorithms
  debug: false
```

**XML configuration:**
```xml
<dynawo-algorithms>
  <homeDir>/home/user/dynawo</homeDir>
  <debug>false</debug>
</dynawo-algorithms>
```

## Default parameters
The dynamic security analysis reuse the `dynawo-default-parameters` [module](../dynamic_simulation/configuration.md#default-parameters).
