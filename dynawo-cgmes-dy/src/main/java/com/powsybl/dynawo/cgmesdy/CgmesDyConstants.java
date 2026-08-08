/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy;

/**
 * Constants for CGMES DY (Dynamics) profile parsing.
 * Defines CIM namespaces, RDF prefixes, and SPARQL query utilities
 * aligned with IEC 61970-302 (CIM for Dynamics).
 *
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public final class CgmesDyConstants {

    private CgmesDyConstants() {
        // Utility class
    }

    // -------------------------------------------------------------------------
    // CIM namespace URIs (CIM16 / CGMES 2.4 and CIM17 / CGMES 3.0)
    // -------------------------------------------------------------------------

    /** CIM16 namespace (CGMES 2.4.15 – most deployed version) */
    public static final String CIM16_NS = "http://iec.ch/TC57/2013/CIM-schema-cim16#";

    /** CIM17 namespace (CGMES 3.0 – IEC 61970-600 series) */
    public static final String CIM17_NS = "http://iec.ch/TC57/CIM100#";

    /** RDF namespace */
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /** RDFS namespace */
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    /** MD (Model Description) namespace */
    public static final String MD_NS = "http://iec.ch/TC57/61970-552/ModelDescription/1#";

    // -------------------------------------------------------------------------
    // SPARQL prefix declarations
    // -------------------------------------------------------------------------

    public static final String SPARQL_PREFIXES_CIM16 =
            "PREFIX cim: <" + CIM16_NS + ">\n" +
            "PREFIX rdf: <" + RDF_NS + ">\n" +
            "PREFIX rdfs: <" + RDFS_NS + ">\n" +
            "PREFIX md:   <" + MD_NS + ">\n";

    public static final String SPARQL_PREFIXES_CIM17 =
            "PREFIX cim: <" + CIM17_NS + ">\n" +
            "PREFIX rdf: <" + RDF_NS + ">\n" +
            "PREFIX rdfs: <" + RDFS_NS + ">\n" +
            "PREFIX md:   <" + MD_NS + ">\n";

    // -------------------------------------------------------------------------
    // PropertyBag field names shared across model categories
    // -------------------------------------------------------------------------

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SYNC_MACHINE_ID = "synchronousMachineId";
    public static final String FIELD_ASYNC_MACHINE_ID = "asynchronousMachineId";
    public static final String FIELD_EXCITER_ID = "excitationSystemId";
    public static final String FIELD_POWER_PLANT_ID = "powerPlantId";

    // -------------------------------------------------------------------------
    // CIM class names for dynamic model types (used in SPARQL rdf:type filters)
    // -------------------------------------------------------------------------

    // --- Governors ---
    public static final String CLASS_GOV_STEAM0 = "GovSteam0";
    public static final String CLASS_GOV_STEAM1 = "GovSteam1";
    public static final String CLASS_GOV_STEAM2 = "GovSteam2";
    public static final String CLASS_GOV_STEAM_CC = "GovSteamCC";
    public static final String CLASS_GOV_STEAM_EU = "GovSteamEU";
    public static final String CLASS_GOV_STEAM_FV2 = "GovSteamFV2";
    public static final String CLASS_GOV_STEAM_FV3 = "GovSteamFV3";
    public static final String CLASS_GOV_STEAM_FV4 = "GovSteamFV4";
    public static final String CLASS_GOV_STEAM_IEEE1 = "GovSteamIEEE1";
    public static final String CLASS_GOV_STEAM_SGO = "GovSteamSGO";
    public static final String CLASS_GOV_HYDRO1 = "GovHydro1";
    public static final String CLASS_GOV_HYDRO2 = "GovHydro2";
    public static final String CLASS_GOV_HYDRO3 = "GovHydro3";
    public static final String CLASS_GOV_HYDRO4 = "GovHydro4";
    public static final String CLASS_GOV_HYDRO_DD = "GovHydroDD";
    public static final String CLASS_GOV_HYDRO_FRANCIS = "GovHydroFrancis";
    public static final String CLASS_GOV_HYDRO_IEEE0 = "GovHydroIEEE0";
    public static final String CLASS_GOV_HYDRO_IEEE2 = "GovHydroIEEE2";
    public static final String CLASS_GOV_HYDRO_PELTON = "GovHydroPelton";
    public static final String CLASS_GOV_HYDRO_PID = "GovHydroPID";
    public static final String CLASS_GOV_HYDRO_PID2 = "GovHydroPID2";
    public static final String CLASS_GOV_HYDRO_R = "GovHydroR";
    public static final String CLASS_GOV_HYDRO_WEH = "GovHydroWEH";
    public static final String CLASS_GOV_HYDRO_WPID = "GovHydroWPID";
    public static final String CLASS_GOV_GAST = "GovGAST";
    public static final String CLASS_GOV_GAST1 = "GovGAST1";
    public static final String CLASS_GOV_GAST2 = "GovGAST2";
    public static final String CLASS_GOV_GAST3 = "GovGAST3";
    public static final String CLASS_GOV_GAST4 = "GovGAST4";
    public static final String CLASS_GOV_GASTWD = "GovGASTWD";
    public static final String CLASS_GOV_CT1 = "GovCT1";
    public static final String CLASS_GOV_CT2 = "GovCT2";

    // --- Exciters (IEEE) ---
    public static final String CLASS_EXC_IEEE_DC1A = "ExcIEEEDC1A";
    public static final String CLASS_EXC_IEEE_DC2A = "ExcIEEEDC2A";
    public static final String CLASS_EXC_IEEE_DC3A = "ExcIEEEDC3A";
    public static final String CLASS_EXC_IEEE_DC4B = "ExcIEEEDC4B";
    public static final String CLASS_EXC_IEEE_AC1A = "ExcIEEEAC1A";
    public static final String CLASS_EXC_IEEE_AC2A = "ExcIEEEAC2A";
    public static final String CLASS_EXC_IEEE_AC3A = "ExcIEEEAC3A";
    public static final String CLASS_EXC_IEEE_AC4A = "ExcIEEEAC4A";
    public static final String CLASS_EXC_IEEE_AC5A = "ExcIEEEAC5A";
    public static final String CLASS_EXC_IEEE_AC6A = "ExcIEEEAC6A";
    public static final String CLASS_EXC_IEEE_AC7B = "ExcIEEEAC7B";
    public static final String CLASS_EXC_IEEE_AC8B = "ExcIEEEAC8B";
    public static final String CLASS_EXC_IEEE_ST1A = "ExcIEEEST1A";
    public static final String CLASS_EXC_IEEE_ST2A = "ExcIEEEST2A";
    public static final String CLASS_EXC_IEEE_ST3A = "ExcIEEEST3A";
    public static final String CLASS_EXC_IEEE_ST4B = "ExcIEEEST4B";
    public static final String CLASS_EXC_IEEE_ST5B = "ExcIEEEST5B";
    public static final String CLASS_EXC_IEEE_ST6B = "ExcIEEEST6B";
    public static final String CLASS_EXC_IEEE_ST7B = "ExcIEEEST7B";

    // --- Exciters (vendor/other) ---
    public static final String CLASS_EXC_AVR1 = "ExcAVR1";
    public static final String CLASS_EXC_AVR2 = "ExcAVR2";
    public static final String CLASS_EXC_AVR3 = "ExcAVR3";
    public static final String CLASS_EXC_AVR4 = "ExcAVR4";
    public static final String CLASS_EXC_AVR5 = "ExcAVR5";
    public static final String CLASS_EXC_AVR7 = "ExcAVR7";
    public static final String CLASS_EXC_BBC = "ExcBBC";
    public static final String CLASS_EXC_CZ = "ExcCZ";
    public static final String CLASS_EXC_DC1A = "ExcDC1A";
    public static final String CLASS_EXC_DC2A = "ExcDC2A";
    public static final String CLASS_EXC_DC3A = "ExcDC3A";
    public static final String CLASS_EXC_ELIN1 = "ExcELIN1";
    public static final String CLASS_EXC_ELIN2 = "ExcELIN2";
    public static final String CLASS_EXC_HU = "ExcHU";
    public static final String CLASS_EXC_NI = "ExcNI";
    public static final String CLASS_EXC_OEX3T = "ExcOEX3T";
    public static final String CLASS_EXC_PIC = "ExcPIC";
    public static final String CLASS_EXC_REXS = "ExcREXS";
    public static final String CLASS_EXC_RQB = "ExcRQB";
    public static final String CLASS_EXC_SCRX = "ExcSCRX";
    public static final String CLASS_EXC_SEXS = "ExcSEXS";
    public static final String CLASS_EXC_SK = "ExcSK";
    public static final String CLASS_EXC_ST1A = "ExcST1A";
    public static final String CLASS_EXC_ST2A = "ExcST2A";
    public static final String CLASS_EXC_ST3 = "ExcST3";
    public static final String CLASS_EXC_ST4B = "ExcST4B";
    public static final String CLASS_EXC_ST6B = "ExcST6B";
    public static final String CLASS_EXC_ST7B = "ExcST7B";
    public static final String CLASS_EXC_SYMPTR = "ExcSYMPTR";
    public static final String CLASS_EXC_AC1A = "ExcAC1A";
    public static final String CLASS_EXC_AC2A = "ExcAC2A";
    public static final String CLASS_EXC_AC3A = "ExcAC3A";
    public static final String CLASS_EXC_AC4A = "ExcAC4A";
    public static final String CLASS_EXC_AC5A = "ExcAC5A";
    public static final String CLASS_EXC_AC6A = "ExcAC6A";
    public static final String CLASS_EXC_AC8B = "ExcAC8B";

    // --- PSS ---
    public static final String CLASS_PSS_SB4 = "PssSB4";
    public static final String CLASS_PSS_IEEE1A = "PssIEEE1A";
    public static final String CLASS_PSS_IEEE2B = "PssIEEE2B";
    public static final String CLASS_PSS_IEEE3B = "PssIEEE3B";
    public static final String CLASS_PSS_IEEE4B = "PssIEEE4B";
    public static final String CLASS_PSS_1 = "Pss1";
    public static final String CLASS_PSS_1A = "Pss1A";
    public static final String CLASS_PSS_2B = "Pss2B";
    public static final String CLASS_PSS_2ST = "Pss2ST";
    public static final String CLASS_PSS_5 = "Pss5";
    public static final String CLASS_PSS_PTIST1 = "PssPTIST1";
    public static final String CLASS_PSS_PTIST3 = "PssPTIST3";
    public static final String CLASS_PSS_ELIN2 = "PssELIN2";
    public static final String CLASS_PSS_SH = "PssSH";
    public static final String CLASS_PSS_WECC = "PssWECC";
    public static final String CLASS_PSS_RQB = "PssRQB";

    // --- Synchronous machine ---
    public static final String CLASS_SYNC_SIMPLIFIED = "SynchronousMachineSimplified";
    public static final String CLASS_SYNC_DETAILED = "SynchronousMachineDetailed";
    public static final String CLASS_SYNC_EQUIV_CIRCUIT = "SynchronousMachineEquivalentCircuit";
    public static final String CLASS_SYNC_TIME_CONST_REACTANCE = "SynchronousMachineTimeConstantReactance";
