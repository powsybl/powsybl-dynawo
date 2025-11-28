# Dynamic models configuration
Dynamic model configuration creates dynamic models used by Dynawo.
Most of the dynamic models are associated with a static equipment present in the network.
If some equipments are not configured, Dynawo would use a default model and set of parameters.

## Implementation
Powsybl-Dynawo handles two methods of configuration:
- **Dynamic Models DSL**: a domain specific language written in groovy (used in iTools)
- **Dynawo Dynamic Models**: a JSON configuration file.

**Note**: For Json configuration, all models should be written in an `models` array.
```json
{
  "models":[...]
}
```
In the following examples the array will be omitted.

## Dynamic Model
- All the models share three attributes:
- `modelName`: refers to the dynamic model library used in Dynawo (used as a keyword in Groovy script).
- `dynamicModelId`: identifies the model (set explicitly or implicitly depending on the model type).
- `parameterSetId`: refers to a set of parameters for this model in the model parameters file (optional and set to `dynamicModelId` by default).

**Note**: In Json configuration file `parameterSetId` is replaced by the `group` attribute and can be customized with the `groupType` attribute taking one of the following value:
- `FIXED`: the `parameterSetId` equals the `group` (default value)
- `PREFIX`: the `parameterSetId` equals the `group` + `dynamicModelId`
- `SUFFIX`: the `parameterSetId` equals the `dynamicModelId` + `group`

See examples below.

### Equipment models
Dynamic models matching a network equipment (generator, load, ...).
With specific attribute:
- `staticId`: equipment id in the network.

The `dynamicModelId` will be set implicitly to `staticId` and cannot be modified.

**Groovy script:**
```groovy
GeneratorSynchronousThreeWindings {
    staticId 'GEN'
    parameterSetId 'GSTW'
}
LoadAlphaBeta {
    staticId 'LOAD'
    parameterSetId 'LAB_LOAD'
}
```
**Json configuration:**
```json
[
  {
    "model":"GeneratorSynchronousThreeWindings",
    "group": "GSTW",
    "properties":[
      {
        "name":"staticId",
        "value":"GEN",
        "type":"STRING"
      }
    ]
  },
  {
    "model":"LoadAlphaBeta",
    "group": "LAB",
    "groupType": "PREFIX",
    "properties":[
      {
        "name":"staticId",
        "value":"LOAD",
        "type":"STRING"
      }
    ]
  }
]
```

### Automation system models
Dynamic models representing an automation system without network representation.
The `dynamicModelId` have to be set explicitly.

#### Overload Management System
Automation system which emits a specific order when the current on the monitored line exceeds a given threshold for a given duration.  
With specific attributes:
- `controlledBranch`
- `iMeasurement`
- `iMeasurementSide`

**Groovy script:**
```groovy
import com.powsybl.iidm.network.TwoSides

OverloadManagementSystem {
    dynamicModelId "OVERLOAD1"
    parameterSetId "OMS"
    controlledBranch "LINE1"
    iMeasurement "TFO1"
    iMeasurementSide TwoSides.ONE
}
```
**Json configuration:**
```json
{
  "model":"OverloadManagementSystem",
  "group": "OMS",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "OVERLOAD1",
      "type": "STRING"
    },
    {
      "name": "controlledBranch",
      "value": "LINE1",
      "type": "STRING"
    },
    {
      "name": "iMeasurement",
      "value": "TFO1",
      "type": "STRING"
    },
    {
      "name": "iMeasurementSide",
      "value": "ONE",
      "type": "TWO_SIDES"
    }
  ]
}
```

#### Two Level Overload Management System
Automation system which emits a specific order when the current on two monitored lines exceeds a given threshold for a given duration.  
With specific attributes:
- `controlledBranch`
- `iMeasurement1`
- `iMeasurement1Side`
- `iMeasurement2`
- `iMeasurement2Side`

