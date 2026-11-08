/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy;

import com.powsybl.dynawo.cgmesdy.governors.gas.*;
import com.powsybl.dynawo.cgmesdy.governors.hydro.*;
import com.powsybl.dynawo.cgmesdy.governors.steam.*;
import com.powsybl.dynawo.cgmesdy.exciters.ac.*;
import com.powsybl.dynawo.cgmesdy.exciters.dc.*;
import com.powsybl.dynawo.cgmesdy.exciters.st.*;
import com.powsybl.dynawo.cgmesdy.exciters.vendor.*;
import com.powsybl.dynawo.cgmesdy.pss.*;
import com.powsybl.dynawo.cgmesdy.synchronous.*;
import com.powsybl.dynawo.cgmesdy.asynchronous.*;
import com.powsybl.dynawo.cgmesdy.protection.VCompIEEEType1;
import com.powsybl.dynawo.cgmesdy.wind.*;
import com.powsybl.dynawo.cgmesdy.load.*;
import com.powsybl.dynawo.cgmesdy.hvdc.*;
import com.powsybl.dynawo.cgmesdy.protection.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for all dynamic model instances parsed from a CGMES DY profile file.
 *
 * <p>Each list holds model instances of the corresponding IEC 61970-302 CIM class.
 * Models are stored as immutable Java records and serve as an intermediate
 * representation before mapping to the powsybl-dynawo internal structure.</p>
 *
 * <p>Usage:
 * <pre>{@code
 *   CgmesDyImporter importer = new CgmesDyImporter();
 *   CgmesDyModel model = importer.importDy(dataSource);
 *   model.govSteam1List().forEach(gs -> ...);
 * }</pre>
 * </p>
 *
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public final class CgmesDyModel {

    // =========================================================================
    // Governors – Steam
    // =========================================================================
    private final List<GovSteam0> govSteam0List = new ArrayList<>();
    private final List<GovSteam1> govSteam1List = new ArrayList<>();
    private final List<GovSteam2> govSteam2List = new ArrayList<>();
    private final List<GovSteamCC> govSteamCCList = new ArrayList<>();
    private final List<GovSteamEU> govSteamEUList = new ArrayList<>();
    private final List<GovSteamFV2> govSteamFV2List = new ArrayList<>();
    private final List<GovSteamFV3> govSteamFV3List = new ArrayList<>();
    private final List<GovSteamFV4> govSteamFV4List = new ArrayList<>();
    private final List<GovSteamIEEE1> govSteamIEEE1List = new ArrayList<>();
    private final List<GovSteamSGO> govSteamSGOList = new ArrayList<>();

    // =========================================================================
    // Governors – Hydro
    // =========================================================================
    private final List<GovHydro1> govHydro1List = new ArrayList<>();
    private final List<GovHydro2> govHydro2List = new ArrayList<>();
    private final List<GovHydro3> govHydro3List = new ArrayList<>();
    private final List<GovHydro4> govHydro4List = new ArrayList<>();
    private final List<GovHydroDD> govHydroDDList = new ArrayList<>();
    private final List<GovHydroFrancis> govHydroFrancisList = new ArrayList<>();
    private final List<GovHydroIEEE0> govHydroIEEE0List = new ArrayList<>();
    private final List<GovHydroIEEE2> govHydroIEEE2List = new ArrayList<>();
    private final List<GovHydroPelton> govHydroPeltonList = new ArrayList<>();
    private final List<GovHydroPID> govHydroPIDList = new ArrayList<>();
    private final List<GovHydroPID2> govHydroPID2List = new ArrayList<>();
    private final List<GovHydroR> govHydroRList = new ArrayList<>();
    private final List<GovHydroWEH> govHydroWEHList = new ArrayList<>();
    private final List<GovHydroWPID> govHydroWPIDList = new ArrayList<>();

    // =========================================================================
    // Governors – Gas / Combined Cycle
    // =========================================================================
    private final List<GovGAST> govGASTList = new ArrayList<>();
    private final List<GovGAST1> govGAST1List = new ArrayList<>();
    private final List<GovGAST2> govGAST2List = new ArrayList<>();
    private final List<GovGAST3> govGAST3List = new ArrayList<>();
    private final List<GovGAST4> govGAST4List = new ArrayList<>();
    private final List<GovGASTWD> govGASTWDList = new ArrayList<>();
    private final List<GovCT1> govCT1List = new ArrayList<>();
    private final List<GovCT2> govCT2List = new ArrayList<>();

    // =========================================================================
    // Exciters – IEEE DC
    // =========================================================================
    private final List<ExcIEEEDC1A> excIEEEDC1AList = new ArrayList<>();
    private final List<ExcIEEEDC2A> excIEEEDC2AList = new ArrayList<>();
    private final List<ExcIEEEDC3A> excIEEEDC3AList = new ArrayList<>();
    private final List<ExcIEEEDC4B> excIEEEDC4BList = new ArrayList<>();

    // =========================================================================
    // Exciters – IEEE AC
    // =========================================================================
    private final List<ExcIEEEAC1A> excIEEEAC1AList = new ArrayList<>();
    private final List<ExcIEEEAC2A> excIEEEAC2AList = new ArrayList<>();
    private final List<ExcIEEEAC3A> excIEEEAC3AList = new ArrayList<>();
    private final List<ExcIEEEAC4A> excIEEEAC4AList = new ArrayList<>();
    private final List<ExcIEEEAC5A> excIEEEAC5AList = new ArrayList<>();
    private final List<ExcIEEEAC6A> excIEEEAC6AList = new ArrayList<>();
    private final List<ExcIEEEAC7B> excIEEEAC7BList = new ArrayList<>();
    private final List<ExcIEEEAC8B> excIEEEAC8BList = new ArrayList<>();

    // =========================================================================
    // Exciters – IEEE ST
    // =========================================================================
    private final List<ExcIEEEST1A> excIEEEST1AList = new ArrayList<>();
    private final List<ExcIEEEST2A> excIEEEST2AList = new ArrayList<>();
    private final List<ExcIEEEST3A> excIEEEST3AList = new ArrayList<>();
    private final List<ExcIEEEST4B> excIEEEST4BList = new ArrayList<>();
    private final List<ExcIEEEST5B> excIEEEST5BList = new ArrayList<>();
    private final List<ExcIEEEST6B> excIEEEST6BList = new ArrayList<>();
    private final List<ExcIEEEST7B> excIEEEST7BList = new ArrayList<>();

    // =========================================================================
    // Exciters – Vendor / Other
    // =========================================================================
    private final List<ExcAVR1> excAVR1List = new ArrayList<>();
    private final List<ExcAVR2> excAVR2List = new ArrayList<>();
    private final List<ExcAVR3> excAVR3List = new ArrayList<>();
    private final List<ExcAVR4> excAVR4List = new ArrayList<>();
    private final List<ExcAVR5> excAVR5List = new ArrayList<>();
    private final List<ExcAVR7> excAVR7List = new ArrayList<>();
    private final List<ExcBBC> excBBCList = new ArrayList<>();
    private final List<ExcCZ> excCZList = new ArrayList<>();
    private final List<ExcDC1A> excDC1AList = new ArrayList<>();
    private final List<ExcDC2A> excDC2AList = new ArrayList<>();
    private final List<ExcDC3A> excDC3AList = new ArrayList<>();
    private final List<ExcELIN1> excELIN1List = new ArrayList<>();
    private final List<ExcELIN2> excELIN2List = new ArrayList<>();
    private final List<ExcHU> excHUList = new ArrayList<>();
    private final List<ExcNI> excNIList = new ArrayList<>();
    private final List<ExcOEX3T> excOEX3TList = new ArrayList<>();
    private final List<ExcPIC> excPICList = new ArrayList<>();
    private final List<ExcREXS> excREXSList = new ArrayList<>();
    private final List<ExcRQB> excRQBList = new ArrayList<>();
    private final List<ExcSCRX> excSCRXList = new ArrayList<>();
    private final List<ExcSEXS> excSEXSList = new ArrayList<>();
    private final List<ExcSK> excSKList = new ArrayList<>();
    private final List<ExcST1A> excST1AList = new ArrayList<>();
    private final List<ExcST2A> excST2AList = new ArrayList<>();
    private final List<ExcST3> excST3List = new ArrayList<>();
    private final List<ExcST4B> excST4BList = new ArrayList<>();
    private final List<ExcST6B> excST6BList = new ArrayList<>();
    private final List<ExcST7B> excST7BList = new ArrayList<>();
    private final List<ExcSYMPTR> excSYMPTRList = new ArrayList<>();
    private final List<ExcAC1A> excAC1AList = new ArrayList<>();
    private final List<ExcAC2A> excAC2AList = new ArrayList<>();
    private final List<ExcAC3A> excAC3AList = new ArrayList<>();
    private final List<ExcAC4A> excAC4AList = new ArrayList<>();
    private final List<ExcAC5A> excAC5AList = new ArrayList<>();
    private final List<ExcAC6A> excAC6AList = new ArrayList<>();
    private final List<ExcAC8B> excAC8BList = new ArrayList<>();

    // =========================================================================
    // Power System Stabilizers
    // =========================================================================
    private final List<PssSB4> pssSB4List = new ArrayList<>();
    private final List<PssIEEE1A> pssIEEE1AList = new ArrayList<>();
    private final List<PssIEEE2B> pssIEEE2BList = new ArrayList<>();
    private final List<PssIEEE3B> pssIEEE3BList = new ArrayList<>();
    private final List<PssIEEE4B> pssIEEE4BList = new ArrayList<>();
    private final List<Pss1> pss1List = new ArrayList<>();
    private final List<Pss1A> pss1AList = new ArrayList<>();
    private final List<Pss2B> pss2BList = new ArrayList<>();
    private final List<Pss2ST> pss2STList = new ArrayList<>();
    private final List<Pss5> pss5List = new ArrayList<>();
    private final List<PssPTIST1> pssPTIST1List = new ArrayList<>();
    private final List<PssPTIST3> pssPTIST3List = new ArrayList<>();
    private final List<PssELIN2> pssELIN2List = new ArrayList<>();
    private final List<PssSH> pssSHList = new ArrayList<>();
    private final List<PssWECC> pssWECCList = new ArrayList<>();
    private final List<PssRQB> pssRQBList = new ArrayList<>();

    // =========================================================================
    // Synchronous Machines
    // =========================================================================
    private final List<SynchronousMachineSimplified> syncSimplifiedList = new ArrayList<>();
    private final List<SynchronousMachineDetailed> syncDetailedList = new ArrayList<>();
    private final List<SynchronousMachineEquivalentCircuit> syncEquivCircuitList = new ArrayList<>();
    private final List<SynchronousMachineTimeConstantReactance> syncTimeConstReactanceList = new ArrayList<>();
//    private final List<SynchronousMachineUserDefined> syncUserDefinedList = new ArrayList<>();

    // =========================================================================
    // Asynchronous Machines
    // =========================================================================
    private final List<AsynchronousMachineTimeConstantReactance> asyncTimeConstReactanceList = new ArrayList<>();
    private final List<AsynchronousMachineEquivalentCircuit> asyncEquivCircuitList = new ArrayList<>();
//    private final List<AsynchronousMachineUserDefined> asyncUserDefinedList = new ArrayList<>();

    // =========================================================================
    // Wind
    // =========================================================================
    private final List<WindGenTurbineType1aIEC> windType1aList = new ArrayList<>();
    private final List<WindGenTurbineType1bIEC> windType1bList = new ArrayList<>();
    private final List<WindGenTurbineType2IEC> windType2List = new ArrayList<>();
    private final List<WindGenTurbineType3aIEC> windType3aList = new ArrayList<>();
    private final List<WindGenTurbineType3bIEC> windType3bList = new ArrayList<>();
    private final List<WindGenTurbineType4aIEC> windType4aList = new ArrayList<>();
    private final List<WindGenTurbineType4bIEC> windType4bList = new ArrayList<>();
    private final List<WindPlantIEC> windPlantList = new ArrayList<>();
    private final List<WindMechIEC> windMechList = new ArrayList<>();
    private final List<WindAeroConstIEC> windAeroConstList = new ArrayList<>();
    private final List<WindAeroLinearIEC> windAeroLinearList = new ArrayList<>();
    private final List<WindContPitchAngleIEC> windContPitchList = new ArrayList<>();
    private final List<WindContPType3IEC> windContPType3List = new ArrayList<>();
    private final List<WindContPType4aIEC> windContPType4aList = new ArrayList<>();
    private final List<WindContPType4bIEC> windContPType4bList = new ArrayList<>();
    private final List<WindContQIEC> windContQList = new ArrayList<>();
    private final List<WindContRotorRIEC> windContRotorRList = new ArrayList<>();
    private final List<WindContCurrLimIEC> windCurrLimList = new ArrayList<>();
    private final List<WindPitchContEmulIEC> windPitchEmulList = new ArrayList<>();
    private final List<WindPlantFreqPcontrolIEC> windPlantFreqList = new ArrayList<>();
    private final List<WindPlantReactiveControlIEC> windPlantReactList = new ArrayList<>();
    private final List<WindProtectionIEC> windProtectionList = new ArrayList<>();

    // =========================================================================
    // Load
    // =========================================================================
    private final List<LoadStatic> loadStaticList = new ArrayList<>();
    private final List<LoadComposite> loadCompositeList = new ArrayList<>();
    private final List<LoadAggregate> loadAggregateList = new ArrayList<>();
    private final List<LoadMotor> loadMotorList = new ArrayList<>();
    private final List<LoadGenericNonLinear> loadGenericNLList = new ArrayList<>();
    private final List<MechLoad1> mechLoad1List = new ArrayList<>();

    // =========================================================================
    // HVDC
    // =========================================================================
    private final List<CsConverterDynamics> csConverterList = new ArrayList<>();
    private final List<VsConverterDynamics> vsConverterList = new ArrayList<>();

    // =========================================================================
    // Protection / Limiters
    // =========================================================================
    private final List<DiscExcContIEEEDEC1A> discExcDEC1AList = new ArrayList<>();
    private final List<DiscExcContIEEEDEC2A> discExcDEC2AList = new ArrayList<>();
    private final List<DiscExcContIEEEDEC3A> discExcDEC3AList = new ArrayList<>();
    private final List<OverexcLimIEEE> oelIEEEList = new ArrayList<>();
    private final List<OverexcLimX> oelXList = new ArrayList<>();
    private final List<UnderexcLimIEEE1> uelIEEE1List = new ArrayList<>();
    private final List<UnderexcLimIEEE2> uelIEEE2List = new ArrayList<>();
    private final List<UnderexcLimX1> uelX1List = new ArrayList<>();
    private final List<UnderexcLimX2> uelX2List = new ArrayList<>();
    private final List<VoltageAdjusterIEEE> voltageAdjList = new ArrayList<>();
    private final List<VoltageCompensatorIEEE> voltageCompList = new ArrayList<>();

    // =========================================================================
    // Voltage compensator
    // =========================================================================
    private final List<VCompIEEEType1> vCompIEEEType1List = new ArrayList<>();

    // =========================================================================
    // User-defined
    // =========================================================================
//    private final List<UserDefinedModel> userDefinedList = new ArrayList<>();

    // =========================================================================
    // --- Auto-generated accessors (unmodifiable views) ---
    // =========================================================================

    // Governors – Steam
    public List<GovSteam0> govSteam0List() {
        return Collections.unmodifiableList(govSteam0List); }

    public List<GovSteam1> govSteam1List() {
        return Collections.unmodifiableList(govSteam1List); }

    public List<GovSteam2> govSteam2List() {
        return Collections.unmodifiableList(govSteam2List); }

    public List<GovSteamCC> govSteamCCList() {
        return Collections.unmodifiableList(govSteamCCList); }

    public List<GovSteamEU> govSteamEUList() {
        return Collections.unmodifiableList(govSteamEUList); }

    public List<GovSteamFV2> govSteamFV2List() {
        return Collections.unmodifiableList(govSteamFV2List); }

    public List<GovSteamFV3> govSteamFV3List() {
        return Collections.unmodifiableList(govSteamFV3List); }

    public List<GovSteamFV4> govSteamFV4List() {
        return Collections.unmodifiableList(govSteamFV4List); }

    public List<GovSteamIEEE1> govSteamIEEE1List() {
        return Collections.unmodifiableList(govSteamIEEE1List); }

    public List<GovSteamSGO> govSteamSGOList() {
        return Collections.unmodifiableList(govSteamSGOList); }

    // Governors – Hydro
    public List<GovHydro1> govHydro1List() {
        return Collections.unmodifiableList(govHydro1List); }

    public List<GovHydro2> govHydro2List() {
        return Collections.unmodifiableList(govHydro2List); }

    public List<GovHydro3> govHydro3List() {
        return Collections.unmodifiableList(govHydro3List); }

    public List<GovHydro4> govHydro4List() {
        return Collections.unmodifiableList(govHydro4List); }

    public List<GovHydroDD> govHydroDDList() {
        return Collections.unmodifiableList(govHydroDDList); }

    public List<GovHydroFrancis> govHydroFrancisList() {
        return Collections.unmodifiableList(govHydroFrancisList); }

    public List<GovHydroIEEE0> govHydroIEEE0List() {
        return Collections.unmodifiableList(govHydroIEEE0List); }

    public List<GovHydroIEEE2> govHydroIEEE2List() {
        return Collections.unmodifiableList(govHydroIEEE2List); }

    public List<GovHydroPelton> govHydroPeltonList() {
        return Collections.unmodifiableList(govHydroPeltonList); }

    public List<GovHydroPID> govHydroPIDList() {
        return Collections.unmodifiableList(govHydroPIDList); }

    public List<GovHydroPID2> govHydroPID2List() {
        return Collections.unmodifiableList(govHydroPID2List); }

    public List<GovHydroR> govHydroRList() {
        return Collections.unmodifiableList(govHydroRList); }

    public List<GovHydroWEH> govHydroWEHList() {
        return Collections.unmodifiableList(govHydroWEHList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<GovHydroWPID> govHydroWPIDList() {
        return Collections.unmodifiableList(govHydroWPIDList); }

    // Governors – Gas
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<GovGAST> govGASTList() {
        return Collections.unmodifiableList(govGASTList); }

    public List<GovGAST1> govGAST1List() {
        return Collections.unmodifiableList(govGAST1List); }

    public List<GovGAST2> govGAST2List() {
        return Collections.unmodifiableList(govGAST2List); }

    public List<GovGAST3> govGAST3List() {
        return Collections.unmodifiableList(govGAST3List); }

    public List<GovGAST4> govGAST4List() {
        return Collections.unmodifiableList(govGAST4List); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<GovGASTWD> govGASTWDList() {
        return Collections.unmodifiableList(govGASTWDList); }

    public List<GovCT1> govCT1List() {
        return Collections.unmodifiableList(govCT1List); }

    public List<GovCT2> govCT2List() {
        return Collections.unmodifiableList(govCT2List); }

    // Exciters IEEE
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEDC1A> excIEEEDC1AList() {
        return Collections.unmodifiableList(excIEEEDC1AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEDC2A> excIEEEDC2AList() {
        return Collections.unmodifiableList(excIEEEDC2AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEDC3A> excIEEEDC3AList() {
        return Collections.unmodifiableList(excIEEEDC3AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEDC4B> excIEEEDC4BList() {
        return Collections.unmodifiableList(excIEEEDC4BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC1A> excIEEEAC1AList() {
        return Collections.unmodifiableList(excIEEEAC1AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC2A> excIEEEAC2AList() {
        return Collections.unmodifiableList(excIEEEAC2AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC3A> excIEEEAC3AList() {
        return Collections.unmodifiableList(excIEEEAC3AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC4A> excIEEEAC4AList() {
        return Collections.unmodifiableList(excIEEEAC4AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC5A> excIEEEAC5AList() {
        return Collections.unmodifiableList(excIEEEAC5AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC6A> excIEEEAC6AList() {
        return Collections.unmodifiableList(excIEEEAC6AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC7B> excIEEEAC7BList() {
        return Collections.unmodifiableList(excIEEEAC7BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEAC8B> excIEEEAC8BList() {
        return Collections.unmodifiableList(excIEEEAC8BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST1A> excIEEEST1AList() {
        return Collections.unmodifiableList(excIEEEST1AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST2A> excIEEEST2AList() {
        return Collections.unmodifiableList(excIEEEST2AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST3A> excIEEEST3AList() {
        return Collections.unmodifiableList(excIEEEST3AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST4B> excIEEEST4BList() {
        return Collections.unmodifiableList(excIEEEST4BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST5B> excIEEEST5BList() {
        return Collections.unmodifiableList(excIEEEST5BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST6B> excIEEEST6BList() {
        return Collections.unmodifiableList(excIEEEST6BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcIEEEST7B> excIEEEST7BList() {
        return Collections.unmodifiableList(excIEEEST7BList); }

    // Exciters vendor
    public List<ExcAVR1> excAVR1List() {
        return Collections.unmodifiableList(excAVR1List); }

    public List<ExcAVR2> excAVR2List() {
        return Collections.unmodifiableList(excAVR2List); }

    public List<ExcAVR3> excAVR3List() {
        return Collections.unmodifiableList(excAVR3List); }

    public List<ExcAVR4> excAVR4List() {
        return Collections.unmodifiableList(excAVR4List); }

    public List<ExcAVR5> excAVR5List() {
        return Collections.unmodifiableList(excAVR5List); }

    public List<ExcAVR7> excAVR7List() {
        return Collections.unmodifiableList(excAVR7List); }

    public List<ExcBBC> excBBCList() {
        return Collections.unmodifiableList(excBBCList); }

    public List<ExcCZ> excCZList() {
        return Collections.unmodifiableList(excCZList); }

    public List<ExcDC1A> excDC1AList() {
        return Collections.unmodifiableList(excDC1AList); }

    public List<ExcDC2A> excDC2AList() {
        return Collections.unmodifiableList(excDC2AList); }

    public List<ExcDC3A> excDC3AList() {
        return Collections.unmodifiableList(excDC3AList); }

    public List<ExcELIN1> excELIN1List() {
        return Collections.unmodifiableList(excELIN1List); }

    public List<ExcELIN2> excELIN2List() {
        return Collections.unmodifiableList(excELIN2List); }

    public List<ExcHU> excHUList() {
        return Collections.unmodifiableList(excHUList); }

    public List<ExcNI> excNIList() {
        return Collections.unmodifiableList(excNIList); }

    public List<ExcOEX3T> excOEX3TList() {
        return Collections.unmodifiableList(excOEX3TList); }

    public List<ExcPIC> excPICList() {
        return Collections.unmodifiableList(excPICList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcREXS> excREXSList() {
        return Collections.unmodifiableList(excREXSList); }

    public List<ExcRQB> excRQBList() {
        return Collections.unmodifiableList(excRQBList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcSCRX> excSCRXList() {
        return Collections.unmodifiableList(excSCRXList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcSEXS> excSEXSList() {
        return Collections.unmodifiableList(excSEXSList); }

    public List<ExcSK> excSKList() {
        return Collections.unmodifiableList(excSKList); }

    public List<ExcST1A> excST1AList() {
        return Collections.unmodifiableList(excST1AList); }

    public List<ExcST2A> excST2AList() {
        return Collections.unmodifiableList(excST2AList); }

    public List<ExcST3> excST3List() {
        return Collections.unmodifiableList(excST3List); }

    public List<ExcST4B> excST4BList() {
        return Collections.unmodifiableList(excST4BList); }

    public List<ExcST6B> excST6BList() {
        return Collections.unmodifiableList(excST6BList); }

    public List<ExcST7B> excST7BList() {
        return Collections.unmodifiableList(excST7BList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<ExcSYMPTR> excSYMPTRList() {
        return Collections.unmodifiableList(excSYMPTRList); }

    public List<ExcAC1A> excAC1AList() {
        return Collections.unmodifiableList(excAC1AList); }

    public List<ExcAC2A> excAC2AList() {
        return Collections.unmodifiableList(excAC2AList); }

    public List<ExcAC3A> excAC3AList() {
        return Collections.unmodifiableList(excAC3AList); }

    public List<ExcAC4A> excAC4AList() {
        return Collections.unmodifiableList(excAC4AList); }

    public List<ExcAC5A> excAC5AList() {
        return Collections.unmodifiableList(excAC5AList); }

    public List<ExcAC6A> excAC6AList() {
        return Collections.unmodifiableList(excAC6AList); }

    public List<ExcAC8B> excAC8BList() {
        return Collections.unmodifiableList(excAC8BList); }

    // PSS
    public List<PssSB4> pssSB4List() {
        return Collections.unmodifiableList(pssSB4List); }

    public List<PssIEEE1A> pssIEEE1AList() {
        return Collections.unmodifiableList(pssIEEE1AList); }

    public List<PssIEEE2B> pssIEEE2BList() {
        return Collections.unmodifiableList(pssIEEE2BList); }

    public List<PssIEEE3B> pssIEEE3BList() {
        return Collections.unmodifiableList(pssIEEE3BList); }

    public List<PssIEEE4B> pssIEEE4BList() {
        return Collections.unmodifiableList(pssIEEE4BList); }

    public List<Pss1> pss1List() {
        return Collections.unmodifiableList(pss1List); }

    public List<Pss1A> pss1AList() {
        return Collections.unmodifiableList(pss1AList); }

    public List<Pss2B> pss2BList() {
        return Collections.unmodifiableList(pss2BList); }

    public List<Pss2ST> pss2STList() {
        return Collections.unmodifiableList(pss2STList); }

    public List<Pss5> pss5List() {
        return Collections.unmodifiableList(pss5List); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<PssPTIST1> pssPTIST1List() {
        return Collections.unmodifiableList(pssPTIST1List); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<PssPTIST3> pssPTIST3List() {
        return Collections.unmodifiableList(pssPTIST3List); }

    public List<PssELIN2> pssELIN2List() {
        return Collections.unmodifiableList(pssELIN2List); }

    public List<PssSH> pssSHList() {
        return Collections.unmodifiableList(pssSHList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<PssWECC> pssWECCList() {
        return Collections.unmodifiableList(pssWECCList); }

    public List<PssRQB> pssRQBList() {
        return Collections.unmodifiableList(pssRQBList); }

    // Sync machines
    public List<SynchronousMachineSimplified> syncSimplifiedList() {
        return Collections.unmodifiableList(syncSimplifiedList); }

    public List<SynchronousMachineDetailed> syncDetailedList() {
        return Collections.unmodifiableList(syncDetailedList); }

    public List<SynchronousMachineEquivalentCircuit> syncEquivCircuitList() {
        return Collections.unmodifiableList(syncEquivCircuitList); }

    public List<SynchronousMachineTimeConstantReactance> syncTimeConstReactanceList() {
        return Collections.unmodifiableList(syncTimeConstReactanceList); }

//    public List<SynchronousMachineUserDefined> syncUserDefinedList() {
//        return Collections.unmodifiableList(syncUserDefinedList); }

    // Async machines
    public List<AsynchronousMachineTimeConstantReactance> asyncTimeConstReactanceList() {
        return Collections.unmodifiableList(asyncTimeConstReactanceList); }

    public List<AsynchronousMachineEquivalentCircuit> asyncEquivCircuitList() {
        return Collections.unmodifiableList(asyncEquivCircuitList); }

//    public List<AsynchronousMachineUserDefined> asyncUserDefinedList() {
//        return Collections.unmodifiableList(asyncUserDefinedList); }

    // Wind
    public List<WindGenTurbineType1aIEC> windType1aList() {
        return Collections.unmodifiableList(windType1aList); }

    public List<WindGenTurbineType1bIEC> windType1bList() {
        return Collections.unmodifiableList(windType1bList); }

    public List<WindGenTurbineType2IEC> windType2List() {
        return Collections.unmodifiableList(windType2List); }

    public List<WindGenTurbineType3aIEC> windType3aList() {
        return Collections.unmodifiableList(windType3aList); }

    public List<WindGenTurbineType3bIEC> windType3bList() {
        return Collections.unmodifiableList(windType3bList); }

    public List<WindGenTurbineType4aIEC> windType4aList() {
        return Collections.unmodifiableList(windType4aList); }

    public List<WindGenTurbineType4bIEC> windType4bList() {
        return Collections.unmodifiableList(windType4bList); }

    public List<WindPlantIEC> windPlantList() {
        return Collections.unmodifiableList(windPlantList); }

    public List<WindMechIEC> windMechList() {
        return Collections.unmodifiableList(windMechList); }

    public List<WindAeroConstIEC> windAeroConstList() {
        return Collections.unmodifiableList(windAeroConstList); }

    public List<WindAeroLinearIEC> windAeroLinearList() {
        return Collections.unmodifiableList(windAeroLinearList); }

    public List<WindContPitchAngleIEC> windContPitchList() {
        return Collections.unmodifiableList(windContPitchList); }

    public List<WindContPType3IEC> windContPType3List() {
        return Collections.unmodifiableList(windContPType3List); }

    public List<WindContPType4aIEC> windContPType4aList() {
        return Collections.unmodifiableList(windContPType4aList); }

    public List<WindContPType4bIEC> windContPType4bList() {
        return Collections.unmodifiableList(windContPType4bList); }

    public List<WindContQIEC> windContQList() {
        return Collections.unmodifiableList(windContQList); }

    public List<WindContRotorRIEC> windContRotorRList() {
        return Collections.unmodifiableList(windContRotorRList); }

    public List<WindContCurrLimIEC> windCurrLimList() {
        return Collections.unmodifiableList(windCurrLimList); }

    public List<WindPitchContEmulIEC> windPitchEmulList() {
        return Collections.unmodifiableList(windPitchEmulList); }

    public List<WindPlantFreqPcontrolIEC> windPlantFreqList() {
        return Collections.unmodifiableList(windPlantFreqList); }

    public List<WindPlantReactiveControlIEC> windPlantReactList() {
        return Collections.unmodifiableList(windPlantReactList); }

    public List<WindProtectionIEC> windProtectionList() {
        return Collections.unmodifiableList(windProtectionList); }

    // Load
    public List<LoadStatic> loadStaticList() {
        return Collections.unmodifiableList(loadStaticList); }

    public List<LoadComposite> loadCompositeList() {
        return Collections.unmodifiableList(loadCompositeList); }

    public List<LoadAggregate> loadAggregateList() {
        return Collections.unmodifiableList(loadAggregateList); }

    public List<LoadMotor> loadMotorList() {
        return Collections.unmodifiableList(loadMotorList); }

    public List<LoadGenericNonLinear> loadGenericNLList() {
        return Collections.unmodifiableList(loadGenericNLList); }

    public List<MechLoad1> mechLoad1List() {
        return Collections.unmodifiableList(mechLoad1List); }

    // HVDC
    public List<CsConverterDynamics> csConverterList() {
        return Collections.unmodifiableList(csConverterList); }

    public List<VsConverterDynamics> vsConverterList() {
        return Collections.unmodifiableList(vsConverterList); }

    // Protection
    public List<DiscExcContIEEEDEC1A> discExcDEC1AList() {
        return Collections.unmodifiableList(discExcDEC1AList); }

    public List<DiscExcContIEEEDEC2A> discExcDEC2AList() {
        return Collections.unmodifiableList(discExcDEC2AList); }

    public List<DiscExcContIEEEDEC3A> discExcDEC3AList() {
        return Collections.unmodifiableList(discExcDEC3AList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<OverexcLimIEEE> oelIEEEList() {
        return Collections.unmodifiableList(oelIEEEList); }

    public List<OverexcLimX> oelXList() {
        return Collections.unmodifiableList(oelXList); }

    public List<UnderexcLimIEEE1> uelIEEE1List() {
        return Collections.unmodifiableList(uelIEEE1List); }

    public List<UnderexcLimIEEE2> uelIEEE2List() {
        return Collections.unmodifiableList(uelIEEE2List); }

    public List<UnderexcLimX1> uelX1List() {
        return Collections.unmodifiableList(uelX1List); }

    public List<UnderexcLimX2> uelX2List() {
        return Collections.unmodifiableList(uelX2List); }

    public List<VoltageAdjusterIEEE> voltageAdjList() {
        return Collections.unmodifiableList(voltageAdjList); }

    public List<VoltageCompensatorIEEE> voltageCompList() {
        return Collections.unmodifiableList(voltageCompList); }

    // User-defined
//    public List<UserDefinedModel> userDefinedList() {
//        return Collections.unmodifiableList(userDefinedList); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public List<VCompIEEEType1> vCompIEEEType1List() {
        return Collections.unmodifiableList(vCompIEEEType1List); }

    // =========================================================================
    // Package-private adders (used only by CgmesDyModelLoader)
    // =========================================================================

    public void add(GovSteam0 m) {
        govSteam0List.add(m); }

    public void add(GovSteam1 m) {
        govSteam1List.add(m); }

    public void add(GovSteam2 m) {
        govSteam2List.add(m); }

    public void add(GovSteamCC m) {
        govSteamCCList.add(m); }

    public void add(GovSteamEU m) {
        govSteamEUList.add(m); }

    public void add(GovSteamFV2 m) {
        govSteamFV2List.add(m); }

    public void add(GovSteamFV3 m) {
        govSteamFV3List.add(m); }

    public void add(GovSteamFV4 m) {
        govSteamFV4List.add(m); }

    public void add(GovSteamIEEE1 m) {
        govSteamIEEE1List.add(m); }

    public void add(GovSteamSGO m) {
        govSteamSGOList.add(m); }

    public void add(GovHydro1 m) {
        govHydro1List.add(m); }

    public void add(GovHydro2 m) {
        govHydro2List.add(m); }

    public void add(GovHydro3 m) {
        govHydro3List.add(m); }

    public void add(GovHydro4 m) {
        govHydro4List.add(m); }

    public void add(GovHydroDD m) {
        govHydroDDList.add(m); }

    public void add(GovHydroFrancis m) {
        govHydroFrancisList.add(m); }

    public void add(GovHydroIEEE0 m) {
        govHydroIEEE0List.add(m); }

    public void add(GovHydroIEEE2 m) {
        govHydroIEEE2List.add(m); }

    public void add(GovHydroPelton m) {
        govHydroPeltonList.add(m); }

    public void add(GovHydroPID m) {
        govHydroPIDList.add(m); }

    public void add(GovHydroPID2 m) {
        govHydroPID2List.add(m); }

    public void add(GovHydroR m) {
        govHydroRList.add(m); }

    public void add(GovHydroWEH m) {
        govHydroWEHList.add(m); }

    public void add(GovHydroWPID m) {
        govHydroWPIDList.add(m); }

    public void add(GovGAST m) {
        govGASTList.add(m); }

    public void add(GovGAST1 m) {
        govGAST1List.add(m); }

    public void add(GovGAST2 m) {
        govGAST2List.add(m); }

    public void add(GovGAST3 m) {
        govGAST3List.add(m); }

    public void add(GovGAST4 m) {
        govGAST4List.add(m); }

    public void add(GovGASTWD m) {
        govGASTWDList.add(m); }

    public void add(GovCT1 m) {
        govCT1List.add(m); }

    public void add(GovCT2 m) {
        govCT2List.add(m); }

    public void add(ExcIEEEDC1A m) {
        excIEEEDC1AList.add(m); }

    public void add(ExcIEEEDC2A m) {
        excIEEEDC2AList.add(m); }

    public void add(ExcIEEEDC3A m) {
        excIEEEDC3AList.add(m); }

    public void add(ExcIEEEDC4B m) {
        excIEEEDC4BList.add(m); }

    public void add(ExcIEEEAC1A m) {
        excIEEEAC1AList.add(m); }

    public void add(ExcIEEEAC2A m) {
        excIEEEAC2AList.add(m); }

    public void add(ExcIEEEAC3A m) {
        excIEEEAC3AList.add(m); }

    public void add(ExcIEEEAC4A m) {
        excIEEEAC4AList.add(m); }

    public void add(ExcIEEEAC5A m) {
        excIEEEAC5AList.add(m); }

    public void add(ExcIEEEAC6A m) {
        excIEEEAC6AList.add(m); }

    public void add(ExcIEEEAC7B m) {
        excIEEEAC7BList.add(m); }

    public void add(ExcIEEEAC8B m) {
        excIEEEAC8BList.add(m); }

    public void add(ExcIEEEST1A m) {
        excIEEEST1AList.add(m); }

    public void add(ExcIEEEST2A m) {
        excIEEEST2AList.add(m); }

    public void add(ExcIEEEST3A m) {
        excIEEEST3AList.add(m); }

    public void add(ExcIEEEST4B m) {
        excIEEEST4BList.add(m); }

    public void add(ExcIEEEST5B m) {
        excIEEEST5BList.add(m); }

    public void add(ExcIEEEST6B m) {
        excIEEEST6BList.add(m); }

    public void add(ExcIEEEST7B m) {
        excIEEEST7BList.add(m); }

    public void add(ExcAVR1 m) {
        excAVR1List.add(m); }

    public void add(ExcAVR2 m) {
        excAVR2List.add(m); }

    public void add(ExcAVR3 m) {
        excAVR3List.add(m); }

    public void add(ExcAVR4 m) {
        excAVR4List.add(m); }

    public void add(ExcAVR5 m) {
        excAVR5List.add(m); }

    public void add(ExcAVR7 m) {
        excAVR7List.add(m); }

    public void add(ExcBBC m) {
        excBBCList.add(m); }

    public void add(ExcCZ m) {
        excCZList.add(m); }

    public void add(ExcDC1A m) {
        excDC1AList.add(m); }

    public void add(ExcDC2A m) {
        excDC2AList.add(m); }

    public void add(ExcDC3A m) {
        excDC3AList.add(m); }

    public void add(ExcELIN1 m) {
        excELIN1List.add(m); }

    public void add(ExcELIN2 m) {
        excELIN2List.add(m); }

    public void add(ExcHU m) {
        excHUList.add(m); }

    public void add(ExcNI m) {
        excNIList.add(m); }

    public void add(ExcOEX3T m) {
        excOEX3TList.add(m); }

    public void add(ExcPIC m) {
        excPICList.add(m); }

    public void add(ExcREXS m) {
        excREXSList.add(m); }

    public void add(ExcRQB m) {
        excRQBList.add(m); }

    public void add(ExcSCRX m) {
        excSCRXList.add(m); }

    public void add(ExcSEXS m) {
        excSEXSList.add(m); }

    public void add(ExcSK m) {
        excSKList.add(m); }

    public void add(ExcST1A m) {
        excST1AList.add(m); }

    public void add(ExcST2A m) {
        excST2AList.add(m); }

    public void add(ExcST3 m) {
        excST3List.add(m); }

    public void add(ExcST4B m) {
        excST4BList.add(m); }

    public void add(ExcST6B m) {
        excST6BList.add(m); }

    public void add(ExcST7B m) {
        excST7BList.add(m); }

    public void add(ExcSYMPTR m) {
        excSYMPTRList.add(m); }

    public void add(ExcAC1A m) {
        excAC1AList.add(m); }

    public void add(ExcAC2A m) {
        excAC2AList.add(m); }

    public void add(ExcAC3A m) {
        excAC3AList.add(m); }

    public void add(ExcAC4A m) {
        excAC4AList.add(m); }

    public void add(ExcAC5A m) {
        excAC5AList.add(m); }

    public void add(ExcAC6A m) {
        excAC6AList.add(m); }

    public void add(ExcAC8B m) {
        excAC8BList.add(m); }

    public void add(PssSB4 m) {
        pssSB4List.add(m); }

    public void add(PssIEEE1A m) {
        pssIEEE1AList.add(m); }

    public void add(PssIEEE2B m) {
        pssIEEE2BList.add(m); }

    public void add(PssIEEE3B m) {
        pssIEEE3BList.add(m); }

    public void add(PssIEEE4B m) {
        pssIEEE4BList.add(m); }

    public void add(Pss1 m) {
        pss1List.add(m); }

    public void add(Pss1A m) {
        pss1AList.add(m); }

    public void add(Pss2B m) {
        pss2BList.add(m); }

    public void add(Pss2ST m) {
        pss2STList.add(m); }

    public void add(Pss5 m) {
        pss5List.add(m); }

    public void add(PssPTIST1 m) {
        pssPTIST1List.add(m); }

    public void add(PssPTIST3 m) {
        pssPTIST3List.add(m); }

    public void add(PssELIN2 m) {
        pssELIN2List.add(m); }

    public void add(PssSH m) {
        pssSHList.add(m); }

    public void add(PssWECC m) {
        pssWECCList.add(m); }

    public void add(PssRQB m) {
        pssRQBList.add(m); }

    public void add(SynchronousMachineSimplified m) {
        syncSimplifiedList.add(m); }

    public void add(SynchronousMachineDetailed m) {
        syncDetailedList.add(m); }

    public void add(SynchronousMachineEquivalentCircuit m) {
        syncEquivCircuitList.add(m); }

    public void add(SynchronousMachineTimeConstantReactance m) {
        syncTimeConstReactanceList.add(m); }

//    public void add(SynchronousMachineUserDefined m) {
//        syncUserDefinedList.add(m); }

    public void add(AsynchronousMachineTimeConstantReactance m) {
        asyncTimeConstReactanceList.add(m); }

    public void add(AsynchronousMachineEquivalentCircuit m) {
        asyncEquivCircuitList.add(m); }

//    public void add(AsynchronousMachineUserDefined m) {
//        asyncUserDefinedList.add(m); }

    public void add(WindGenTurbineType1aIEC m) {
        windType1aList.add(m); }

    public void add(WindGenTurbineType1bIEC m) {
        windType1bList.add(m); }

    public void add(WindGenTurbineType2IEC m) {
        windType2List.add(m); }

    public void add(WindGenTurbineType3aIEC m) {
        windType3aList.add(m); }

    public void add(WindGenTurbineType3bIEC m) {
        windType3bList.add(m); }

    public void add(WindGenTurbineType4aIEC m) {
        windType4aList.add(m); }

    public void add(WindGenTurbineType4bIEC m) {
        windType4bList.add(m); }

    public void add(WindPlantIEC m) {
        windPlantList.add(m); }

    public void add(WindMechIEC m) {
        windMechList.add(m); }

    public void add(WindAeroConstIEC m) {
        windAeroConstList.add(m); }

    public void add(WindAeroLinearIEC m) {
        windAeroLinearList.add(m); }

    public void add(WindContPitchAngleIEC m) {
        windContPitchList.add(m); }

    public void add(WindContPType3IEC m) {
        windContPType3List.add(m); }

    public void add(WindContPType4aIEC m) {
        windContPType4aList.add(m); }

    public void add(WindContPType4bIEC m) {
        windContPType4bList.add(m); }

    public void add(WindContQIEC m) {
        windContQList.add(m); }

    public void add(WindContRotorRIEC m) {
        windContRotorRList.add(m); }

    public void add(WindContCurrLimIEC m) {
        windCurrLimList.add(m); }

    public void add(WindPitchContEmulIEC m) {
        windPitchEmulList.add(m); }

    public void add(WindPlantFreqPcontrolIEC m) {
        windPlantFreqList.add(m); }

    public void add(WindPlantReactiveControlIEC m) {
        windPlantReactList.add(m); }

    public void add(WindProtectionIEC m) {
        windProtectionList.add(m); }

    public void add(LoadStatic m) {
        loadStaticList.add(m); }

    public void add(LoadComposite m) {
        loadCompositeList.add(m); }

    public void add(LoadAggregate m) {
        loadAggregateList.add(m); }

    public void add(LoadMotor m) {
        loadMotorList.add(m); }

    public void add(LoadGenericNonLinear m) {
        loadGenericNLList.add(m); }

    public void add(MechLoad1 m) {
        mechLoad1List.add(m); }

    public void add(CsConverterDynamics m) {
        csConverterList.add(m); }

    public void add(VsConverterDynamics m) {
        vsConverterList.add(m); }

    public void add(DiscExcContIEEEDEC1A m) {
        discExcDEC1AList.add(m); }

    public void add(DiscExcContIEEEDEC2A m) {
        discExcDEC2AList.add(m); }

    public void add(DiscExcContIEEEDEC3A m) {
        discExcDEC3AList.add(m); }

    public void add(OverexcLimIEEE m) {
        oelIEEEList.add(m); }

    public void add(OverexcLimX m) {
        oelXList.add(m); }

    public void add(UnderexcLimIEEE1 m) {
        uelIEEE1List.add(m); }

    public void add(UnderexcLimIEEE2 m) {
        uelIEEE2List.add(m); }

    public void add(UnderexcLimX1 m) {
        uelX1List.add(m); }

    public void add(UnderexcLimX2 m) {
        uelX2List.add(m); }

    public void add(VoltageAdjusterIEEE m) {
        voltageAdjList.add(m); }

    public void add(VoltageCompensatorIEEE m) {
        voltageCompList.add(m); }

    public void add(VCompIEEEType1 m) {
        vCompIEEEType1List.add(m); }

//    public void add(UserDefinedModel m) {
//        userDefinedList.add(m); }
}

