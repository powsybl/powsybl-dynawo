# Dynamic models description
Quick definition of every models used in powsybl-dynawo sorted by category.
See how to use model in a simulation [here](dynamic-models-configuration.md).

## Equipment models

### Generator

#### Base generator
- GeneratorFictitious: 
Fictitious generator (behaves in a similar way as an alpha-beta load)

- GeneratorPVFixed

- GeneratorFictitious


#### Synchronized generator
- GeneratorPQ:
Generator with fixed active and reactive power targets. The actual outputs may however vary in order to mimic frequency and voltage regulation.

- GeneratorPV: 
Generator with fixed active power and voltage targets. The reactive power output changes over time in order to follow the voltage target (when it is not possible to do so, the reactive power is set to the minimum or maximum value). The active power outputs may vary in order to mimic frequency regulation

- GeneratorPVDiagramPQ

#### Synchronous generator
- GeneratorSynchronousFourWindings:
Four windings synchronous generator

- GeneratorSynchronousFourWindingsGovCt2St4b

- GeneratorSynchronousFourWindingsGovSteam1ExcIEEEST4B

- GeneratorSynchronousFourWindingsGovSteam1ExcIEEEST4BPssIEEE2B

- GeneratorSynchronousFourWindingsGovSteam1St4b

- GeneratorSynchronousFourWindingsGovSteam1St4bPss2b

- GeneratorSynchronousFourWindingsGovSteamEuSt4b

- GeneratorSynchronousFourWindingsGoverPropVRPropInt

- GeneratorSynchronousFourWindingsIEEEG1Ac1a

- GeneratorSynchronousFourWindingsIEEEG1Dc1a

- GeneratorSynchronousFourWindingsIEEEG1IEEX2A

- GeneratorSynchronousFourWindingsPmConstAc6aPss3b

- GeneratorSynchronousFourWindingsPmConstAc7bPss3b

- GeneratorSynchronousFourWindingsPmConstAc7cPss2c

- GeneratorSynchronousFourWindingsPmConstAc8b

- GeneratorSynchronousFourWindingsPmConstAc8bPss3b

- GeneratorSynchronousFourWindingsPmConstDc1a

- GeneratorSynchronousFourWindingsPmConstIEEX2A

- GeneratorSynchronousFourWindingsPmConstSt5bPss2b

- GeneratorSynchronousFourWindingsPmConstSt5cPss2bScl1c

- GeneratorSynchronousFourWindingsPmConstSt5cPss2bScl2c

- GeneratorSynchronousFourWindingsPmConstSt6bPss3b

- GeneratorSynchronousFourWindingsPmConstSt6cPss6c

- GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel2c

- GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel3c

- GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel4c

- GeneratorSynchronousFourWindingsPmConstSt6cPss6cOel5c

- GeneratorSynchronousFourWindingsPmConstSt7bPss2a

- GeneratorSynchronousFourWindingsPmConstSt7bPss2aUel1

- GeneratorSynchronousFourWindingsPmConstSt7bPss2aUel2c

- GeneratorSynchronousFourWindingsPmConstSt9cPss2c

- GeneratorSynchronousFourWindingsPmConstVRNordic

- GeneratorSynchronousFourWindingsPmConstVRNordicTfo

- GeneratorSynchronousFourWindingsProportionalRegulations

- GeneratorSynchronousFourWindingsTGov1Dc1a

- GeneratorSynchronousFourWindingsTGov1Sexs

- GeneratorSynchronousFourWindingsTGov1SexsPss2A

- GeneratorSynchronousFourWindingsTGov1SexsPss2a

- GeneratorSynchronousFourWindingsTGov3IEEX2A

- GeneratorSynchronousFourWindingsVRKundur

- GeneratorSynchronousFourWindingsVRKundurPssKundur

- GeneratorSynchronousProportionalRegulationsInternalParameters

- GeneratorSynchronousThreeWindings:
Three windings synchronous generator

- GeneratorSynchronousThreeWindingsGovHydro4St4b

- GeneratorSynchronousThreeWindingsGoverNordicVRNordic

- GeneratorSynchronousThreeWindingsGoverNordicVRNordicTfo

- GeneratorSynchronousThreeWindingsGoverPropVRPropInt

- GeneratorSynchronousThreeWindingsHyGovBbSex1

- GeneratorSynchronousThreeWindingsHyGovScrx

- GeneratorSynchronousThreeWindingsHyGovScrxTfo

- GeneratorSynchronousThreeWindingsIEEEG2BbSex1

- GeneratorSynchronousThreeWindingsIeeeG1IeeeT1

- GeneratorSynchronousThreeWindingsIeeeG1IeeeT1Tfo

- GeneratorSynchronousThreeWindingsIeeeG1Scrx

- GeneratorSynchronousThreeWindingsIeeeG1ScrxTfo

- GeneratorSynchronousThreeWindingsPmConstExAc1

- GeneratorSynchronousThreeWindingsPmConstExAc1Tfo

- GeneratorSynchronousThreeWindingsPmConstExcIeeeAc1a

- GeneratorSynchronousThreeWindingsPmConstExcIeeeAc1aTfo

- GeneratorSynchronousThreeWindingsPmConstScrx

- GeneratorSynchronousThreeWindingsPmConstScrxTfo

- GeneratorSynchronousThreeWindingsPmConstVRNordic

- GeneratorSynchronousThreeWindingsPmConstVRNordicTfo

- GeneratorSynchronousThreeWindingsProportionalRegulations


#### Signal N generator
- GeneratorPQPropDiagramPQSignalN

- GeneratorPQPropSignalN

- GeneratorPVDiagramPQSignalN

- GeneratorPVPropSignalN

- GeneratorPVRemoteDiagramPQSignalN

- GeneratorPVRemoteSignalN

