# Configuration

## Dynawo-algorithms properties
The `dynawo-algorithms` module defines the required parameters to run with Dynawo-algorithms.

### homeDir
Use the `homeDir` property to define the installation directory of the dynawo-algorithms wrapper.

### debug
Use the `debug` property to specify if the temporary folder where the inputs are generated should be kept after the simulation.<br>
This property is independent of the `debugDir` parameter property (here for [SA](inv:powsyblcore:*:*#simulation/dynamic_security/configuration.md#optional-properties) and here for [MC](../margin_calculation/configuration.md#debugdir)). If both properties are set to `true`, the temporary simulation folder will be kept **and** dumped in the requested folder.

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
  <homeDir>/home/user/dynawo-algorithms</homeDir>
  <debug>false</debug>
</dynawo-algorithms>
```

## Default parameters
The dynamic security analysis reuses the `dynawo-simulation-default-parameters` [module](../dynamic_simulation/configuration.md#default-parameters).