**Groovy script:**
```groovy
import com.powsybl.iidm.network.TwoSides

TwoLevelOverloadManagementSystem {
    dynamicModelId "OVERLOAD_TL_1"
    parameterSetId "OMS_TL"
    controlledBranch "LINE1"
    iMeasurement1 "TFO1"
    iMeasurement1Side TwoSides.TWO
    iMeasurement2 "LINE2"
    iMeasurement2Side TwoSides.ONE
}
```
**Json configuration:**
```json
{
  "model":"TwoLevelOverloadManagementSystem",
  "group": "OMS_TL",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "OVERLOAD_TL_1",
      "type": "STRING"
    },
    {
      "name": "controlledBranch",
      "value": "LINE1",
      "type": "STRING"
    },
    {
      "name": "iMeasurement1",
      "value": "TFO1",
      "type": "STRING"
    },
    {
      "name": "iMeasurement1Side",
      "value": "TWO",
      "type": "TWO_SIDES"
    },
    {
      "name": "iMeasurement2",
      "value": "LINE2",
      "type": "STRING"
    },
    {
      "name": "iMeasurement2Side",
      "value": "ONE",
      "type": "TWO_SIDES"
    }
  ]
}
```

#### Under Voltage Automation System
Sends switch-off signal to the monitored generator when the voltage goes below a threshold.  
With specific attribute:
- `generator`: generator static id

**Groovy script:**
```groovy
UnderVoltage {
    dynamicModelId "UV_GEN1"
    parameterSetId "UV"
    generator "GEN1"
}
```
**Json configuration:**
```json
{
  "model":"UnderVoltage",
  "group": "UV",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "UV_GEN1",
      "type": "STRING"
    },
    {
      "name": "generator",
      "value": "GEN1",
      "type": "STRING"
    }
  ]
}
```

#### Phase Shifter P
Phase-shifter which monitors a given active power. When the active power goes above a given threshold, taps will be changed in order to decrease it. When the active power goes below another threshold, the phase-shifter is automatically deactivated.  
With specific attribute:
- `transformer`: transformer static id

**Groovy script:**
```groovy
PhaseShifterP {
    dynamicModelId "PSP"
    parameterSetId "PS_PAR"
    transformer "TFO1"
}
```
**Json configuration:**
```json
{
  "model":"PhaseShifterP",
  "group": "PS_PAR",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "PSP",
      "type": "STRING"
    },
    {
      "name": "transformer",
      "value": "TFO1",
      "type": "STRING"
    }
  ]
}
```

#### Phase Shifter I
Phase-shifter which monitors a given current. When the current goes above a given threshold, taps will be changed in order to decrease it. When the active power goes below another threshold, the phase-shifter is automatically deactivated.  
With specific attribute:
- `transformer`: transformer static id

**Groovy script:**
```groovy
PhaseShifterI {
    dynamicModelId "PSI"
    parameterSetId "PS_PAR"
    transformer "TFO1"
}
```
**Json configuration:**
```json
{
  "model":"PhaseShifterI",
  "group": "PS_PAR",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "PSI",
      "type": "STRING"
    },
    {
      "name": "transformer",
      "value": "TFO1",
      "type": "STRING"
    }
  ]
}
```

#### Phase Shifter Blocking I
Blocks phase shifter I when the monitored intensity goes below a threshold.  
With specific attributes:
- `phaseShifterId`: Phase shifter I dynamic model id

**Groovy script:**
```groovy
import com.powsybl.dynawo.models.TransformerSide

PhaseShifterBlockingI {
    dynamicModelId "PSB"
    parameterSetId "PSB_PAR"
    phaseShifterId "PS"
}
```
**Json configuration:**
```json
{
  "model":"PhaseShifterBlockingI",
  "group": "PSB_PAR",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "PSB",
      "type": "STRING"
    },
    {
      "name": "phaseShifterId",
      "value": "PS",
      "type": "STRING"
    }
  ]
}
```

#### Tap Changer
Tap changer automation system added to a transformer.  
With specific attributes:
- `staticId`: load mapped to a dynamic model with transformer
- `side`: transformer side where the tap changer is added, can be: 
  - `HIGH_VOLTAGE`
  - `LOW_VOLTAGE`
  - `NONE` (default)

**Groovy script:**
```groovy
import com.powsybl.dynawo.models.TransformerSide

TapChangerAutomaton {
    dynamicModelId "TC"
    parameterSetId "TC"
    staticId "LOAD"
    side TransformerSide.HIGH_VOLTAGE
}
```
**Json configuration:**
```json
{
  "model":"TapChangerAutomaton",
  "group": "TC",
  "properties":[
    {
      "name": "dynamicModelId",
      "value": "TC",
      "type": "STRING"
    },
    {
      "name": "staticId",
      "value": "LOAD",
      "type": "STRING"
    },
    {
      "name": "side",
      "value": "HIGH_VOLTAGE",
      "type": "STRING"
    }
  ]
}
```

