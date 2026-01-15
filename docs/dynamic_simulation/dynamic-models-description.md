# Dynamic models description
Quick definition of every models used in powsybl-dynawo sorted by category.
See how to use model in a simulation [here](dynamic-models-configuration.md).

## Equipment models

### Generator

#### Base generator
- `GeneratorFictitious`: 
Fictitious generator (behaves in a similar way as an alpha-beta load).

- `GeneratorPVFixed`:
Simplified generator model with fixed active and reactive power.

- `GeneratorFictitious`:
Simplified generator model with voltage-dependent active and reactive power.


#### Synchronized generator
- `GeneratorPQ`:
Generator with fixed active and reactive power targets. The actual outputs may however vary in order to mimic frequency and voltage regulation.

- `GeneratorPV`: 
Generator with fixed active power and voltage targets. The reactive power output changes over time in order to follow the voltage target (when it is not possible to do so, the reactive power is set to the minimum or maximum value). The active power outputs may vary in order to mimic frequency regulation.

- `GeneratorPVDiagramPQ`:
Generator with fixed active power and voltage targets. The reactive power output changes over time in order to follow the voltage target (when it is not possible to do so, the reactive power is set to the minimum or maximum value). The active power outputs may vary in order to mimic frequency regulation. Active and reactive power are within the diagram PQ.

#### Synchronous generator
- `GeneratorSynchronousFourWindings`:
Four-winding synchronous generator.

- `GeneratorSynchronousFourWindingsGovCt2St4b`: 
Four-winding synchronous generator with governor GOVCT2 regulation and voltage regulator ST4B (IEEE).

- `GeneratorSynchronousFourWindingsGovSteam1St4b`: 
Four-winding synchronous generator with governor GOVSteam1 regulation and voltage regulator ST4B (IEEE).
(⚠️ Named `GeneratorSynchronousFourWindingsGovSteam1ExcIEEEST4B` in Dynawo 1.6.0 and below)

- `GeneratorSynchronousFourWindingsGovSteam1St4bPss2b`: 
Four-winding synchronous generator with governor GOVSteam1 regulation and voltage regulator ST4B (IEEE) and power system stabilizer PSS2B (IEEE).
(⚠️ Named `GeneratorSynchronousFourWindingsGovSteam1ExcIEEEST4BPssIEEE2B` in Dynawo 1.6.0 and below)

- `GeneratorSynchronousFourWindingsGovSteamEuSt4b`: 
Four-winding synchronous generator with governor GOVSteamEU (IEEE) and voltage regulator ST4B (IEEE).

- `GeneratorSynchronousFourWindingsGoverPropVRPropInt`: 
Four-winding synchronous generator with simplified proportional control for governor and simplified proportional integral (PI) control for voltage regulator.

- `GeneratorSynchronousFourWindingsIEEEG1Ac1a`: 
Four-winding synchronous generator with governor IEEEG1 regulation and voltage regulator AC1A (IEEE).

- `GeneratorSynchronousFourWindingsIEEEG1Dc1a`: 
Four-winding synchronous generator with governor IEEEG1 regulation and voltage regulator DC1A (IEEE).

- `GeneratorSynchronousFourWindingsIEEEG1IEEX2A`: 
Four-winding synchronous generator with governor IEEEG1 regulation and voltage regulator IEEX2A.

- `GeneratorSynchronousFourWindingsPmConstAc6aPss3b`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator AC6A (IEEE) and power system stabilizer PSS3B (IEEE).

- `GeneratorSynchronousFourWindingsPmConstAc7bPss3b`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator AC7B (IEEE) and power system stabilizer PSS3B (IEEE).

- `GeneratorSynchronousFourWindingsPmConstAc7cPss2c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator AC7C (IEEE) and power system stabilizer PSS2C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstAc8b`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator AC8B (IEEE).

- `GeneratorSynchronousFourWindingsPmConstAc8bPss3b`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator AC8B (IEEE) and power system stabilizer PSS3B (IEEE).