- GeneratorPVSignalN

- GeneratorPVTfoDiagramPQSignalN

- GeneratorPVTfoSignalN


#### Inertial grid
- InertialGrid


#### Wecc
- PhotovoltaicsWeccCurrentSource

- PhotovoltaicsWeccCurrentSourceB

- PhotovoltaicsWeccVoltageSource

- PhotovoltaicsWeccVoltageSourceA

- PhotovoltaicsWeccVoltageSourceB

- WT4AWeccCurrentSource

- WT4BWeccCurrentSource

- WTG4AWeccCurrentSource

- WTG4BWeccCurrentSource


#### Grid forming converter
- GridFormingConverterDispatchableVirtualOscillatorControl

- GridFormingConverterDroopControl

- GridFormingConverterMatchingControl

### Load
#### Base load

- ElectronicLoad

- LoadAlphaBeta
Voltage-dependent load

- LoadAlphaBetaMotor

- LoadAlphaBetaMotorFifthOrder

- LoadAlphaBetaMotorSimplified

- LoadAlphaBetaRestorative:
Restorative load model with a voltage-dependent behavior when the voltage hits the limits

- LoadAlphaBetaRestorativeLimitsRecalc

- LoadAlphaBetaRestorativeNetwork

- LoadAlphaBetaThreeMotorFifthOrder

- LoadAlphaBetaTwoMotorFifthOrder

- LoadAlphaBetaTwoMotorSimplified

- LoadPQ: 
PQ load

- LoadZIP

#### Load one transformer
- LoadOneTransformer:
Load behind one transformer

#### Load two transformers
- LoadTwoTransformers:
Load behind two transformers

#### Load one transformer tap changer
- LoadOneTransformerTapChanger:
Load behind one transformer with a tap changer

#### Load two transformers tap changer
- LoadTwoTransformersTapChangers:
Load behind two transformers with tap changers

### HVDC
#### HVDC P
- HvdcPQProp

- HvdcPQPropDangling

- HvdcPQPropDanglingDiagramPQ

- HvdcPQPropDiagramPQ

- HvdcPQPropDiagramPQEmulation

- HvdcPQPropDiagramPQEmulationSet

- HvdcPQPropDiagramPQEmulationVariableK

- HvdcPQPropEmulation

- HvdcPQPropEmulationSet

- HvdcPQPropEmulationVariableK

- HvdcPTanPhi

- HvdcPTanPhiDangling

- HvdcPTanPhiDanglingDiagramPQ

- HvdcPTanPhiDiagramPQ

- HvdcPV

- HvdcPVDangling

- HvdcPVDanglingDiagramPQ

- HvdcPVDiagramPQ

- HvdcPVDiagramPQEmulation

- HvdcPVDiagramPQEmulationSet

- HvdcPVEmulation

- HvdcPVEmulationSet

#### HVDC VSC
- HvdcVSC:
Standard dynamic model for Hvdc-VSC link

- HvdcVSCDanglingP:
Standard dynamic model for Hvdc-VSC link between two different synchronous areas (P control on the main one)

- HvdcVSCDanglingUdc:
Standard dynamic model for Hvdc-VSC link between two different synchronous areas (P control on the main one)

- HvdcVSCEmulation:
Standard dynamic model for Hvdc-VSC link with AC emulation

- HvdcVsc:
Standard dynamic model for Hvdc-Vsc link

- HvdcVscDanglingP:
Standard dynamic model for Hvdc-Vsc link between two different synchronous areas (P control on the main one)

- HvdcVscDanglingUDc:
Standard dynamic model for Hvdc-Vsc link between two different synchronous areas (P control on the main one)

- HvdcVscEmulation:
Standard dynamic model for Hvdc-Vsc link with AC emulation


#### Static var Compensator
- StaticVarCompensator:
Standard static var compensator

- StaticVarCompensatorPV

- StaticVarCompensatorPVModeHandling

- StaticVarCompensatorPVProp

- StaticVarCompensatorPVPropModeHandling

- StaticVarCompensatorPVPropRemote

- StaticVarCompensatorPVPropRemoteModeHandling

- StaticVarCompensatorPVRemote

- StaticVarCompensatorPVRemoteModeHandling

#### Shunt
- ShuntB

- ShuntBWithSections

### Bus

#### Base bus
- Bus:
Standard bus

#### Infinite bus
- InfiniteBus

- InfiniteBusWithImpedance

- InfiniteBusWithVariations

#### Line
- Line:
Standard line

#### Transformer
- NetworkTransformer

- TransformerFixedRatio


## Automation system model

### Overload management system
- OverloadManagementSystem:
Automation system which emits a specific order when the current on the monitored line exceeds a given threshold for a given duration

### Two level overload management system
- TwoLevelOverloadManagementSystem:
Automation system which emits a specific order when the current on two monitored lines exceeds a given threshold for a given duration

### Phase shifter I
- PhaseShifterI:
Phase-shifter which monitors a given current. When the current goes above a given threshold, taps will be changed in order to decrease it. When the active power goes below another threshold, the phase-shifter is automatically deactivated

### Phase shifter blocking I
- PhaseShifterBlockingI:
Blocks phase shifter I when the monitored intensity goes below a threshold

### Phase shifter P
- PhaseShifterP:
Phase-shifter which monitors a given active power. When the active power goes above a given threshold, taps will be changed in order to decrease it. When the active power goes below another threshold, the phase-shifter is automatically deactivated

### Tap changer automaton
- TapChangerAutomaton:
Tap changer

### Tap changer blocking automaton
- TapChangerBlockingAutomaton:
Blocks tap changers when one of the monitored voltages goes below a threshold

### Under voltage
- UnderVoltage:
Sends switch-off signal to the monitored generator when the voltage goes below a threshold