//    public static final String CLASS_SYNC_USER_DEFINED = "SynchronousMachineUserDefined";

    // --- Asynchronous machine ---
    public static final String CLASS_ASYNC_TIME_CONST_REACTANCE = "AsynchronousMachineTimeConstantReactance";
    public static final String CLASS_ASYNC_EQUIV_CIRCUIT = "AsynchronousMachineEquivalentCircuit";
//    public static final String CLASS_ASYNC_USER_DEFINED = "AsynchronousMachineUserDefined";

    // --- Wind ---
    public static final String CLASS_WIND_TYPE1A_IEC = "WindGenTurbineType1aIEC";
    public static final String CLASS_WIND_TYPE1B_IEC = "WindGenTurbineType1bIEC";
    public static final String CLASS_WIND_TYPE2_IEC = "WindGenTurbineType2IEC";
    public static final String CLASS_WIND_TYPE3A_IEC = "WindGenTurbineType3aIEC";
    public static final String CLASS_WIND_TYPE3B_IEC = "WindGenTurbineType3bIEC";
    public static final String CLASS_WIND_TYPE4A_IEC = "WindGenTurbineType4aIEC";
    public static final String CLASS_WIND_TYPE4B_IEC = "WindGenTurbineType4bIEC";
    public static final String CLASS_WIND_PLANT_IEC = "WindPlantIEC";
    public static final String CLASS_WIND_MECH_IEC = "WindMechIEC";
    public static final String CLASS_WIND_AERO_CONST = "WindAeroConstIEC";
    public static final String CLASS_WIND_AERO_LINEAR = "WindAeroLinearIEC";
    public static final String CLASS_WIND_CONT_PITCH = "WindContPitchAngleIEC";
    public static final String CLASS_WIND_CONT_PTYPE3 = "WindContPType3IEC";
    public static final String CLASS_WIND_CONT_PTYPE4A = "WindContPType4aIEC";
    public static final String CLASS_WIND_CONT_PTYPE4B = "WindContPType4bIEC";
    public static final String CLASS_WIND_CONT_Q = "WindContQIEC";
    public static final String CLASS_WIND_CONT_ROTOR_R = "WindContRotorRIEC";
    public static final String CLASS_WIND_CURR_LIM = "WindContCurrLimIEC";
    public static final String CLASS_WIND_PITCH_EMUL = "WindPitchContEmulIEC";
    public static final String CLASS_WIND_PLANT_FREQ = "WindPlantFreqPcontrolIEC";
    public static final String CLASS_WIND_PLANT_REACT = "WindPlantReactiveControlIEC";
    public static final String CLASS_WIND_PROTECTION = "WindProtectionIEC";

    // --- Load ---
    public static final String CLASS_LOAD_STATIC = "LoadStatic";
    public static final String CLASS_LOAD_COMPOSITE = "LoadComposite";
    public static final String CLASS_LOAD_AGGREGATE = "LoadAggregate";
    public static final String CLASS_LOAD_MOTOR = "LoadMotor";
    public static final String CLASS_LOAD_GENERIC_NL = "LoadGenericNonLinear";
    public static final String CLASS_LOAD_USER_DEFINED = "LoadUserDefined";
    public static final String CLASS_MECH_LOAD1 = "MechLoad1";

    // --- HVDC ---
    public static final String CLASS_CS_CONVERTER = "CsConverter";
    public static final String CLASS_VS_CONVERTER = "VsConverter";

    // --- Protection / Limiters ---
    public static final String CLASS_DISC_EXC_DEC1A = "DiscExcContIEEEDEC1A";
    public static final String CLASS_DISC_EXC_DEC2A = "DiscExcContIEEEDEC2A";
    public static final String CLASS_DISC_EXC_DEC3A = "DiscExcContIEEEDEC3A";
    public static final String CLASS_OEL_IEEE = "OverexcLimIEEE";
    public static final String CLASS_OEL_X = "OverexcLimX";
    public static final String CLASS_UEL_IEEE1 = "UnderexcLimIEEE1";
    public static final String CLASS_UEL_IEEE2 = "UnderexcLimIEEE2";
    public static final String CLASS_UEL_X1 = "UnderexcLimX1";
    public static final String CLASS_UEL_X2 = "UnderexcLimX2";
    public static final String CLASS_VOLTAGE_ADJ = "VoltageAdjusterIEEE";
    public static final String CLASS_VOLTAGE_COMP = "VoltageCompensatorIEEE";

    // --- FACTS ---
