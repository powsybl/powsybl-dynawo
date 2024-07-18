# Dynamic Models DSL

The Dynamic Models DSL is a domain specific language written in groovy for the creation dynamic models used by Dynawo, most of them associated with a static equipment present in the network.
If some equipments are not configured, Dynawo would use a default model and set of parameters.

## BlackBoxModel
All the models supported are `BlackBoxModel`. This kind of dynamic model have three attributes:
- `lib` refers to the dynamic model library used in Dynawo.
- `dynamicModelId` identifies the model.
- `parameterSetId` refers a set of parameters for this model in one of the network parameters file.

## Equipment models
These models are `BlackBoxModel` matching an IIDM equipment (generator, load, ...).
To instantiate them with DSL, you have to use their model name as a keyword and define at least a `staticId` and a `parameterSetId`.
The `dynamicModelId` is optional and would be equal to the `staticId` if not set.

**Example**
```groovy
GeneratorSynchronousThreeWindings {
    staticId '<GENERATOR_ID>'
    dynamicModelId '<DYNAMIC_MODEL_ID>'
    parameterSetId '<PARAMETER_ID>'
}
LoadAlphaBeta {
    staticId '<LOAD_ID>'
    parameterSetId '<PARAMETER_ID>'
}
```

## Automation system models
These models are `BlackBoxModel` of automation system without IIDM equipment.

### OverloadManagementSystem
This model connect to IIDM line or two windings transformer. Besides `dynamicModelId` and `parameterSetId` it needs:
-  one control branch
-  one measurement branch (can be the controlled one)
-  which side is monitored.

**Example**
```groovy
OverloadManagementSystem {
    dynamicModelId '<DYN_ID>'
    parameterSetId '<PARAMETER_ID>'
    controlledBranch '<BRANCH_ID>'
    iMeasurement '<BRANCH_ID>'
    iMeasurementSide TwoSides.TWO
}
```

#### TODO add all automation systems

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
* `internalModelPrefix`: used for dyd file creation

## Dynamic model Builder List
Ultimately, all groovy scripts call dedicated builders that can be used directly by developers.
### Equipments
* BaseStaticVarCompensatorBuilder
* BaseShuntBuilder
* HvdcPBuilder
* HvdcVscBuilder
* GeneratorFictitiousBuilder
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
* DynamicTwoLevelsOverloadManagementSystemBuilder
* PhaseShifterPAutomationSystemBuilder
* PhaseShifterIAutomationSystemBuilder
* PhaseShifterBlockingIAutomationSystemBuilder
