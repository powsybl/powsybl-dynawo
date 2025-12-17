# Additional models
Additional models allow advanced user to test new Dynawo models that are not already implemented in powsybl-dynawo.
Additional models can be set in [Dynawo simulation parameters](configuration.md#additionalModelsFile) with a JSON file.

## Minimal configuration
In order to add a dynamic model, the `lib` name have to be written in an existing category. 
For example:
```json
{
  "BASE_LINE": {
    "libs": [
      {
        "lib": "Line2"
      }
    ]
  }
}
```
In this case, a new line model called `Line2` will be added to the list of line dynamic model.
Every property can be set: see [library-properties](dynamic-models-configuration.md#library-properties).

Note: the dynamic model must exist in Dynawo.

## Complex configuration
For generator model using specialized component, we need to override some connection properties.
Thus, `macroStaticReference` and variable prefix can be set for additional model.
For example:
```json
{
  "BASE_GENERATOR": {
    "libs": [
      {
        "lib": "BaseGenerator2",
        "variablePrefix": [
          {
            "variable":"terminal",
            "prefix":"generatorAlt"
          }
        ],
        "macroStaticRef": [
          {
            "dynamicVar":"generator2_PGen2",
            "staticVar":"p"
          },
          {
            "dynamicVar":"generator2_QGen2",
            "staticVar":"q"
          },
          {
            "dynamicVar":"generator2_state",
            "staticVar":"state"
          }
        ]
      }
    ]
  }
}
```
In this case, the `BaseGenerator2` will have overridden `macroStaticReference` and `terminal` connection giving use the following dyd configuration file:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<dyn:dynamicModelsArchitecture xmlns:dyn="http://www.rte-france.com/dynawo">
    <dyn:blackBoxModel id="GEN" lib="BaseGenerator2" parFile="models.par" parId="GPQ" staticId="GEN">
        <dyn:macroStaticRef id="MSR_BaseGenerator2"/>
    </dyn:blackBoxModel>
    <dyn:macroConnector id="MC_BaseGenerator2-DefaultEquipmentConnectionPoint">
        <dyn:connect var1="generatorAlt_terminal" var2="@STATIC_ID@@NODE@_ACPIN"/>
        <dyn:connect var1="generator_switchOffSignal1" var2="@STATIC_ID@@NODE@_switchOff"/>
    </dyn:macroConnector>
    <dyn:macroStaticReference id="MSR_BaseGenerator2">
        <dyn:staticRef var="generator2_PGen2" staticVar="p"/>
        <dyn:staticRef var="generator2_QGen2" staticVar="q"/>
        <dyn:staticRef var="generator2_state" staticVar="state"/>
    </dyn:macroStaticReference>
    <dyn:macroConnect connector="MC_BaseGenerator2-DefaultEquipmentConnectionPoint" id1="GEN" id2="NETWORK"/>
</dyn:dynamicModelsArchitecture>
```
### Managed category
This overriding mechanism works only with the following category:
- `BASE_GENERATOR`
- `SYNCHRONIZED_GENERATOR`
- `SYNCHRONOUS_GENERATOR` 

### Managed variable prefix
The variable prefix overriding mechanism works only with the following variable:
- `terminal`
- `switchOffSignal` (will override switch off signal 1, 2 and 3)
- `omegaRefPu`
- `omegaPu`
- `running`