- `GeneratorSynchronousFourWindingsPmConstDc1a`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator DC1A (IEEE).

- `GeneratorSynchronousFourWindingsPmConstIEEX2A`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator IEEX2A (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt5bPss2b`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST5B (IEEE) and power system stabilizer PSS2B (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt5cPss2bScl1c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST5B (IEEE) and power system stabilizer PSS2B (IEEE) and stator current limiter SCL1C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt5cPss2bScl2c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST5C (IEEE) and power system stabilizer PSS2B (IEEE) and stator current limiter SCL2C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt6bPss3b`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST6B (IEEE) and power system stabilizer PSS3B (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt6cPss6c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST6C (IEEE) and power system stabilizer PSS6C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel2c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST6C (IEEE) and power system stabilizer PSS6C (IEEE) and over excitation limiter OEL2C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel3c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST6C (IEEE) and power system stabilizer PSS6C (IEEE) and over excitation limiter OEL3C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel4c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST6C (IEEE) and power system stabilizer PSS6C (IEEE) and over excitation limiter OEL4C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel5c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST6C (IEEE) and power system stabilizer PSS6C (IEEE) and over excitation limiter OEL5C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt7bPss2a`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST7B (IEEE) and power system stabilizer PSS2A (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt7bPss2aUel1`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST7B (IEEE) and power system stabilizer PSS2A (IEEE) and under excitation limiter UEL1 (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt7bPss2aUel2c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST7B (IEEE) and power system stabilizer PSS2A (IEEE) and under excitation limiter UEL2C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstSt9cPss2c`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator ST9C (IEEE) and power system stabilizer PSS2C (IEEE).

- `GeneratorSynchronousFourWindingsPmConstVRNordic`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator VRNordic (for Nordic 32-bus test system).

- `GeneratorSynchronousFourWindingsPmConstVRNordicTfo`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator VRNordic (for Nordic 32-bus test system) and generator transformer.

- `GeneratorSynchronousFourWindingsProportionalRegulations`: 
Four-winding synchronous generator with simplified proportional control for governor and simplified proportional control for voltage regulator.

- `GeneratorSynchronousFourWindingsTGov1Dc1a`: 
Four-winding synchronous generator with governor TGOV1 (IEC) and voltage regulator DC1A (IEEE).

- `GeneratorSynchronousFourWindingsTGov1Sexs`: 
Four-winding synchronous generator with governor TGOV1 (IEC) and voltage regulator SEXS.

- `GeneratorSynchronousFourWindingsTGov1SexsPss2a`: 
Four-winding synchronous generator with governor TGOV1 (IEC) and voltage regulator SEXS and power system stabilizer PSS2A (IEEE).
(⚠️ Named `GeneratorSynchronousFourWindingsTGov1SexsPss2A` in Dynawo 1.6.0 and below)

- `GeneratorSynchronousFourWindingsTGov3IEEX2A`: 
Four-winding synchronous generator with governor TGOV1 (IEC) and voltage regulator IEEX2A.

- `GeneratorSynchronousFourWindingsVRKundur`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator VRKundur (Kundur).

- `GeneratorSynchronousFourWindingsVRKundurPssKundur`: 
Four-winding synchronous generator with constant mechanical power and voltage regulator VRKundur (Kundur) and power system stabilizer VRKundur (Kundur).

- `GeneratorSynchronousProportionalRegulationsInternalParameters`: 
Synchronous generator, initialized with internal parameters with proportional control for governor and proportional control for voltage regulator.

- `GeneratorSynchronousThreeWindings`:
Three-winding synchronous generator.

- `GeneratorSynchronousThreeWindingsGovHydro4St4b`:
Three-winding synchronous generator with governor GovHydro4 (IEC) and voltage regulator ST4B (IEEE).

- `GeneratorSynchronousThreeWindingsGoverNordicVRNordic`:
Three-winding synchronous generator with governor GoverNordic and voltage regulator VRNordic (for Nordic 32-bus test system).

- `GeneratorSynchronousThreeWindingsGoverNordicVRNordicTfo`:
Three-winding synchronous generator with governor GoverNordic and voltage regulator VRNordic (for Nordic 32-bus test system) and generator transformer.

- `GeneratorSynchronousThreeWindingsGoverPropVRPropInt`: 
Three-winding synchronous generator with simplified proportional control for governor and simplified proportional integral (PI) control for voltage regulator.

- `GeneratorSynchronousThreeWindingsHyGovBbSex1`:
Three-winding synchronous generator with governor HYGOV and Brown Boveri Static Exciter BBSEX1 (IEC).

- `GeneratorSynchronousThreeWindingsHyGovScrx`:
Three-winding synchronous generator with governor HYGOV and exciter SCRX (IEC).

- `GeneratorSynchronousThreeWindingsHyGovScrxTfo`:
Three-winding synchronous generator with governor HYGOV and exciter SCRX (IEC) and generator transformer.

- `GeneratorSynchronousThreeWindingsIEEEG2BbSex1`:
Three-winding synchronous generator with governor IEEEG2 and Brown Boveri Static Exciter BBSEX1 (IEC).

- `GeneratorSynchronousThreeWindingsIeeeG1IeeeT1`:
Three-winding synchronous generator with governor IEEEG1 and exciter IEEET1.

- `GeneratorSynchronousThreeWindingsIeeeG1IeeeT1Tfo`:
Three-winding synchronous generator with governor IEEEG1 and exciter IEEET1 and generator transformer.

- `GeneratorSynchronousThreeWindingsIeeeG1Scrx`:
Three-winding synchronous generator with governor IEEEG1 and exciter SCRX (IEC).

- `GeneratorSynchronousThreeWindingsIeeeG1ScrxTfo`:
Three-winding synchronous generator with governor IEEEG1 and exciter SCRX (IEC) and generator transformer.

- `GeneratorSynchronousThreeWindingsPmConstExAc1`:
Three-winding synchronous generator with constant mechanical power and exciter EXAC1 (IEEE).
(⚠️ Named `GeneratorSynchronousThreeWindingsPmConstExcIeeeAc1a` in Dynawo 1.6.0)

- `GeneratorSynchronousThreeWindingsPmConstExAc1Tfo`:
Three-winding synchronous generator with constant mechanical power and exciter EXAC1 (IEEE) and generator transformer.
(⚠️ Named `GeneratorSynchronousThreeWindingsPmConstExcIeeeAc1aTfo` in Dynawo 1.6.0)

- `GeneratorSynchronousThreeWindingsPmConstScrx`:
Three-winding synchronous generator with constant mechanical power and exciter SCRX (IEC).

- `GeneratorSynchronousThreeWindingsPmConstScrxTfo`:
Three-winding synchronous generator with constant mechanical power and exciter SCRX (IEC) and generator transformer.

- `GeneratorSynchronousThreeWindingsPmConstVRNordic`:
Three-winding synchronous generator with constant mechanical power and voltage regulator VRNordic (for Nordic 32-bus test system).

- `GeneratorSynchronousThreeWindingsPmConstVRNordicTfo`:
Three-winding synchronous generator with constant mechanical power and voltage regulator VRNordic (for Nordic 32-bus test system) and generator transformer.

- `GeneratorSynchronousThreeWindingsProportionalRegulations`: 
Three-winding synchronous generator with simplified proportional control for governor and simplified proportional control for voltage regulator.


#### Signal N generator
- `GeneratorPQPropDiagramPQSignalN`:
Simplified generator PQ with a PQ diagram, based on SignalN for the frequency handling and with a proportional reactive power regulation.

- `GeneratorPQPropSignalN`:
Simplified generator PQ based on SignalN for the frequency handling and with a proportional reactive power regulation.

- `GeneratorPVDiagramPQSignalN`:
Simplified generator PV based on SignalN for the frequency handling with an N points PQ diagram.

- `GeneratorPVPropSignalN`:
Simplified  generator PV based on SignalN for the frequency handling and with a proportional voltage regulation.

- `GeneratorPVRemoteDiagramPQSignalN`:
Simplified generator PV with a PQ diagram, based on SignalN for the frequency handling and a remote voltage regulation.

- `GeneratorPVRemoteSignalN`:
Simplified generator PV based on SignalN for the frequency handling and a remote voltage regulation.

- `GeneratorPVSignalN`:
Simplified generator PV based on SignalN for the frequency handling and voltage following the voltage target URef.

- `GeneratorPVTfoDiagramPQSignalN`:
Simplified generator PV based on SignalN for the frequency handling, with a simplified transformer, a voltage regulation at stator and with an N points PQ diagram.

- `GeneratorPVTfoSignalN`:
Simplified generator PV based on SignalN for the frequency handling, with a simplified transformer and a voltage regulation at stator.


#### Inertial grid
- `InertialGrid`:
Generation system with average frequency response.

#### Wecc
- `PhotovoltaicsWeccCurrentSourceB`:
WECC Photovoltaics grid following model with plant control REPCA, current control REECB, generator/converter REGCB with a current source as interface with the grid.
(⚠️ Named `PhotovoltaicsWeccCurrentSource` in Dynawo 1.6.0 and below)

- `PhotovoltaicsWeccVoltageSourceA`:
WECC Photovoltaics grid following model with plant control REPCA, current control REECA, generator/converter REGCB with a voltage source as interface with the grid.

- `PhotovoltaicsWeccVoltageSourceB`:
WECC Photovoltaics grid following model with plant control REPCA, current control REECB, generator/converter REGCB with a voltage source as interface with the grid.
(⚠️ Named `PhotovoltaicsWeccVoltageSource` in Dynawo 1.6.0 and below)

- `WT4AWeccCurrentSource`:
WECC Wind Turbine model with a simplified drive train model WTGT A, without the plant controller, with current control REECA, and generator/convertor REGCB with a current source as interface with the grid.

- `WT4BWeccCurrentSource`: 
WECC Wind Turbine model without plant controller, with current control REECA, and generator/converter REGCB with a current source as interface with the grid.

- `WTG4AWeccCurrentSource`:
WECC Wind Turbine model with a simplified drive train model WTGTA, current control REECA, and generator/converter REGCB with a current source as interface with the grid.

- `WTG4BWeccCurrentSource`:
WECC Wind Turbine model with a simplified drive train model WTGTB, current control REECA, and generator/converter REGCB with a current source as interface with the grid.


#### Grid forming converter
- `GridFormingConverterDispatchableVirtualOscillatorControl`:
Grid forming converter model with Dispatchable Virtual Oscillator (DVO) control.

- `GridFormingConverterDroopControl`:
Grid forming converter model with droop control.

- `GridFormingConverterMatchingControl`:
Grid forming converter model with matching control.

### Load
#### Base load

- `ElectronicLoad`:
Constant power load with disconnection and reconnections depending on the voltage.

- `LoadAlphaBeta`:
Voltage-dependent load.

- `LoadAlphaBetaMotor`:
Alpha-beta load in parallel with a simple motor model.

- `LoadAlphaBetaMotorFifthOrder`:
Alpha-beta load in parallel with a fifth-order motor model.

- `LoadAlphaBetaMotorSimplified`:
Alpha-beta load in parallel with a simple motor model.

- `LoadAlphaBetaRestorative`:
Restorative load model with a voltage-dependent behavior when the voltage hits the limits.

- `LoadAlphaBetaRestorativeLimitsRecalc`:
Restorative load model with a voltage-dependent behavior when the voltage hits the limits, with limits recalculation at initialization.

- `LoadAlphaBetaRestorativeNetwork`:
Restorative load model with a voltage-dependent behavior when the voltage hits the limits, same model as in the Network.

- `LoadAlphaBetaThreeMotorFifthOrder`:
Alpha-beta load in parallel with three fifth-order motor models.

- `LoadAlphaBetaTwoMotorFifthOrder`:
Alpha-beta load in parallel with two fifth-order motor models.

- `LoadAlphaBetaTwoMotorSimplified`:
Alpha-beta load in parallel with two simplified motor models.

- `LoadPQ`: 
PQ load.

- `LoadZIP`:
ZIP coefficients load model.

#### Load one transformer
- `LoadOneTransformer`:
Load behind one transformer.

#### Load two transformers
- `LoadTwoTransformers`:
Load behind two transformers.

#### Load one transformer tap changer
- `LoadOneTransformerTapChanger`:
Load behind one transformer with a tap changer.

#### Load two transformers tap changer
- `LoadTwoTransformersTapChangers`:
Load behind two transformers with tap changers.

### HVDC
#### HVDC P
- `HvdcPQProp`:
HVDC link with a proportional reactive power control. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropDangling`:
HVDC link with a proportional reactive power control. terminal1 can inject a fixed reactive power or use the proportional control, depending on the user's choice. terminal2 is connected to a switched-off bus.

- `HvdcPQPropDanglingDiagramPQ`:
HVDC link with a proportional reactive power control and a PQ diagram. terminal1 can inject a fixed reactive power or use the proportional control, depending on the user's choice. terminal2 is connected to a switched-off bus.

- `HvdcPQPropDiagramPQ`:
HVDC link with a proportional reactive power control and a PQ diagram. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropDiagramPQEmulation`:
HVDC link with AC Emulation control for active power and a proportional reactive power control and a PQ diagram. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropDiagramPQEmulationSet`:
HVDC link with AC Emulation control for active power (initialization with PRefSet0Pu set by user) and a proportional reactive power control and a PQ diagram. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropDiagramPQEmulationVariableK`:
HVDC link with AC Emulation control for active power (initialization with a variable KACEmulation) and a proportional reactive power control and a PQ diagram. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropEmulation`:
HVDC link with AC Emulation control for active power and a proportional reactive power control. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropEmulationSet`:
HVDC link with AC Emulation control for active power (initialization with PRefSet0Pu set by user) and a proportional reactive power control. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPQPropEmulationVariableK`:
HVDC link with AC Emulation control for active power (initialization with a variable KACEmulation) and a proportional reactive power control. Each terminal can inject a fixed reactive power or use the proportional control, depending on the user's choice.

- `HvdcPTanPhi`:
HVDC link with P and Q=tan(Phi)*P regulation.

- `HvdcPTanPhiDangling`:
HVDC link with P and Q=tan(Phi)*P regulation with terminal2 connected to a switched-off bus.

- `HvdcPTanPhiDanglingDiagramPQ`:
HVDC link with P and Q=tan(Phi)*P regulation, a PQ diagram and with terminal2 connected to a switched-off bus.

- `HvdcPTanPhiDiagramPQ`:
HVDC link with P and Q=tan(Phi)*P regulation and a PQ diagram.

- `HvdcPV`:
PV HVDC link. Each terminal can regulate the voltage or the reactive power, depending on the user's choice.

- `HvdcPVDangling`:
PV HVDC link with terminal2 connected to a switched-off bus.

- `HvdcPVDanglingDiagramPQ`:
PV HVDC link with a PQ diagram and terminal2 connected to a switched-off bus.

- `HvdcPVDiagramPQ`:
PV HVDC link with a PQ diagram. Each terminal can regulate the voltage or the reactive power, depending on the user's choice.

- `HvdcPVDiagramPQEmulation`:
PV HVDC link with AC Emulation control for active power and a PQ diagram. Each terminal can regulate the voltage or the reactive power, depending on the user's choice.

- `HvdcPVDiagramPQEmulationSet`:
PV HVDC link with AC Emulation control for active power (initialization with PRefSet0Pu set by user) and a PQ diagram. Each terminal can regulate the voltage or the reactive power, depending on the user's choice.

- `HvdcPVEmulation`:
PV HVDC link with AC Emulation control for active power. Each terminal can regulate the voltage or the reactive power, depending on the user's choice.

- `HvdcPVEmulationSet`:
PV HVDC link with AC Emulation control for active power (initialization with PRefSet0Pu set by user). Each terminal can regulate the voltage or the reactive power, depending on the user's choice.

#### HVDC VSC
- `HvdcVsc`:
Standard dynamic model for Hvdc-Vsc link.
(⚠️ Named `HvdcVSC` in Dynawo 1.5.0 and below)

- `HvdcVscDanglingP`:
Standard dynamic model for Hvdc-Vsc link between two different synchronous areas (P control on the main one).
(⚠️ Named `HvdcVSCDanglingP` in Dynawo 1.5.0 and below)

- `HvdcVscDanglingUDc`:
Standard dynamic model for Hvdc-Vsc link between two different synchronous areas (DC  control on the main one).
(⚠️ Named `HvdcVSCDanglingUdc` in Dynawo 1.5.0 and below)

- `HvdcVscEmulation`:
Standard dynamic model for Hvdc-Vsc link with AC emulation.
(⚠️ Named `HvdcVSCEmulation` in Dynawo 1.5.0 and below)


#### Static var Compensator
- `StaticVarCompensator`:
Standard static var compensator.

- `StaticVarCompensatorPV`:
PV static var compensator model without mode handling.

- `StaticVarCompensatorPVModeHandling`:
PV static var compensator model with mode handling.

- `StaticVarCompensatorPVProp`:
PV static var compensator model with slope without mode handling.

- `StaticVarCompensatorPVPropModeHandling`:
PV static var compensator model with slope and mode handling.

- `StaticVarCompensatorPVPropRemote`:
PV static var compensator model with remote voltage regulation, slope and without mode handling.

- `StaticVarCompensatorPVPropRemoteModeHandling`:
PV static var compensator model with remote voltage regulation, slope and mode handling.

- `StaticVarCompensatorPVRemote`:
PV static var compensator model with remote voltage regulation without mode handling.

- `StaticVarCompensatorPVRemoteModeHandling`:
PV static var compensator model with remote voltage regulation, slope and mode handling.

#### Shunt
- `ShuntB`:
Shunt element with constant susceptance, reactive power depends on voltage.

- `ShuntBWithSections`:
Shunt element with voltage-dependent reactive power and a variable susceptance given by a table and a section.

### Bus

#### Base bus
- `Bus`:
Standard bus.

#### Infinite bus
- `InfiniteBus`:
Infinite bus.

- `InfiniteBusWithImpedance`:
Infinite bus connected to an impedance.

- `InfiniteBusWithVariations`:
Infinite bus with configurable variations on the voltage module and on the frequency.

#### Line
- `Line`:
Standard line.

#### Transformer
- `NetworkTransformer`:
Two-winding transformer with a fixed ratio, same model as the Network one.

- `TransformerFixedRatio`:
Two-winding transformer with a fixed ratio.

## Automation system model

### Overload management system
- `OverloadManagementSystem`:
Automation system which emits a specific order when the current on the monitored line exceeds a given threshold for a given duration.

### Two level overload management system
- `TwoLevelOverloadManagementSystem`:
Automation system which emits a specific order when the current on two monitored lines exceeds a given threshold for a given duration.

### Phase shifter I
- `PhaseShifterI`:
Phase-shifter which monitors a given current. When the current goes above a given threshold, taps will be changed in order to decrease it. When the active power goes below another threshold, the phase-shifter is automatically deactivated.

### Phase shifter blocking I
- `PhaseShifterBlockingI`:
Blocks phase shifter I when the monitored intensity goes below a threshold.

### Phase shifter P
- `PhaseShifterP`:
Phase-shifter which monitors a given active power. When the active power goes above a given threshold, taps will be changed in order to decrease it. When the active power goes below another threshold, the phase-shifter is automatically deactivated.

### Tap changer automaton
- `TapChangerAutomationSystem`:
Tap changer.

### Tap changer blocking automaton
- `TapChangerBlockingAutomationSystem`:
Blocks tap changers when one of the monitored voltages goes below a threshold.

### Under voltage
- `UnderVoltage`:
Sends switch-off signal to the monitored generator when the voltage goes below a threshold.