//    public static final String CLASS_SVC_USER_DEFINED = "SVCUserDefined";
//
//    // --- User-defined ---
//    public static final String CLASS_EXCITER_USER_DEF = "ExcitationSystemUserDefined";
//    public static final String CLASS_GOV_USER_DEF = "TurbineGovernorUserDefined";
//    public static final String CLASS_PSS_USER_DEF = "PowerSystemStabilizerUserDefined";
//    public static final String CLASS_LOAD_USER_DEF = "LoadUserDefined";
//    public static final String CLASS_SYNC_USER_DEF = "SynchronousMachineUserDefined";
//    public static final String CLASS_ASYNC_USER_DEF = "AsynchronousMachineUserDefined";
//    public static final String CLASS_VSC_USER_DEF = "VSCUserDefined";
//    public static final String CLASS_CSC_USER_DEF = "CSCUserDefined";
//    public static final String CLASS_WIND_PLANT_USER_DEF = "WindPlantUserDefined";
//    public static final String CLASS_WIND_TYPE1OR2_USER_DEF = "WindType1or2UserDefined";
//    public static final String CLASS_WIND_TYPE3OR4_USER_DEF = "WindType3or4UserDefined";

    public static final String CLASS_VCOMP_IEEE_TYPE1 = "VCompIEEEType1";

    // -------------------------------------------------------------------------
    // SPARQL query resource paths
    // -------------------------------------------------------------------------
    public static final String QUERIES_PATH = "/com/powsybl/dynawo/cgmesdy/queries/";
}
