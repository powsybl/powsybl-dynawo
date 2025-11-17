# Dynawo extensions

This document describes the Dynawo-specific extensions that can be added to various network elements in a Dynawo dynamic simulation model. These extensions provide additional metadata and configuration options for elements such as generators, networks, and voltage levels.

## Synchronized generator properties

This extension is used to configure Dynawo-specific metadata for synchronized generators.  
This extension is attached to a [generator](inv:powsyblcore:*:*#network_subnetwork.md#generator).

| Attribute | Type | Unit | Required | Default value | Description |
|-----------|------|------|----------|------|-------------|
| type | string | - | yes      | -    | The generator model/driver identifier used by Dynawo. |
| rpcl | enum (RpclType) | - | no       | `NONE` | RPCL1 is not supported. |

Here is how to add a synchronized generator properties extension to a generator:
```java
generator.newExtension(SynchronizedGeneratorPropertiesAdder.class)
    .withType("MySyncGenType")
    .withRpcl(RpclType.RPCL2)
    .add();
```

## Synchronous generator properties
This extension is used to configure Dynawo-specific metadata for synchronous generators.  
This extension is attached to a [generator](inv:powsyblcore:*:*#network_subnetwork.md#generator).

| Attribute | Type | Unit | Required                                    | Default value | Description |
|-----------|------|-----|---------------------------------------------|---------------|-------------|
| numberOfWindings | enum (Windings) | - | yes                                         | -             | Defines the number of windings of the synchronous generator. Options: `THREE_WINDINGS`, `FOUR_WINDINGS`. |
| governor | string | - | only if the voltageRregulator is instancied | -             | The governor model used by the generator. |
| voltageRegulator | string |  | only if the governor is instancied          | -             | The voltage regulator model used by the generator. |
| pss | string | - | no                                          | -             | The power system stabilizer model used by the generator. |
| auxiliaries | boolean | - | no                                          | -             | Indicates whether auxiliaries are present. |
| internalTransformer | boolean | - | no                                          | -             | Indicates if the generator includes an internal transformer. |
| rpcl | enum (RpclType) | - | no                                          | `NONE`         | RPCL mode used by the generator. Use `isRpcl1()` and `isRpcl2()` to check specific modes. |
| uva | enum (Uva) | - | no                                          | `LOCAL`       | Type of voltage measurement. Options: `LOCAL`, `DISTANT`. |
| aggregated | boolean | - | no                                          | -             | Indicates if the generator is aggregated. |
| qlim | boolean | - | no                                          | -             | Indicates if Q limits are applied. |

Here is how to add a synchronous generator properties extension to a generator:
```java
generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
    .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
    .withGovernor("GovModel")
    .withVoltageRegulator("VRModel")
    .withPss("PSSModel")
    .withAuxiliaries(true)
    .withInternalTransformer(false)
    .withRpcl(RpclType.RPCL2)
    .withUva(SynchronousGeneratorProperties.Uva.LOCAL)
    .withAggregated(true)
    .withQlim(true)
    .add();
```

## Generator connection level

This extension is used to define the connection level of a generator.  
This extension is attached to a [generator](inv:powsyblcore:*:*#network_subnetwork.md#generator).

| Attribute | Type | Unit | Required | Default value | Description |
|-----------|------|------|----------|---------------|-------------|
| level | enum (GeneratorConnectionLevelType) | - | yes | - | Defines the generator connection level. Options: `TSO` or `DSO`|

Here is how to add a generator connection level extension to a generator:
```java
generator.newExtension(GeneratorConnectionLevelAdder.class)
    .withLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType.TSO)
    .add();
```

## Voltage level load characteristics

This extension is used to define the load characteristic type for a voltage level.  
This extension is attached to a [voltage level](inv:powsyblcore:*:*#network_subnetwork.md#voltagelevel).

| Attribute | Type | Unit | Required | Default value | Description |
|-----------|------|------|----------|---------------|-------------|
| characteristic | enum (Type) | - | yes | - | Defines the load characteristic type. Options: `INDUSTRIAL` or `CONSTANT`. |

Here is how to add a voltage level load characteristics extension to a voltage level:
```java
voltageLevel.newExtension(VoltageLevelLoadCharacteristicsAdder.class)
    .withCharacteristic(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL)
    .add();
```