#### Tap Changer Blocking Automaton
Blocks tap changers when one of the monitored voltages goes below a threshold.   
With specific attributes:
- `transformers`: static ids handling three types of equipment:
  - transformer
  - load mapped to a dynamic model with transformer
  - tap changer automation system (referenced by its dynamic model id)
- `uMeasurements`: up to five bus or busbar section ids, can be defined in two ways:
  - a simple list of ids, one id for each measurement point
  - a list of list of ids, each list containing a probable measurement point id, the first one found in the network will be used

**Groovy script:**
```groovy
def measurementsTCB1 = ["S1VL2_BBS1", "S3VL1_B1"]
List[] measurementsTCB2 = [["OldId", "NGEN", "NHV1"], ["NHV1", "OldId"], ["NHV2"]]

TapChangerBlockingAutomaton {
    dynamicModelId "TCB1"
    parameterSetId "TCB"
    uMeasurements measurementsTCB1
    transformers "TWT", "LD1"
}

TapChangerBlockingAutomaton {
    dynamicModelId "TCB2"
    parameterSetId "TCB"
    uMeasurements measurementsTCB2
    transformers "NGEN_NHV1", "NHV2_NLOAD", "LOAD"
}
```
**Json configuration:**
```json
[
  {
    "model":"TapChangerBlockingAutomaton",
    "group": "TCB",
    "properties":[
      {
        "name": "dynamicModelId",
        "value": "TCB1",
        "type": "STRING"
      },
      {
        "name": "transformers",
        "values": ["TWT", "LD1"],
        "type": "STRING"
      },
      {
        "name": "uMeasurements",
        "values": ["S1VL2_BBS1", "S3VL1_B1"],
        "type": "STRING"
      }
    ]
  },
  {
    "model":"TapChangerBlockingAutomaton",
    "group": "TCB",
    "properties":[
      {
        "name": "dynamicModelId",
        "value": "TCB2",
        "type": "STRING"
      },
      {
        "name": "transformers",
        "values": ["NGEN_NHV1", "NHV2_NLOAD", "LOAD"],
        "type": "STRING"
      },
      {
        "name": "uMeasurements",
        "arrays": [["OldId", "NGEN", "NHV1"], ["NHV1", "OldId"], ["NHV2"]],
        "type": "STRING"
      }
    ]
  }
]
```

## Supported models
Models are listed in [models.json](../../dynawo/src/main/resources/models.json).  
The list is divided in categories each linked to a dedicated builder.
### Categories properties
* `defaultLib` : name of the default library
* `libs` : list of dynawo libraries supported for this category

The list is statically loaded via [ModelConfigLoader](https://javadoc.io/doc/com.powsybl/powsybl-dynawo/latest/com/powsybl/dynawo/builders/ModelConfigLoader.html) services and thus can be extended.

### Library properties
* `lib`: library name used in dynawo
* `alias`: name used in powsybl-dynawo instead of lib
* `properties`: dynamic model properties (synchronized, dangling, etc.)
* `internalModelPrefix`: used for dynamic model file creation
* `doc`: library documentation
* `minVersion`: Dynawo minimum version required
* `maxVersion`: Dynawo maximum version required
* `endCause`: explains the cause of the model ending at `maxVersion` 

## Dynamic model Builder List
Ultimately, all groovy scripts call dedicated builders that can be used directly by developers.
### Equipments
* BaseStaticVarCompensatorBuilder
* BaseShuntBuilder
* HvdcPBuilder
* HvdcVscBuilder
* BaseGeneratorBuilder
* SignalNGeneratorBuilder
* SynchronizedGeneratorBuilder
* SynchronousGeneratorBuilder
* WeccBuilder
* GridFormingConverterBuilder
* LineBuilder
* StandardBusBuilder
* InfiniteBusBuilder
* TransformerFixedRatioBuilder
* BaseLoadBuilder
* LoadOneTransformerBuilder
* LoadTwoTransformersBuilder
* LoadOneTransformerTapChangerBuilder
* LoadTwoTransformersTapChangersBuilder
### Automation Systems
* TapChangerAutomationSystemBuilder
* TapChangerBlockingAutomationSystemBuilder
* UnderVoltageAutomationSystemBuilder
* DynamicOverloadManagementSystemBuilder
* DynamicTwoLevelOverloadManagementSystemBuilder
* PhaseShifterPAutomationSystemBuilder
* PhaseShifterIAutomationSystemBuilder
* PhaseShifterBlockingIAutomationSystemBuilder
