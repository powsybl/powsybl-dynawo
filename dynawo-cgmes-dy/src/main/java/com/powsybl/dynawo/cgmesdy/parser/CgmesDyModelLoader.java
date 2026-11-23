/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.parser;

import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.dynawo.cgmesdy.governors.steam.*;
import com.powsybl.dynawo.cgmesdy.governors.hydro.*;
import com.powsybl.dynawo.cgmesdy.governors.gas.*;
import com.powsybl.dynawo.cgmesdy.exciters.dc.*;
import com.powsybl.dynawo.cgmesdy.exciters.ac.*;
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
import com.powsybl.triplestore.api.PropertyBag;
import com.powsybl.triplestore.api.PropertyBags;
import com.powsybl.triplestore.api.TripleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.powsybl.dynawo.cgmesdy.CgmesDyConstants.*;
import static com.powsybl.dynawo.cgmesdy.parser.PropertyBagUtils.*;

/**
 * Populates a {@link CgmesDyModel} by executing SPARQL queries against a
 * powsybl {@link TripleStore} loaded from a CGMES DY profile file.
 *
 * <p>All SPARQL queries are loaded from classpath resources under
 * {@code QUERIES_PATH/<ClassName>.sparql}.  The placeholder {@code %%CIM_NS%%}
 * in each file is replaced at runtime with the chosen CIM namespace URI.
 *
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public final class CgmesDyModelLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CgmesDyModelLoader.class);

    private static final String SYNC_MACHINE_ID = "synchronousMachineId";
    private static final String ASYNC_MACHINE_ID = "asynchronousMachineId";
    private static final String EXCITER_ID = "excitationSystemId";
    private static final String POWER_PLANT_ID = "powerPlantId";

    private final TripleStore tripleStore;
    private final String cimNs;

    public CgmesDyModelLoader(TripleStore tripleStore, String cimNs) {
        this.tripleStore = tripleStore;
        this.cimNs = cimNs;
    }

    // =========================================================================
    // Entry point
    // =========================================================================

    public CgmesDyModel load() {
        CgmesDyModel model = new CgmesDyModel();
        loadGovSteam0(model);
        loadGovSteam1(model);
        loadGovSteam2(model);
        loadGovSteamCC(model);
        loadGovSteamEU(model);
        loadGovSteamFV2(model);
        loadGovSteamFV3(model);
        loadGovSteamFV4(model);
        loadGovSteamIEEE1(model);
        loadGovSteamSGO(model);
        loadGovHydro1(model);
        loadGovHydro2(model);
        loadGovHydro3(model);
        loadGovHydro4(model);
        loadGovHydroDD(model);
        loadGovHydroFrancis(model);
        loadGovHydroIEEE0(model);
        loadGovHydroIEEE2(model);
        loadGovHydroPelton(model);
        loadGovHydroPID(model);
        loadGovHydroPID2(model);
        loadGovHydroR(model);
        loadGovHydroWEH(model);
        loadGovHydroWPID(model);
        loadGovGAST(model);
        loadGovGAST1(model);
        loadGovGAST2(model);
        loadGovGAST3(model);
        loadGovGAST4(model);
        loadGovGASTWD(model);
        loadGovCT1(model);
        loadGovCT2(model);
        loadExcIEEEDC1A(model);
        loadExcIEEEDC2A(model);
        loadExcIEEEDC3A(model);
        loadExcIEEEDC4B(model);
        loadExcIEEEAC1A(model);
        loadExcIEEEAC2A(model);
        loadExcIEEEAC3A(model);
        loadExcIEEEAC4A(model);
        loadExcIEEEAC5A(model);
        loadExcIEEEAC6A(model);
        loadExcIEEEAC7B(model);
        loadExcIEEEAC8B(model);
        loadExcIEEEST1A(model);
        loadExcIEEEST2A(model);
        loadExcIEEEST3A(model);
        loadExcIEEEST4B(model);
        loadExcIEEEST5B(model);
        loadExcIEEEST6B(model);
        loadExcIEEEST7B(model);
        loadExcAVRs(model);
        loadExcBBC(model);
        loadExcCZ(model);
        loadExcDCVariants(model);
        loadExcELIN(model);
        loadExcHU(model);
        loadExcNI(model);
        loadExcOEX3T(model);
        loadExcPIC(model);
        loadExcREXS(model);
        loadExcRQB(model);
        loadExcSCRX(model);
        loadExcSEXS(model);
        loadExcSK(model);
        loadExcSTVariants(model);
        loadExcSYMPTR(model);
        loadExcACVariants(model);
        loadPssSB4(model);
        loadPssIEEE1A(model);
        loadPssIEEE2B(model);
        loadPssIEEE3B(model);
        loadPssIEEE4B(model);
        loadPss1(model);
        loadPss1A(model);
        loadPss2B(model);
        loadPss2ST(model);
        loadPss5(model);
        loadPssPTIST1(model);
        loadPssPTIST3(model);
        loadPssELIN2(model);
        loadPssSH(model);
        loadPssWECC(model);
        loadPssRQB(model);
        loadSyncMachines(model);
        loadAsyncMachines(model);
        loadWindTurbines(model);
        loadWindSubModels(model);
        loadLoadModels(model);
        loadHvdc(model);
        loadProtection(model);
        // loadUserDefined(model);
        LOG.info("CgmesDyModelLoader completed. Summary: {}", buildSummary(model));
        return model;
    }

    // =========================================================================
    // Classpath query loader
    // =========================================================================

    private String loadQuery(String cimClassName) {
        String resourcePath = QUERIES_PATH + cimClassName + ".sparql";
        try (InputStream is = CgmesDyModelLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new CgmesDyParseException("SPARQL resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8).replace("%%CIM_NS%%", cimNs);
        } catch (IOException e) {
            throw new CgmesDyParseException("Cannot read SPARQL resource: " + resourcePath, e);
        }
    }

    private PropertyBags query(String sparql) {
        try {
            return tripleStore.query(sparql);
        } catch (Exception e) {
            LOG.error("SPARQL query failed: {}", e.getMessage(), e);
            return new PropertyBags();
        }
    }

    // =========================================================================
    // Governors – Steam
    // =========================================================================

    private void loadGovSteam0(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM0)).forEach(b -> {
            try {
                m.add(new GovSteam0(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"),
                        asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "dt")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteam0 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteam1(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM1)).forEach(b -> {
            try {
                m.add(new GovSteam1(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "k"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"),
                        asDouble(b, "uo"), asDouble(b, "uc"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "t4"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "t5"), asDouble(b, "k3"), asDouble(b, "k4"),
                        asDouble(b, "t6"), asDouble(b, "k5"), asDouble(b, "k6"), asDouble(b, "t7"), asDouble(b, "k7"), asDouble(b, "k8"),
                        asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"),
                        asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                        asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"),
                        asDouble(b, "gv5"), asDouble(b, "pgv5"), asDouble(b, "gv6"), asDouble(b, "pgv6")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteam1 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteam2(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM2)).forEach(b -> {
            try {
                m.add(new GovSteam2(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "dbf"), asDouble(b, "k"), asDouble(b, "r"),
                        asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "uo"), asDouble(b, "uc")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteam2 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamCC(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_CC)).forEach(b -> {
            try {
                m.add(new GovSteamCC(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"),
                        asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "uc"), asDouble(b, "uo"),
                        asDouble(b, "dhp"), asDouble(b, "dlp"), asDouble(b, "fhp"), asDouble(b, "flp"),
                        asDouble(b, "fip"), asDouble(b, "tip"), asDouble(b, "tlp")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamCC {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamEU(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_EU)).forEach(b -> {
            try {
                m.add(new GovSteamEU(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "ke"), asDouble(b, "kfcor"), asDouble(b, "komegacor"),
                        asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"),
                        asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "te"), asDouble(b, "tfp"),
                        asDouble(b, "tvhp"), asDouble(b, "tvip"), asDouble(b, "chc"), asDouble(b, "cho"),
                        asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"), asDouble(b, "simX")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamEU {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamFV2(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_FV2)).forEach(b -> {
            try {
                m.add(new GovSteamFV2(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "vamax"), asDouble(b, "vamin"),
                        asDouble(b, "k"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "t4"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "t5"), asDouble(b, "k3"), asDouble(b, "k4"),
                        asInt(b, "iFlag")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamFV2 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamFV3(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_FV3)).forEach(b -> {
            try {
                m.add(new GovSteamFV3(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"),
                        asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "prmax"), asDouble(b, "k"), asDouble(b, "t5"), asDouble(b, "t6"),
                        asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "uc"), asDouble(b, "uo"),
                        asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                        asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"),
                        asDouble(b, "gv5"), asDouble(b, "pgv5"), asDouble(b, "gv6"), asDouble(b, "pgv6")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamFV3 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamFV4(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_FV4)).forEach(b -> {
            try {
                m.add(new GovSteamFV4(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"),
                        asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "k"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "k3"), asDouble(b, "k4"),
                        asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "t7"), asDouble(b, "ta"), asDouble(b, "tc"), asDouble(b, "ty"), asDouble(b, "tt"),
                        asDouble(b, "kf1"), asDouble(b, "kf2"), asDouble(b, "tf2"),
                        asDouble(b, "rsmimn"), asDouble(b, "rsmimx"), asDouble(b, "vvmax"), asDouble(b, "vvmin"),
                        asDouble(b, "cpsmn"), asDouble(b, "cpsmx"), asDouble(b, "dp"), asDouble(b, "lpsp"), asDouble(b, "ovex")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamFV4 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamIEEE1(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_IEEE1)).forEach(b -> {
            try {
                m.add(new GovSteamIEEE1(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "vmax"), asDouble(b, "vmin"),
                        asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "uo"), asDouble(b, "uc"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "t4"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "t5"), asDouble(b, "k3"), asDouble(b, "k4"),
                        asDouble(b, "t6"), asDouble(b, "k5"), asDouble(b, "k6"), asDouble(b, "t7"), asDouble(b, "k7"), asDouble(b, "k8"),
                        asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"),
                        asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                        asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"),
                        asDouble(b, "gv5"), asDouble(b, "pgv5"), asDouble(b, "gv6"), asDouble(b, "pgv6")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamIEEE1 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovSteamSGO(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_STEAM_SGO)).forEach(b -> {
            try {
                m.add(new GovSteamSGO(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "vmax"), asDouble(b, "vmin"),
                        asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"),
                        asDouble(b, "pmax"), asDouble(b, "pmin")));
            } catch (Exception e) {
                LOG.warn("Skipping GovSteamSGO {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    // =========================================================================
    // Governors – Hydro
    // =========================================================================

    private void loadGovHydro1(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_HYDRO1)).forEach(b -> {
            try {
                m.add(new GovHydro1(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "tr"), asDouble(b, "tf"), asDouble(b, "tg"),
                        asDouble(b, "velm"), asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "tw"),
                        asDouble(b, "at"), asDouble(b, "dturb"), asDouble(b, "qnl"),
                        asDouble(b, "rperm"), asDouble(b, "rtemp"), asDouble(b, "tp"), asDouble(b, "hdam")));
            } catch (Exception e) {
                LOG.warn("Skipping GovHydro1 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovHydro2(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_HYDRO2)).forEach(b -> {
            try {
                m.add(new GovHydro2(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "tr"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "ki"), asDouble(b, "kg"), asDouble(b, "tp"), asDouble(b, "td"),
                        asDouble(b, "aturb"), asDouble(b, "bturb"), asDouble(b, "tturb"), asDouble(b, "velm"),
                        asDouble(b, "tw"), asDouble(b, "db1"), asDouble(b, "eps")));
            } catch (Exception e) {
                LOG.warn("Skipping GovHydro2 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovHydro3(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_HYDRO3)).forEach(b -> {
            try {
                m.add(new GovHydro3(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "tr"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "ki"), asDouble(b, "kg"), asDouble(b, "tp"), asDouble(b, "td"),
                        asDouble(b, "aturb"), asDouble(b, "bturb"), asDouble(b, "tturb"), asDouble(b, "velm"),
                        asDouble(b, "tw"), asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"),
                        asDouble(b, "tt"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"),
                        asDouble(b, "qnl"), asDouble(b, "rperm"), asDouble(b, "rtemp"), asDouble(b, "hdam"), asInt(b, "govtype")));
            } catch (Exception e) {
                LOG.warn("Skipping GovHydro3 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovHydro4(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_HYDRO4)).forEach(b -> {
            try {
                m.add(new GovHydro4(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "tr"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                        asDouble(b, "rclose"), asDouble(b, "ropen"),
                        asDouble(b, "ta"), asDouble(b, "tc"), asDouble(b, "td"), asDouble(b, "ts"), asDouble(b, "tp"),
                        asDouble(b, "tw"), asDouble(b, "dturb"), asDouble(b, "qnl"),
                        asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                        asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"),
                        asDouble(b, "gv5"), asDouble(b, "pgv5"), asDouble(b, "gv6"), asDouble(b, "pgv6"),
                        asDouble(b, "atw"), asDouble(b, "velm"), asDouble(b, "hdam")));
            } catch (Exception e) {
                LOG.warn("Skipping GovHydro4 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovHydroDD(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_DD, m); }

    private void loadGovHydroFrancis(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_FRANCIS, m); }

    private void loadGovHydroIEEE0(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_IEEE0, m); }

    private void loadGovHydroIEEE2(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_IEEE2, m); }

    private void loadGovHydroPelton(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_PELTON, m); }

    private void loadGovHydroPID(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_PID, m); }

    private void loadGovHydroPID2(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_PID2, m); }

    private void loadGovHydroR(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_R, m); }

    private void loadGovHydroWEH(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_WEH, m); }

    private void loadGovHydroWPID(CgmesDyModel m) {
        loadHydroGeneric(CLASS_GOV_HYDRO_WPID, m); }

    private void loadHydroGeneric(String cls, CgmesDyModel m) {
        query(loadQuery(cls)).forEach(b -> {
            try {
                dispatchHydro(cls, b, m);
            } catch (Exception e) {
                LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
            }
        });
    }

    private void dispatchHydro(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String sm = asAssocId(b, SYNC_MACHINE_ID);
        switch (cls) {
            case CLASS_GOV_HYDRO_DD -> m.add(new GovHydroDD(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "td"), asDouble(b, "tf"), asDouble(b, "tg"), asDouble(b, "tp"), asDouble(b, "tt"), asDouble(b, "tr"),
                asDouble(b, "velm"), asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "aturb"), asDouble(b, "bturb"), asDouble(b, "tturb"),
                asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"),
                asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"), asDouble(b, "gv5"), asDouble(b, "pgv5")));
            case CLASS_GOV_HYDRO_FRANCIS -> m.add(new GovHydroFrancis(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "rs"), asDouble(b, "tg"), asDouble(b, "tp"), asDouble(b, "bp"), asDouble(b, "td"),
                asDouble(b, "ta"), asDouble(b, "ts"), asDouble(b, "twnc"), asDouble(b, "twng"), asDouble(b, "qn"), asDouble(b, "h0"),
                asDouble(b, "am"), asDouble(b, "av0"), asDouble(b, "avsmnx"), asDouble(b, "avsmx"), asDouble(b, "hn"),
                asDouble(b, "kc"), asDouble(b, "kg"), asDouble(b, "ki"), asDouble(b, "knl"), asDouble(b, "qc0"),
                asDouble(b, "va"), asDouble(b, "valvmax"), asDouble(b, "valvmin"), asDouble(b, "vc"),
                asString(b, "waterTunnelSurgeChamberSimulation")));
            case CLASS_GOV_HYDRO_IEEE0 -> m.add(new GovHydroIEEE0(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "k"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "pmax"), asDouble(b, "pmin")));
            case CLASS_GOV_HYDRO_IEEE2 -> m.add(new GovHydroIEEE2(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "tr"), asDouble(b, "tf"), asDouble(b, "tg"),
                asDouble(b, "velm"), asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "tw"),
                asDouble(b, "at"), asDouble(b, "dturb"), asDouble(b, "qnl"),
                asDouble(b, "rperm"), asDouble(b, "rtemp"), asDouble(b, "tp"), asDouble(b, "hdam"), asDouble(b, "ki"),
                asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"),
                asDouble(b, "gv5"), asDouble(b, "pgv5"), asDouble(b, "gv6"), asDouble(b, "pgv6")));
            case CLASS_GOV_HYDRO_PELTON -> m.add(new GovHydroPelton(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "av0"), asDouble(b, "av1"), asDouble(b, "bp"), asDouble(b, "db1"),
                asDouble(b, "db2"), asDouble(b, "h1"), asDouble(b, "h2"), asDouble(b, "hn"), asDouble(b, "kc"), asDouble(b, "kg"),
                asDouble(b, "qc0"), asDouble(b, "qn"), asDouble(b, "simplifiedPelton"), asDouble(b, "staticCompensating"),
                asDouble(b, "ta"), asDouble(b, "td"), asDouble(b, "ts"), asDouble(b, "twnc"), asDouble(b, "twng"), asDouble(b, "tx"),
                asDouble(b, "va"), asDouble(b, "valvmax"), asDouble(b, "valvmin"), asDouble(b, "vav"), asDouble(b, "vc"), asDouble(b, "vcv"),
                asBoolean(b, "cfrac"), asBoolean(b, "sfrac")));
            case CLASS_GOV_HYDRO_PID -> m.add(new GovHydroPID(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "td"), asDouble(b, "tf"), asDouble(b, "tg"), asDouble(b, "tp"), asDouble(b, "tt"), asDouble(b, "tr"),
                asDouble(b, "velm"), asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "aturb"), asDouble(b, "bturb"), asDouble(b, "tturb"),
                asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"), asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kd"),
                asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"), asDouble(b, "gv5"), asDouble(b, "pgv5")));
            case CLASS_GOV_HYDRO_PID2 -> m.add(new GovHydroPID2(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "pmax"), asDouble(b, "pmin"),
                asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kd"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "treg"),
                asDouble(b, "tw"), asDouble(b, "velmax"), asDouble(b, "velmin"), asDouble(b, "gmax"), asDouble(b, "gmin"), asDouble(b, "g0")));
            case CLASS_GOV_HYDRO_R -> m.add(new GovHydroR(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "td"), asDouble(b, "tf"), asDouble(b, "tg"), asDouble(b, "tp"), asDouble(b, "tt"), asDouble(b, "tr"),
                asDouble(b, "velm"), asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "aturb"), asDouble(b, "bturb"), asDouble(b, "tturb"),
                asDouble(b, "db1"), asDouble(b, "eps"), asDouble(b, "db2"),
                asDouble(b, "gv1"), asDouble(b, "pgv1"), asDouble(b, "gv2"), asDouble(b, "pgv2"),
                asDouble(b, "gv3"), asDouble(b, "pgv3"), asDouble(b, "gv4"), asDouble(b, "pgv4"), asDouble(b, "gv5"), asDouble(b, "pgv5")));
            case CLASS_GOV_HYDRO_WEH -> m.add(new GovHydroWEH(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "rpg"), asDouble(b, "rpp"), asDouble(b, "reg"), asDouble(b, "tg"),
                asDouble(b, "tp"), asDouble(b, "td"), asDouble(b, "tf"), asDouble(b, "td2"), asDouble(b, "tw"),
                asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "gtmxop"), asDouble(b, "gtmxcl"),
                asDouble(b, "pmss1"), asDouble(b, "pmss2"), asDouble(b, "pmss3"), asDouble(b, "pmss4"), asDouble(b, "pmss5"),
                asDouble(b, "pmss6"), asDouble(b, "pmss7"), asDouble(b, "pmss8"), asDouble(b, "pmss9"), asDouble(b, "pmss10"),
                asDouble(b, "gpmax"), asDouble(b, "gmax"), asDouble(b, "gmin"),
                asDouble(b, "dpv"), asDouble(b, "dicn"), asDouble(b, "dspv"), asDouble(b, "feedbackSignal")));
            case CLASS_GOV_HYDRO_WPID -> m.add(new GovHydroWPID(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "treg"), asDouble(b, "tw"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"),
                asDouble(b, "pmax"), asDouble(b, "pmin"), asDouble(b, "gmax"), asDouble(b, "gmin"), asDouble(b, "mastp"),
                asDouble(b, "d"), asDouble(b, "kd"), asDouble(b, "ki"), asDouble(b, "kp"), asDouble(b, "velmax"), asDouble(b, "velmin")));
            default -> LOG.warn("Unhandled hydro class: {}", cls);
        }
    }

    // =========================================================================
    // Governors – Gas
    // =========================================================================

    private void loadGovGAST(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_GAST)).forEach(b -> {
            try {
                m.add(new GovGAST(
                    resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                    asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"),
                    asDouble(b, "at"), asDouble(b, "kt"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "dturb")));
            } catch (Exception e) {
                LOG.warn("Skipping GovGAST {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovGAST1(CgmesDyModel m) {
        query(loadQuery(CLASS_GOV_GAST1)).forEach(b -> {
            try {
                m.add(new GovGAST1(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"),
                        asDouble(b, "at"), asDouble(b, "kt"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "dturb"),
                        asDouble(b, "fpv"), asDouble(b, "ka"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "tltr"),
                        asDouble(b, "tac"), asDouble(b, "tv"), asDouble(b, "b")));
            } catch (Exception e) {
                LOG.warn("Skipping GovGAST1 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    private void loadGovGAST2(CgmesDyModel m) {
        loadGasGeneric(CLASS_GOV_GAST2, m); }

    private void loadGovGAST3(CgmesDyModel m) {
        loadGasGeneric(CLASS_GOV_GAST3, m); }

    private void loadGovGAST4(CgmesDyModel m) {
        loadGasGeneric(CLASS_GOV_GAST4, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadGovGASTWD(CgmesDyModel m) {
        loadGasGeneric(CLASS_GOV_GASTWD, m); }

    private void loadGovCT1(CgmesDyModel m) {
        loadGasGeneric(CLASS_GOV_CT1, m); }

    private void loadGovCT2(CgmesDyModel m) {
        loadGasGeneric(CLASS_GOV_CT2, m); }

    private void loadGasGeneric(String cls, CgmesDyModel m) {
        query(loadQuery(cls)).forEach(b -> {
            try {
                dispatchGas(cls, b, m);
            } catch (Exception e) {
                LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
            }
        });
    }

    private void dispatchGas(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String sm = asAssocId(b, SYNC_MACHINE_ID);
        switch (cls) {
            case CLASS_GOV_GAST2 -> m.add(new GovGAST2(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"),
                asDouble(b, "at"), asDouble(b, "kt"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "dturb"),
                asDouble(b, "w"), asDouble(b, "x"), asDouble(b, "y"), asDouble(b, "z"), asDouble(b, "cd"),
                asDouble(b, "tf"), asDouble(b, "etd"), asDouble(b, "tcd"), asDouble(b, "trate")));
            case CLASS_GOV_GAST3 -> m.add(new GovGAST3(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "vmax"), asDouble(b, "vmin"),
                asDouble(b, "dturb"), asDouble(b, "bca"), asDouble(b, "kca"), asDouble(b, "tsi"), asDouble(b, "bp"), asDouble(b, "tsa"), asDouble(b, "tsb")));
            case CLASS_GOV_GAST4 -> m.add(new GovGAST4(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"),
                asDouble(b, "at"), asDouble(b, "kt"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "dturb"),
                asDouble(b, "bp"), asDouble(b, "tr"), asDouble(b, "rLimMax"), asDouble(b, "rLimMin")));
            case CLASS_GOV_GASTWD -> m.add(new GovGASTWD(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "rdown"), asDouble(b, "rup"),
                asDouble(b, "ta"), asDouble(b, "tact"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "tf"),
                asDouble(b, "kdroop"), asDouble(b, "etd"), asDouble(b, "tcd"), asDouble(b, "trate"), asDouble(b, "teng"),
                asDouble(b, "td"), asDouble(b, "tltr"), asDouble(b, "tsa"), asDouble(b, "tsb"),
                asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "dpv"), asDouble(b, "kpgov"), asDouble(b, "kigov")));
            case CLASS_GOV_CT1 -> m.add(new GovCT1(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "rdown"), asDouble(b, "rup"),
                asDouble(b, "ta"), asDouble(b, "tact"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "teng"), asDouble(b, "tf"),
                asDouble(b, "tsa"), asDouble(b, "tsb"), asDouble(b, "vmax"), asDouble(b, "vmin"),
                asDouble(b, "wfnl"), asBoolean(b, "wfspd"),
                asDouble(b, "kdgov"), asDouble(b, "kigov"), asDouble(b, "kpgov"), asDouble(b, "kpload"), asDouble(b, "kiload"),
                asDouble(b, "tdgov"), asDouble(b, "tno"), asDouble(b, "ldref"), asDouble(b, "dm"), asDouble(b, "db"),
                asDouble(b, "ropen"), asDouble(b, "rclose"), asDouble(b, "kimw"), asDouble(b, "pmwset"), asDouble(b, "aset"), asDouble(b, "ka")));
            case CLASS_GOV_CT2 -> m.add(new GovCT2(id, sm,
                asDouble(b, "mwbase"), asDouble(b, "r"), asDouble(b, "rdown"), asDouble(b, "rup"),
                asDouble(b, "ta"), asDouble(b, "tact"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "teng"), asDouble(b, "tf"),
                asDouble(b, "tsa"), asDouble(b, "tsb"), asDouble(b, "vmax"), asDouble(b, "vmin"),
                asDouble(b, "wfnl"), asBoolean(b, "wfspd"),
                asDouble(b, "kdgov"), asDouble(b, "kigov"), asDouble(b, "kpgov"), asDouble(b, "kpload"), asDouble(b, "kiload"),
                asDouble(b, "tdgov"), asDouble(b, "tno"), asDouble(b, "ldref"), asDouble(b, "dm"), asDouble(b, "db"),
                asDouble(b, "ropen"), asDouble(b, "rclose"), asDouble(b, "kimw"), asDouble(b, "pmwset"), asDouble(b, "aset"), asDouble(b, "ka"),
                asDouble(b, "flim1"), asDouble(b, "plim1"), asDouble(b, "flim2"), asDouble(b, "plim2"),
                asDouble(b, "flim3"), asDouble(b, "plim3"), asDouble(b, "flim4"), asDouble(b, "plim4"),
                asDouble(b, "flim5"), asDouble(b, "plim5"), asDouble(b, "flim6"), asDouble(b, "plim6"),
                asDouble(b, "flim7"), asDouble(b, "plim7"), asDouble(b, "flim8"), asDouble(b, "plim8"),
                asDouble(b, "flim9"), asDouble(b, "plim9"), asDouble(b, "flim10"), asDouble(b, "plim10"),
                asBoolean(b, "prate"), asDouble(b, "uc"), asDouble(b, "uo")));
            default -> LOG.warn("Unhandled gas class: {}", cls);
        }
    }

    // =========================================================================
    // Exciters – IEEE DC
    // =========================================================================

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEDC1A(CgmesDyModel m) {
        query(loadQuery(CLASS_EXC_IEEE_DC1A)).forEach(b -> {
            try {
                m.add(new ExcIEEEDC1A(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"),
                        asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"),
                        asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ki"),
                        asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"),
                        asBoolean(b, "uelin"), asBoolean(b, "exclim")));
            } catch (Exception e) {
                LOG.warn("Skipping ExcIEEEDC1A {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEDC2A(CgmesDyModel m) {
        query(loadQuery(CLASS_EXC_IEEE_DC2A)).forEach(b -> {
            try {
                m.add(new ExcIEEEDC2A(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"),
                        asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"),
                        asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ki"),
                        asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"),
                        asBoolean(b, "uelin"), asBoolean(b, "exclim")));
            } catch (Exception e) {
                LOG.warn("Skipping ExcIEEEDC2A {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEDC3A(CgmesDyModel m) {
        query(loadQuery(CLASS_EXC_IEEE_DC3A)).forEach(b -> {
            try {
                m.add(new ExcIEEEDC3A(
                    resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                    asDouble(b, "trh"), asDouble(b, "kv"), asDouble(b, "vmax"), asDouble(b, "vmin"),
                    asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"),
                    asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"),
                    asBoolean(b, "exclim")));
            } catch (Exception e) {
                LOG.warn("Skipping ExcIEEEDC3A {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEDC4B(CgmesDyModel m) {
        query(loadQuery(CLASS_EXC_IEEE_DC4B)).forEach(b -> {
            try {
                m.add(new ExcIEEEDC4B(
                        resourceId(b), asAssocId(b, SYNC_MACHINE_ID),
                        asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kd"), asDouble(b, "td"),
                        asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"),
                        asDouble(b, "kc"), asDouble(b, "ki2"), asDouble(b, "vfemax"), asDouble(b, "vemin"),
                        asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"),
                        asBoolean(b, "uelin"), asBoolean(b, "oelin")));
            } catch (Exception e) {
                LOG.warn("Skipping ExcIEEEDC4B {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    // =========================================================================
    // Exciters – IEEE AC / ST
    // =========================================================================

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC1A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC1A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC2A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC2A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC3A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC3A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC4A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC4A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC5A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC5A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC6A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC6A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC7B(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC7B, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEAC8B(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_AC8B, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST1A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST1A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST2A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST2A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST3A(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST3A, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST4B(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST4B, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST5B(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST5B, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST6B(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST6B, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcIEEEST7B(CgmesDyModel m) {
        loadExcIeeGeneric(CLASS_EXC_IEEE_ST7B, m); }

    private void loadExcIeeGeneric(String cls, CgmesDyModel m) {
        query(loadQuery(cls)).forEach(b -> {
            try {
                dispatchExcIeee(cls, b, m);
            } catch (Exception e) {
                LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
            }
        });
    }

    private void dispatchExcIeee(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String sm = asAssocId(b, SYNC_MACHINE_ID);
        switch (cls) {
            case CLASS_EXC_IEEE_AC1A -> m.add(new ExcIEEEAC1A(id, sm, asDouble(b, "tr"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ke"), asDouble(b, "vfemax"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_IEEE_AC2A -> m.add(new ExcIEEEAC2A(id, sm, asDouble(b, "tr"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "kb"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "te"), asDouble(b, "vfemax"), asDouble(b, "kh"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ke"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_IEEE_AC3A -> m.add(new ExcIEEEAC3A(id, sm, asDouble(b, "tr"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "te"), asDouble(b, "vemin"), asDouble(b, "kr"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ke"), asDouble(b, "kn"), asDouble(b, "vfemax"), asDouble(b, "efdn"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_IEEE_AC4A -> m.add(new ExcIEEEAC4A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kc")));
            case CLASS_EXC_IEEE_AC5A -> m.add(new ExcIEEEAC5A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf1"), asDouble(b, "tf2"), asDouble(b, "tf3"), asDouble(b, "efdn"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_IEEE_AC6A -> m.add(new ExcIEEEAC6A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "tk"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "te"), asDouble(b, "ke"), asDouble(b, "vhmax"), asDouble(b, "kh"), asDouble(b, "tj"), asDouble(b, "th"), asDouble(b, "td"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "vfelim"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_IEEE_AC7B -> m.add(new ExcIEEEAC7B(id, sm, asDouble(b, "tr"), asDouble(b, "kpr"), asDouble(b, "kir"), asDouble(b, "kdr"), asDouble(b, "tdr"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kpa"), asDouble(b, "kia"), asDouble(b, "kda"), asDouble(b, "tda"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "kp"), asDouble(b, "kl"), asDouble(b, "te"), asDouble(b, "ke"), asDouble(b, "vfemax"), asDouble(b, "vemin"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "kf1"), asDouble(b, "kf2"), asDouble(b, "kf3"), asDouble(b, "tf"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asBoolean(b, "uelin"), asBoolean(b, "oelin")));
            case CLASS_EXC_IEEE_AC8B -> m.add(new ExcIEEEAC8B(id, sm, asDouble(b, "tr"), asDouble(b, "kpr"), asDouble(b, "kir"), asDouble(b, "kdr"), asDouble(b, "tdr"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "te"), asDouble(b, "ke"), asDouble(b, "vfemax"), asDouble(b, "vemin"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asBoolean(b, "uelin")));
            case CLASS_EXC_IEEE_ST1A -> m.add(new ExcIEEEST1A(id, sm, asDouble(b, "tr"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "tb1"), asDouble(b, "tc1"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kc"), asDouble(b, "kf"), asDouble(b, "tf"), asBoolean(b, "uelin"), asBoolean(b, "pssin"), asBoolean(b, "ilr"), asDouble(b, "klr")));
            case CLASS_EXC_IEEE_ST2A -> m.add(new ExcIEEEST2A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kc"), asDouble(b, "efdmax"), asBoolean(b, "uelin")));
            case CLASS_EXC_IEEE_ST3A -> m.add(new ExcIEEEST3A(id, sm, asDouble(b, "tr"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "km"), asDouble(b, "tm"), asDouble(b, "vmmax"), asDouble(b, "vmmin"), asDouble(b, "kg"), asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kc"), asDouble(b, "xl"), asDouble(b, "thetap"), asDouble(b, "vbmax"), asDouble(b, "vgmax"), asBoolean(b, "uelin")));
            case CLASS_EXC_IEEE_ST4B -> m.add(new ExcIEEEST4B(id, sm, asDouble(b, "tr"), asDouble(b, "kpr"), asDouble(b, "kir"), asDouble(b, "ta"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kpm"), asDouble(b, "kim"), asDouble(b, "vmmax"), asDouble(b, "vmmin"), asDouble(b, "kg"), asDouble(b, "kp"), asDouble(b, "xl"), asDouble(b, "thetap"), asDouble(b, "vbmax"), asDouble(b, "kc"), asDouble(b, "xl2"), asBoolean(b, "uelin"), asBoolean(b, "oelin")));
            case CLASS_EXC_IEEE_ST5B -> m.add(new ExcIEEEST5B(id, sm, asDouble(b, "tr"), asDouble(b, "kc"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "t1"), asDouble(b, "tb1"), asDouble(b, "tc1"), asDouble(b, "tb2"), asDouble(b, "tc2"), asDouble(b, "tob1"), asDouble(b, "toc1"), asDouble(b, "tob2"), asDouble(b, "toc2"), asDouble(b, "tub1"), asDouble(b, "tuc1"), asDouble(b, "tub2"), asDouble(b, "tuc2")));
            case CLASS_EXC_IEEE_ST6B -> m.add(new ExcIEEEST6B(id, sm, asDouble(b, "tr"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "kci"), asDouble(b, "kff"), asDouble(b, "kg"), asDouble(b, "kia"), asDouble(b, "klr"), asDouble(b, "km"), asDouble(b, "kpa"), asDouble(b, "kvd"), asDouble(b, "ilr"), asDouble(b, "tg"), asDouble(b, "ts"), asDouble(b, "tvd"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asString(b, "oelin")));
            case CLASS_EXC_IEEE_ST7B -> m.add(new ExcIEEEST7B(id, sm, asDouble(b, "tr"), asDouble(b, "kh"), asDouble(b, "kia"), asDouble(b, "tia"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "tf"), asDouble(b, "kl"), asDouble(b, "kpa"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "tg"), asString(b, "uelin"), asString(b, "oelin")));
            default -> LOG.warn("Unhandled IEEE exciter class: {}", cls);
        }
    }

    // =========================================================================
    // Exciters – Vendor
    // =========================================================================

    private void loadExcAVRs(CgmesDyModel m) {
        for (String c : new String[]{CLASS_EXC_AVR1, CLASS_EXC_AVR2, CLASS_EXC_AVR3, CLASS_EXC_AVR4, CLASS_EXC_AVR5, CLASS_EXC_AVR7}) {
            loadExcVendorGeneric(c, m);
        }
    }

    private void loadExcBBC(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_BBC, m); }

    private void loadExcCZ(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_CZ, m); }

    private void loadExcDCVariants(CgmesDyModel m) {
        for (String c : new String[]{CLASS_EXC_DC1A, CLASS_EXC_DC2A, CLASS_EXC_DC3A}) {
            loadExcVendorGeneric(c, m);
        }
    }

    private void loadExcELIN(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_ELIN1, m); loadExcVendorGeneric(CLASS_EXC_ELIN2, m); }

    private void loadExcHU(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_HU, m); }

    private void loadExcNI(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_NI, m); }

    private void loadExcOEX3T(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_OEX3T, m); }

    private void loadExcPIC(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_PIC, m); }

    private void loadExcREXS(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_REXS, m); }

    private void loadExcRQB(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_RQB, m); }

    private void loadExcSCRX(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_SCRX, m); }

    private void loadExcSEXS(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_SEXS, m); }

    private void loadExcSK(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_SK, m); }

    private void loadExcSTVariants(CgmesDyModel m) {
        for (String c : new String[]{CLASS_EXC_ST1A, CLASS_EXC_ST2A, CLASS_EXC_ST3, CLASS_EXC_ST4B, CLASS_EXC_ST6B, CLASS_EXC_ST7B}) {
            loadExcVendorGeneric(c, m);
        }
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadExcSYMPTR(CgmesDyModel m) {
        loadExcVendorGeneric(CLASS_EXC_SYMPTR, m); }

    private void loadExcACVariants(CgmesDyModel m) {
        for (String c : new String[]{CLASS_EXC_AC1A, CLASS_EXC_AC2A, CLASS_EXC_AC3A, CLASS_EXC_AC4A, CLASS_EXC_AC5A, CLASS_EXC_AC6A, CLASS_EXC_AC8B}) {
            loadExcVendorGeneric(c, m);
        }
    }

    private void loadExcVendorGeneric(String cls, CgmesDyModel m) {
        query(loadQuery(cls)).forEach(b -> {
            try {
                dispatchExcVendor(cls, b, m);
            } catch (Exception e) {
                LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
            }
        });
    }

    private void dispatchExcVendor(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String sm = asAssocId(b, SYNC_MACHINE_ID);
        switch (cls) {
            case CLASS_EXC_AVR1 -> m.add(new ExcAVR1(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "te"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asDouble(b, "tr"), asDouble(b, "vrmn"), asDouble(b, "vrmx")));
            case CLASS_EXC_AVR2 -> m.add(new ExcAVR2(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "te"), asDouble(b, "vrmn"), asDouble(b, "vrmx"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asDouble(b, "tr")));
            case CLASS_EXC_AVR3 -> m.add(new ExcAVR3(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "vrmn"), asDouble(b, "vrmx"), asDouble(b, "te"), asDouble(b, "efdn"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asDouble(b, "tr")));
            case CLASS_EXC_AVR4 -> m.add(new ExcAVR4(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vrmn"), asDouble(b, "vrmx"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf1"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asDouble(b, "tr")));
            case CLASS_EXC_AVR5 -> m.add(new ExcAVR5(id, sm, asDouble(b, "ka"), asDouble(b, "rex"), asDouble(b, "ta"), asDouble(b, "tr")));
            case CLASS_EXC_AVR7 -> m.add(new ExcAVR7(id, sm, asDouble(b, "a1"), asDouble(b, "a2"), asDouble(b, "a3"), asDouble(b, "a4"), asDouble(b, "a5"), asDouble(b, "a6"), asDouble(b, "k1"), asDouble(b, "k3"), asDouble(b, "k5"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "vmax1"), asDouble(b, "vmax3"), asDouble(b, "vmax5"), asDouble(b, "vmin1"), asDouble(b, "vmin3"), asDouble(b, "vmin5")));
            case CLASS_EXC_BBC -> m.add(new ExcBBC(id, sm, asDouble(b, "ka"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "vrmin"), asDouble(b, "vrmax"), asDouble(b, "kn"), asDouble(b, "kp"), asDouble(b, "switch1"), asDouble(b, "efdmin"), asDouble(b, "efdmax"), asDouble(b, "xe")));
            case CLASS_EXC_CZ -> m.add(new ExcCZ(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tc"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "krb"), asDouble(b, "kp"), asDouble(b, "tp"), asDouble(b, "efdmax")));
            case CLASS_EXC_DC1A -> m.add(new ExcDC1A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"), asBoolean(b, "exclim")));
            case CLASS_EXC_DC2A -> m.add(new ExcDC2A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"), asBoolean(b, "exclim"), asDouble(b, "vlb")));
            case CLASS_EXC_DC3A -> m.add(new ExcDC3A(id, sm, asDouble(b, "trh"), asDouble(b, "kv"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf1"), asDouble(b, "efd1"), asDouble(b, "seefd1"), asDouble(b, "efd2"), asDouble(b, "seefd2"), asBoolean(b, "exclim")));
            case CLASS_EXC_ELIN1 -> m.add(new ExcELIN1(id, sm, asDouble(b, "tfi"), asDouble(b, "tnu"), asDouble(b, "ka"), asDouble(b, "ts1"), asDouble(b, "ts2"), asDouble(b, "dpnf"), asDouble(b, "vpu"), asDouble(b, "efmin"), asDouble(b, "efmax"), asDouble(b, "ks1"), asDouble(b, "ks2")));
            case CLASS_EXC_ELIN2 -> m.add(new ExcELIN2(id, sm, asDouble(b, "tr4"), asDouble(b, "k1"), asDouble(b, "t1"), asDouble(b, "p1"), asDouble(b, "p2"), asDouble(b, "ti1"), asDouble(b, "ti3"), asDouble(b, "ti4"), asDouble(b, "te"), asDouble(b, "ermin"), asDouble(b, "ermax"), asDouble(b, "kcse"), asDouble(b, "kce"), asDouble(b, "tc4"), asDouble(b, "tb4"), asDouble(b, "efdbas"), asDouble(b, "iefmax"), asDouble(b, "iefmax2"), asDouble(b, "efmin2"), asDouble(b, "k2"), asDouble(b, "t2")));
            case CLASS_EXC_HU -> m.add(new ExcHU(id, sm, asDouble(b, "ae"), asDouble(b, "ai"), asDouble(b, "atr"), asDouble(b, "emax"), asDouble(b, "emin"), asDouble(b, "imax"), asDouble(b, "imin"), asDouble(b, "ke"), asDouble(b, "ki"), asDouble(b, "te"), asDouble(b, "ti"), asDouble(b, "tr")));
            case CLASS_EXC_NI -> m.add(new ExcNI(id, sm, asDouble(b, "busfedSelector"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "te"), asDouble(b, "vrmn"), asDouble(b, "vrmx")));
            case CLASS_EXC_OEX3T -> m.add(new ExcOEX3T(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_PIC -> m.add(new ExcPIC(id, sm, asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "kf"), asDouble(b, "tf1"), asDouble(b, "tf2"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asDouble(b, "efdmax"), asDouble(b, "efdmin"), asDouble(b, "ka2"), asDouble(b, "vr1"), asDouble(b, "vr2"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "vrmax"), asDouble(b, "vrmin")));
            case CLASS_EXC_REXS -> m.add(new ExcREXS(id, sm, asDouble(b, "tr"), asDouble(b, "ta"), asDouble(b, "tb1"), asDouble(b, "tb2"), asDouble(b, "tc1"), asDouble(b, "tc2"), asDouble(b, "ka"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "te"), asDouble(b, "ke"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ki"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2"), asString(b, "feedbackSignal"), asBoolean(b, "exclfb")));
            case CLASS_EXC_RQB -> m.add(new ExcRQB(id, sm, asDouble(b, "clmt"), asDouble(b, "lsmin"), asDouble(b, "mesu"), asDouble(b, "t4m"), asDouble(b, "tc"), asDouble(b, "tf"), asDouble(b, "tout"), asDouble(b, "ucmax"), asDouble(b, "ucmin"), asDouble(b, "ki"), asDouble(b, "xp")));
            case CLASS_EXC_SCRX -> m.add(new ExcSCRX(id, sm, asDouble(b, "tr"), asDouble(b, "k"), asDouble(b, "tatb"), asDouble(b, "tb"), asDouble(b, "emax"), asDouble(b, "emin"), asBoolean(b, "rcmxFlag")));
            case CLASS_EXC_SEXS -> m.add(new ExcSEXS(id, sm, asDouble(b, "tr"), asDouble(b, "tatb"), asDouble(b, "tb"), asDouble(b, "k"), asDouble(b, "te"), asDouble(b, "emin"), asDouble(b, "emax")));
            case CLASS_EXC_SK -> m.add(new ExcSK(id, sm, asDouble(b, "k"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "kc"), asDouble(b, "kce"), asDouble(b, "kd"), asDouble(b, "kgob"), asDouble(b, "kp"), asDouble(b, "kqi"), asDouble(b, "kqob"), asDouble(b, "kqp"), asDouble(b, "nq"), asDouble(b, "qconoff"), asDouble(b, "qz"), asDouble(b, "remote"), asDouble(b, "sbase"), asDouble(b, "tc"), asDouble(b, "te"), asDouble(b, "ti"), asDouble(b, "tp"), asDouble(b, "tr"), asDouble(b, "uimax"), asDouble(b, "uimin"), asDouble(b, "urmax"), asDouble(b, "urmin"), asDouble(b, "vtmax"), asDouble(b, "vtmin"), asDouble(b, "yp")));
            case CLASS_EXC_ST1A -> m.add(new ExcST1A(id, sm, asDouble(b, "tr"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kc"), asDouble(b, "kf"), asDouble(b, "tf"), asBoolean(b, "pssin"), asBoolean(b, "ilr"), asDouble(b, "klr")));
            case CLASS_EXC_ST2A -> m.add(new ExcST2A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kc"), asDouble(b, "efdmax")));
            case CLASS_EXC_ST3 -> m.add(new ExcST3(id, sm, asDouble(b, "tr"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "km"), asDouble(b, "tm"), asDouble(b, "vmmax"), asDouble(b, "vmmin"), asDouble(b, "kg"), asDouble(b, "kp"), asDouble(b, "ki"), asDouble(b, "kc"), asDouble(b, "xl"), asDouble(b, "thetap"), asDouble(b, "vbmax")));
            case CLASS_EXC_ST4B -> m.add(new ExcST4B(id, sm, asDouble(b, "tr"), asDouble(b, "kpr"), asDouble(b, "kir"), asDouble(b, "ta"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kpm"), asDouble(b, "kim"), asDouble(b, "vmmax"), asDouble(b, "vmmin"), asDouble(b, "kg"), asDouble(b, "kp"), asDouble(b, "xl"), asDouble(b, "thetap"), asDouble(b, "vbmax"), asDouble(b, "kc")));
            case CLASS_EXC_ST6B -> m.add(new ExcST6B(id, sm, asDouble(b, "tr"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "kci"), asDouble(b, "kff"), asDouble(b, "kg"), asDouble(b, "kia"), asDouble(b, "klr"), asDouble(b, "km"), asDouble(b, "kpa"), asDouble(b, "kvd"), asDouble(b, "ilr"), asDouble(b, "tg"), asDouble(b, "ts"), asDouble(b, "tvd"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "vrmax"), asDouble(b, "vrmin")));
            case CLASS_EXC_ST7B -> m.add(new ExcST7B(id, sm, asDouble(b, "tr"), asDouble(b, "kh"), asDouble(b, "kia"), asDouble(b, "tia"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "tf"), asDouble(b, "kl"), asDouble(b, "kpa"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "vmax"), asDouble(b, "vmin"), asDouble(b, "tg")));
            case CLASS_EXC_SYMPTR -> m.add(new ExcSYMPTR(id, sm, asDouble(b, "efmx"), asDouble(b, "efmn"), asDouble(b, "vopi"), asDouble(b, "vres"), asDouble(b, "tr"), asDouble(b, "vrmax"), asDouble(b, "vrmin")));
            case CLASS_EXC_AC1A -> m.add(new ExcAC1A(id, sm, asDouble(b, "tr"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ke"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_AC2A -> m.add(new ExcAC2A(id, sm, asDouble(b, "tr"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "kb"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "te"), asDouble(b, "vfemax"), asDouble(b, "kh"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ke"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_AC3A -> m.add(new ExcAC3A(id, sm, asDouble(b, "tr"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "te"), asDouble(b, "vemin"), asDouble(b, "kr"), asDouble(b, "kf"), asDouble(b, "tf"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "ke"), asDouble(b, "vfemax"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_AC4A -> m.add(new ExcAC4A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "vimax"), asDouble(b, "vimin"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "kc")));
            case CLASS_EXC_AC5A -> m.add(new ExcAC5A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ke"), asDouble(b, "te"), asDouble(b, "kf"), asDouble(b, "tf1"), asDouble(b, "tf2"), asDouble(b, "tf3"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_AC6A -> m.add(new ExcAC6A(id, sm, asDouble(b, "tr"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "tk"), asDouble(b, "tb"), asDouble(b, "tc"), asDouble(b, "te"), asDouble(b, "ke"), asDouble(b, "vhmax"), asDouble(b, "kh"), asDouble(b, "tj"), asDouble(b, "th"), asDouble(b, "td"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            case CLASS_EXC_AC8B -> m.add(new ExcAC8B(id, sm, asDouble(b, "tr"), asDouble(b, "kpr"), asDouble(b, "kir"), asDouble(b, "kdr"), asDouble(b, "tdr"), asDouble(b, "vrmax"), asDouble(b, "vrmin"), asDouble(b, "ka"), asDouble(b, "ta"), asDouble(b, "vamax"), asDouble(b, "vamin"), asDouble(b, "te"), asDouble(b, "ke"), asDouble(b, "vfemax"), asDouble(b, "vemin"), asDouble(b, "kc"), asDouble(b, "kd"), asDouble(b, "e1"), asDouble(b, "se1"), asDouble(b, "e2"), asDouble(b, "se2")));
            default -> LOG.warn("Unhandled vendor exciter class: {}", cls);
        }
    }

    // =========================================================================
    // PSS
    // =========================================================================

    private void loadPssSB4(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_SB4, m); }

    private void loadPssIEEE1A(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_IEEE1A, m); }

    private void loadPssIEEE2B(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_IEEE2B, m); }

    private void loadPssIEEE3B(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_IEEE3B, m); }

    private void loadPssIEEE4B(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_IEEE4B, m); }

    private void loadPss1(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_1, m); }

    private void loadPss1A(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_1A, m); }

    private void loadPss2B(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_2B, m); }

    private void loadPss2ST(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_2ST, m); }

    private void loadPss5(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_5, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadPssPTIST1(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_PTIST1, m); }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private void loadPssPTIST3(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_PTIST3, m); }

    private void loadPssELIN2(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_ELIN2, m); }

    private void loadPssSH(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_SH, m); }

    private void loadPssWECC(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_WECC, m); }

    private void loadPssRQB(CgmesDyModel m) {
        loadPssGeneric(CLASS_PSS_RQB, m); }

    private void loadPssGeneric(String cls, CgmesDyModel m) {
        query(loadQuery(cls)).forEach(b -> {
            try {
                dispatchPss(cls, b, m);
            } catch (Exception e) {
                LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
            }
        });
    }

    private void dispatchPss(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String exc = asAssocId(b, EXCITER_ID);
        switch (cls) {
            case CLASS_PSS_SB4 -> m.add(new PssSB4(id, exc, asDouble(b, "tt"), asDouble(b, "kx"), asDouble(b, "tx1"), asDouble(b, "tx2"), asDouble(b, "tx3"), asDouble(b, "tx4"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_IEEE1A -> m.add(new PssIEEE1A(id, exc, asDouble(b, "inputSignalType"), asDouble(b, "kx"), asDouble(b, "t6"), asDouble(b, "ks"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_IEEE2B -> m.add(new PssIEEE2B(id, exc, asAssocId(b, "inputSignal1Type"), asAssocId(b, "inputSignal2Type"), asDouble(b, "ks1"), asDouble(b, "ks2"), asDouble(b, "ks3"), asDouble(b, "tw1"), asDouble(b, "tw2"), asDouble(b, "tw3"), asDouble(b, "tw4"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t6"), asDouble(b, "t7"), asDouble(b, "t8"), asDouble(b, "t9"), asDouble(b, "t10"), asDouble(b, "t11"), asDouble(b, "n"), asDouble(b, "m"), asDouble(b, "vstmax"), asDouble(b, "vstmin")));
            case CLASS_PSS_IEEE3B -> m.add(new PssIEEE3B(id, exc, asAssocId(b, "inputSignal1Type"), asAssocId(b, "inputSignal2Type"), asDouble(b, "ks1"), asDouble(b, "ks2"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "a1"), asDouble(b, "a2"), asDouble(b, "a3"), asDouble(b, "a4"), asDouble(b, "a5"), asDouble(b, "a6"), asDouble(b, "a7"), asDouble(b, "a8"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_IEEE4B -> m.add(new PssIEEE4B(id, exc,
                asDouble(b, "bwh1"), asDouble(b, "bwh2"), asDouble(b, "bwl1"), asDouble(b, "bwl2"),
                asDouble(b, "kh"), asDouble(b, "kh1"), asDouble(b, "kh11"), asDouble(b, "kh17"), asDouble(b, "kh2"),
                asDouble(b, "ki"), asDouble(b, "ki1"), asDouble(b, "ki11"), asDouble(b, "ki17"), asDouble(b, "ki2"),
                asDouble(b, "kl"), asDouble(b, "kl1"), asDouble(b, "kl11"), asDouble(b, "kl17"), asDouble(b, "kl2"),
                asDouble(b, "omeganh1"), asDouble(b, "omeganh2"), asDouble(b, "omeganl1"), asDouble(b, "omeganl2"),
                asDouble(b, "th1"), asDouble(b, "th10"), asDouble(b, "th11"), asDouble(b, "th12"), asDouble(b, "th2"), asDouble(b, "th3"), asDouble(b, "th4"), asDouble(b, "th5"), asDouble(b, "th6"), asDouble(b, "th7"), asDouble(b, "th8"), asDouble(b, "th9"),
                asDouble(b, "ti1"), asDouble(b, "ti10"), asDouble(b, "ti11"), asDouble(b, "ti12"), asDouble(b, "ti2"), asDouble(b, "ti3"), asDouble(b, "ti4"), asDouble(b, "ti5"), asDouble(b, "ti6"), asDouble(b, "ti7"), asDouble(b, "ti8"), asDouble(b, "ti9"),
                asDouble(b, "tl1"), asDouble(b, "tl10"), asDouble(b, "tl11"), asDouble(b, "tl12"), asDouble(b, "tl2"), asDouble(b, "tl3"), asDouble(b, "tl4"), asDouble(b, "tl5"), asDouble(b, "tl6"), asDouble(b, "tl7"), asDouble(b, "tl8"), asDouble(b, "tl9"),
                asDouble(b, "vsmax"), asDouble(b, "vsmin"), asDouble(b, "vshmax"), asDouble(b, "vshmin"), asDouble(b, "vsimax"), asDouble(b, "vsimin"), asDouble(b, "vslmax"), asDouble(b, "vslmin")));
            case CLASS_PSS_1 -> m.add(new Pss1(id, exc, asDouble(b, "kx"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_1A -> m.add(new Pss1A(id, exc, asAssocId(b, "inputSignalType"), asDouble(b, "a1"), asDouble(b, "a2"), asDouble(b, "ks"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "tdelay"), asDouble(b, "vcl"), asDouble(b, "vcu"), asDouble(b, "vrmax"), asDouble(b, "vrmin")));
            case CLASS_PSS_2B -> m.add(new Pss2B(id, exc, asAssocId(b, "inputSignal1Type"), asAssocId(b, "inputSignal2Type"), asDouble(b, "ks1"), asDouble(b, "ks2"), asDouble(b, "ks3"), asDouble(b, "tw1"), asDouble(b, "tw2"), asDouble(b, "tw3"), asDouble(b, "tw4"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t6"), asDouble(b, "t7"), asDouble(b, "t8"), asDouble(b, "t9"), asDouble(b, "t10"), asDouble(b, "t11"), asDouble(b, "n"), asDouble(b, "m"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_2ST -> m.add(new Pss2ST(id, exc, asAssocId(b, "inputSignal1Type"), asAssocId(b, "inputSignal2Type"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "t7"), asDouble(b, "t8"), asDouble(b, "t9"), asDouble(b, "t10"), asDouble(b, "lsmax"), asDouble(b, "lsmin")));
            case CLASS_PSS_5 -> m.add(new Pss5(id, exc, asDouble(b, "deadband"), asDouble(b, "isfreq"), asDouble(b, "kf"), asDouble(b, "kpe"), asDouble(b, "kpss"), asDouble(b, "ktgov"), asDouble(b, "pmin"), asDouble(b, "tl1"), asDouble(b, "tl2"), asDouble(b, "tl3"), asDouble(b, "tl4"), asDouble(b, "tpe"), asDouble(b, "tw1"), asDouble(b, "tw2"), asDouble(b, "vadat"), asDouble(b, "vsmn"), asDouble(b, "vsmx")));
            case CLASS_PSS_PTIST1 -> m.add(new PssPTIST1(id, exc, asDouble(b, "dtc"), asDouble(b, "dtf"), asDouble(b, "dtp"), asDouble(b, "k"), asDouble(b, "m"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "tp"), asDouble(b, "vsmn"), asDouble(b, "vsmx")));
            case CLASS_PSS_PTIST3 -> m.add(new PssPTIST3(id, exc, asDouble(b, "a0"), asDouble(b, "a1"), asDouble(b, "a2"), asDouble(b, "a3"), asDouble(b, "a4"), asDouble(b, "a5"), asDouble(b, "al"), asDouble(b, "athres"), asDouble(b, "b0"), asDouble(b, "b1"), asDouble(b, "b2"), asDouble(b, "b3"), asDouble(b, "b4"), asDouble(b, "b5"), asDouble(b, "dl"), asDouble(b, "dtc"), asDouble(b, "dtf"), asDouble(b, "dtp"), asDouble(b, "isfreq"), asDouble(b, "k"), asDouble(b, "lthres"), asDouble(b, "m"), asDouble(b, "nav"), asDouble(b, "ncl"), asDouble(b, "pmin"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "tp"), asDouble(b, "vsmn"), asDouble(b, "vsmx")));
            case CLASS_PSS_ELIN2 -> m.add(new PssELIN2(id, exc, asDouble(b, "apss"), asDouble(b, "ks1"), asDouble(b, "ks2"), asDouble(b, "ppss"), asDouble(b, "psslim"), asDouble(b, "ts1"), asDouble(b, "ts2"), asDouble(b, "ts3"), asDouble(b, "ts4"), asDouble(b, "ts5"), asDouble(b, "ts6")));
            case CLASS_PSS_SH -> m.add(new PssSH(id, exc, asDouble(b, "k"), asDouble(b, "k0"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "k3"), asDouble(b, "k4"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "td"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_WECC -> m.add(new PssWECC(id, exc, asAssocId(b, "inputSignal1Type"), asAssocId(b, "inputSignal2Type"), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "t4"), asDouble(b, "t5"), asDouble(b, "t6"), asDouble(b, "t7"), asDouble(b, "t8"), asDouble(b, "t9"), asDouble(b, "t10"), asDouble(b, "vsmax"), asDouble(b, "vsmin")));
            case CLASS_PSS_RQB -> m.add(new PssRQB(id, exc, asDouble(b, "kdpm"), asDouble(b, "ki2"), asDouble(b, "ki3"), asDouble(b, "ki4"), asDouble(b, "sibv"), asDouble(b, "t4f"), asDouble(b, "t4m"), asDouble(b, "t4mom"), asDouble(b, "tomd"), asDouble(b, "tomsl")));
            default -> LOG.warn("Unhandled PSS class: {}", cls);
        }
    }

    // =========================================================================
    // Synchronous machines
    // =========================================================================

    private void loadSyncMachines(CgmesDyModel model) {
        for (String cls : new String[]{CLASS_SYNC_SIMPLIFIED, CLASS_SYNC_DETAILED,
            CLASS_SYNC_EQUIV_CIRCUIT, CLASS_SYNC_TIME_CONST_REACTANCE}) {
            query(loadQuery(cls)).forEach(b -> {
                try {
                    dispatchSyncMachine(cls, b, model);
                } catch (Exception e) {
                    LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
                }
            });
        }
    }

    private void dispatchSyncMachine(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String smId = asAssocId(b, SYNC_MACHINE_ID);
        double mBase = asDouble(b, "mBase");
        double damp = asDouble(b, "damping");
        double inertia = asDouble(b, "inertia");
        double xlBase = asDouble(b, "statorLeakageReactance");
        double ra = asDouble(b, "statorResistance");
        switch (cls) {
            case CLASS_SYNC_SIMPLIFIED -> m.add(new SynchronousMachineSimplified(id, smId, mBase, damp, inertia, xlBase, ra));
            case CLASS_SYNC_DETAILED -> m.add(new SynchronousMachineDetailed(id, smId, mBase, damp, inertia, xlBase, ra,
                asAssocId(b, "ifdBaseType"), asDouble(b, "saturationFactor"), asDouble(b, "saturationFactor120")));
            case CLASS_SYNC_EQUIV_CIRCUIT -> m.add(new SynchronousMachineEquivalentCircuit(id, smId, mBase, damp, inertia, xlBase, ra,
                asAssocId(b, "ifdBaseType"), asDouble(b, "saturationFactor"), asDouble(b, "saturationFactor120"),
                asDouble(b, "r1d"), asDouble(b, "x1d"), asDouble(b, "r2d"), asDouble(b, "x2d"), asDouble(b, "rfd"), asDouble(b, "xfd"),
                asDouble(b, "r1q"), asDouble(b, "x1q"), asDouble(b, "r2q"), asDouble(b, "x2q"),
                asDouble(b, "xad"), asDouble(b, "xaq"), asDouble(b, "xf1d"), asDouble(b, "xmd"), asDouble(b, "xmq"), asDouble(b, "xl")));
            case CLASS_SYNC_TIME_CONST_REACTANCE -> m.add(new SynchronousMachineTimeConstantReactance(id, smId, mBase, damp, inertia, xlBase, ra,
                asAssocId(b, "ifdBaseType"), asDouble(b, "saturationFactor"), asDouble(b, "saturationFactor120"),
                asAssocId(b, "modelType"), asDouble(b, "ks"),
                asDouble(b, "xDirectSync"), asDouble(b, "xDirectTrans"), asDouble(b, "xDirectSubtrans"),
                asDouble(b, "xQuadSync"), asDouble(b, "xQuadTrans"), asDouble(b, "xQuadSubtrans"),
                asDouble(b, "tpdo"), asDouble(b, "tppdo"), asDouble(b, "tpqo"), asDouble(b, "tppqo"), asDouble(b, "xl")));
//            case CLASS_SYNC_USER_DEFINED -> m.add(new SynchronousMachineUserDefined(id, smId, mBase, damp, inertia, xlBase, ra, asBoolean(b, "proprietary")));
            default -> LOG.warn("Unhandled sync machine class: {}", cls);
        }
    }

    // =========================================================================
    // Asynchronous machines
    // =========================================================================

    private void loadAsyncMachines(CgmesDyModel model) {
        for (String cls : new String[]{CLASS_ASYNC_TIME_CONST_REACTANCE, CLASS_ASYNC_EQUIV_CIRCUIT}) {
            query(loadQuery(cls)).forEach(b -> {
                try {
                    dispatchAsyncMachine(cls, b, model);
                } catch (Exception e) {
                    LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
                }
            });
        }
    }

    private void dispatchAsyncMachine(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String amId = asAssocId(b, ASYNC_MACHINE_ID);
        switch (cls) {
            case CLASS_ASYNC_TIME_CONST_REACTANCE -> m.add(new AsynchronousMachineTimeConstantReactance(id, amId,
                asDouble(b, "xs"), asDouble(b, "xp"), asDouble(b, "xpp"), asDouble(b, "tpo"), asDouble(b, "tppo"), asDouble(b, "xl")));
            case CLASS_ASYNC_EQUIV_CIRCUIT -> m.add(new AsynchronousMachineEquivalentCircuit(id, amId,
                asDouble(b, "rr1"), asDouble(b, "xr1"), asDouble(b, "rr2"), asDouble(b, "xr2"),
                asDouble(b, "xm"), asDouble(b, "xs"), asDouble(b, "rs")));
//            case CLASS_ASYNC_USER_DEFINED -> m.add(new AsynchronousMachineUserDefined(id, amId, asBoolean(b, "proprietary")));
            default -> LOG.warn("Unhandled async machine class: {}", cls);
        }
    }

    // =========================================================================
    // Wind
    // =========================================================================

    private void loadWindTurbines(CgmesDyModel model) {
        for (String cls : new String[]{CLASS_WIND_TYPE1A_IEC, CLASS_WIND_TYPE1B_IEC, CLASS_WIND_TYPE2_IEC,
            CLASS_WIND_TYPE3A_IEC, CLASS_WIND_TYPE3B_IEC, CLASS_WIND_TYPE4A_IEC, CLASS_WIND_TYPE4B_IEC}) {
            query(loadQuery(cls)).forEach(b -> {
                try {
                    dispatchWindTurbine(cls, b, model);
                } catch (Exception e) {
                    LOG.warn("Skipping {} {}: {}", cls, resourceId(b), e.getMessage());
                }
            });
        }
    }

    private void dispatchWindTurbine(String cls, PropertyBag b, CgmesDyModel m) {
        String id = resourceId(b);
        String pp = asAssocId(b, POWER_PLANT_ID);
        switch (cls) {
            case CLASS_WIND_TYPE1A_IEC -> m.add(new WindGenTurbineType1aIEC(id, pp, asString(b, "windAeroConstIECId"), asString(b, "windProtectionIECId"), asString(b, "windMechIECId"), asString(b, "asynchronousMachineId")));
            case CLASS_WIND_TYPE1B_IEC -> m.add(new WindGenTurbineType1bIEC(id, pp, asString(b, "windAeroLinearIECId"), asString(b, "windProtectionIECId"), asString(b, "windContPitchAngleIECId"), asString(b, "windMechIECId"), asString(b, "asynchronousMachineId")));
            case CLASS_WIND_TYPE2_IEC -> m.add(new WindGenTurbineType2IEC(id, pp, asString(b, "windAeroLinearIECId"), asString(b, "windProtectionIECId"), asString(b, "windContPitchAngleIECId"), asString(b, "windContRotorRIECId"), asString(b, "windMechIECId"), asString(b, "asynchronousMachineId")));
            case CLASS_WIND_TYPE3A_IEC -> m.add(new WindGenTurbineType3aIEC(id, pp, asString(b, "windContPType3IECId"), asString(b, "windContQIECId"), asString(b, "windMechIECId"), asString(b, "windContCurrLimIECId"), asString(b, "windProtectionIECId"), asDouble(b, "kpc"), asDouble(b, "tic"), asDouble(b, "xs")));
            case CLASS_WIND_TYPE3B_IEC -> m.add(new WindGenTurbineType3bIEC(id, pp, asString(b, "windContPType3IECId"), asString(b, "windContQIECId"), asString(b, "windMechIECId"), asString(b, "windContCurrLimIECId"), asString(b, "windProtectionIECId"), asDouble(b, "fthres"), asDouble(b, "mwtcwp"), asDouble(b, "tg"), asDouble(b, "two")));
            case CLASS_WIND_TYPE4A_IEC -> m.add(new WindGenTurbineType4aIEC(id, pp, asString(b, "windContPType4aIECId"), asString(b, "windContQIECId"), asString(b, "windContCurrLimIECId"), asString(b, "windProtectionIECId"), asDouble(b, "dipmax"), asDouble(b, "diqmax"), asDouble(b, "tg")));
            case CLASS_WIND_TYPE4B_IEC -> m.add(new WindGenTurbineType4bIEC(id, pp, asString(b, "windContPType4bIECId"), asString(b, "windContQIECId"), asString(b, "windMechIECId"), asString(b, "windContCurrLimIECId"), asString(b, "windProtectionIECId"), asDouble(b, "dipmax"), asDouble(b, "diqmax"), asDouble(b, "tg")));
            default -> LOG.warn("Unhandled wind turbine class: {}", cls);
        }
    }

    private void loadWindSubModels(CgmesDyModel m) {
        query(loadQuery(CLASS_WIND_PLANT_IEC)).forEach(b -> m.add(new WindPlantIEC(resourceId(b), asString(b, "windPlantFreqPcontrolIECId"), asString(b, "windPlantReactiveControlIECId"))));
        query(loadQuery(CLASS_WIND_MECH_IEC)).forEach(b -> m.add(new WindMechIEC(resourceId(b), asDouble(b, "cdrt"), asDouble(b, "hgen"), asDouble(b, "hwtr"), asDouble(b, "kdrt"))));
        query(loadQuery(CLASS_WIND_AERO_CONST)).forEach(b -> m.add(new WindAeroConstIEC(resourceId(b))));
        query(loadQuery(CLASS_WIND_AERO_LINEAR)).forEach(b -> m.add(new WindAeroLinearIEC(resourceId(b), asDouble(b, "dpomega"), asDouble(b, "dptheta"), asDouble(b, "omegazero"), asDouble(b, "pavail"))));
        query(loadQuery(CLASS_WIND_CONT_PITCH)).forEach(b -> m.add(new WindContPitchAngleIEC(resourceId(b), asDouble(b, "dthetamax"), asDouble(b, "dthetamin"), asDouble(b, "kic"), asDouble(b, "kiomega"), asDouble(b, "kpc"), asDouble(b, "kpomega"), asDouble(b, "kpx"), asDouble(b, "thetamax"), asDouble(b, "thetamin"), asDouble(b, "ttheta"))));
        query(loadQuery(CLASS_WIND_CONT_PTYPE3)).forEach(b -> m.add(new WindContPType3IEC(resourceId(b), asDouble(b, "dpmax"), asDouble(b, "dprefmax"), asDouble(b, "dprefmin"), asDouble(b, "dthetamx"), asDouble(b, "kdtd"), asDouble(b, "kip"), asDouble(b, "kpp"), asDouble(b, "mplvrt"), asDouble(b, "omegaoffset"), asDouble(b, "pdtdmax"), asDouble(b, "rramp"), asDouble(b, "tdvs"), asDouble(b, "temin"), asDouble(b, "tpord"), asDouble(b, "tufilt"), asDouble(b, "tuscale"), asDouble(b, "twref"), asDouble(b, "udvs"), asDouble(b, "updip"), asDouble(b, "wdtd"), asDouble(b, "zeta"), asBoolean(b, "recrossflag"))));
        query(loadQuery(CLASS_WIND_CONT_PTYPE4A)).forEach(b -> m.add(new WindContPType4aIEC(resourceId(b), asDouble(b, "dpmax"), asDouble(b, "dpmin"), asDouble(b, "tpord"), asDouble(b, "tufilt"))));
        query(loadQuery(CLASS_WIND_CONT_PTYPE4B)).forEach(b -> m.add(new WindContPType4bIEC(resourceId(b), asDouble(b, "dpmax"), asDouble(b, "dpmin"), asDouble(b, "tpaero"), asDouble(b, "tpord"), asDouble(b, "tufilt"))));
        query(loadQuery(CLASS_WIND_CONT_Q)).forEach(b -> m.add(new WindContQIEC(resourceId(b), asDouble(b, "iqh1"), asDouble(b, "iqmax"), asDouble(b, "iqmin"), asDouble(b, "iqpost"), asDouble(b, "kiq"), asDouble(b, "kiu"), asDouble(b, "kpq"), asDouble(b, "kpu"), asDouble(b, "kqv"), asDouble(b, "rdroop"), asDouble(b, "tpfilt"), asDouble(b, "tpost"), asDouble(b, "tqord"), asDouble(b, "tufilt"), asDouble(b, "udb1"), asDouble(b, "udb2"), asDouble(b, "umax"), asDouble(b, "umin"), asDouble(b, "uqdip"), asDouble(b, "uref0"), asDouble(b, "xdroop"), asString(b, "mconq"), asString(b, "mqfrt"), asString(b, "mqpri"), asString(b, "windLVRTQcontrolModeType"))));
        query(loadQuery(CLASS_WIND_CONT_ROTOR_R)).forEach(b -> m.add(new WindContRotorRIEC(resourceId(b), asDouble(b, "kirr"), asDouble(b, "komegafilt"), asDouble(b, "kpfilt"), asDouble(b, "kprr"), asDouble(b, "rmax"), asDouble(b, "rmin"), asDouble(b, "tomegafilt"), asDouble(b, "tpfilt"))));
        query(loadQuery(CLASS_WIND_CURR_LIM)).forEach(b -> m.add(new WindContCurrLimIEC(resourceId(b), asDouble(b, "imax"), asDouble(b, "imaxdip"), asDouble(b, "kpqu"), asDouble(b, "mdfslim"), asDouble(b, "mqpri"), asDouble(b, "tufilt"), asDouble(b, "upqumax"))));
        query(loadQuery(CLASS_WIND_PITCH_EMUL)).forEach(b -> m.add(new WindPitchContEmulIEC(resourceId(b), asDouble(b, "kdroop"), asDouble(b, "kipce"), asDouble(b, "kppce"), asDouble(b, "omegatr"), asDouble(b, "pimax"), asDouble(b, "pimin"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "thetamax"), asDouble(b, "thetamin"))));
        query(loadQuery(CLASS_WIND_PLANT_FREQ)).forEach(b -> m.add(new WindPlantFreqPcontrolIEC(resourceId(b), asDouble(b, "dpref"), asDouble(b, "ki"), asDouble(b, "kp"), asDouble(b, "prefmax"), asDouble(b, "prefmin"), asDouble(b, "tft"), asDouble(b, "tlag"), asDouble(b, "twpfiltp"), asDouble(b, "twpfiltu"))));
        query(loadQuery(CLASS_WIND_PLANT_REACT)).forEach(b -> m.add(new WindPlantReactiveControlIEC(resourceId(b), asDouble(b, "ki"), asDouble(b, "kp"), asDouble(b, "kqi"), asDouble(b, "kqp"), asDouble(b, "kuu"), asDouble(b, "twpfiltp"), asDouble(b, "twppfilt"), asDouble(b, "twpqfilt"), asDouble(b, "twpufilt"), asDouble(b, "txfp"), asDouble(b, "txft"), asDouble(b, "uwpqdip"), asDouble(b, "xrefmax"), asDouble(b, "xrefmin"))));
        query(loadQuery(CLASS_WIND_PROTECTION)).forEach(b -> m.add(new WindProtectionIEC(resourceId(b), asDouble(b, "dfimax"), asDouble(b, "fover"), asDouble(b, "funder"), asDouble(b, "mzc"), asDouble(b, "tfma"), asDouble(b, "uover"), asDouble(b, "uunder"))));
    }

    // =========================================================================
    // Load models
    // =========================================================================

    private void loadLoadModels(CgmesDyModel m) {
        query(loadQuery(CLASS_LOAD_STATIC)).forEach(b -> {
            try {
                m.add(new LoadStatic(resourceId(b), asString(b, "energyConsumerId"), asDouble(b, "kp1"), asDouble(b, "kp2"), asDouble(b, "kp3"), asDouble(b, "kp4"), asDouble(b, "kp5"), asDouble(b, "kp6"), asDouble(b, "kq1"), asDouble(b, "kq2"), asDouble(b, "kq3"), asDouble(b, "kq4"), asDouble(b, "kq5"), asDouble(b, "kq6"), asDouble(b, "ep1"), asDouble(b, "ep2"), asDouble(b, "ep3"), asDouble(b, "eq1"), asDouble(b, "eq2"), asDouble(b, "eq3"), asString(b, "staticLoadModelType")));
            } catch (Exception e) {
                LOG.warn("Skipping LoadStatic {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_LOAD_COMPOSITE)).forEach(b -> {
            try {
                m.add(new LoadComposite(resourceId(b), asString(b, "energyConsumerId"), asDouble(b, "epvs"), asDouble(b, "epfs"), asDouble(b, "eqvs"), asDouble(b, "eqfs"), asDouble(b, "epvd"), asDouble(b, "epfd"), asDouble(b, "eqvd"), asDouble(b, "eqfd"), asDouble(b, "mv"), asDouble(b, "mf"), asDouble(b, "lfmac"), asDouble(b, "lfs"), asDouble(b, "lfrac"), asDouble(b, "pfrac"), asDouble(b, "td"), asDouble(b, "tf"), asDouble(b, "tc"), asDouble(b, "xm"), asDouble(b, "xp"), asDouble(b, "xpp"), asDouble(b, "ls"), asDouble(b, "ra"), asDouble(b, "tpo"), asDouble(b, "tppo")));
            } catch (Exception e) {
                LOG.warn("Skipping LoadComposite {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_LOAD_AGGREGATE)).forEach(b -> {
            try {
                m.add(new LoadAggregate(resourceId(b), asString(b, "energyConsumerId"), asString(b, "loadMotorId"), asString(b, "loadStaticId")));
            } catch (Exception e) {
                LOG.warn("Skipping LoadAggregate {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_LOAD_MOTOR)).forEach(b -> {
            try {
                m.add(new LoadMotor(resourceId(b), asDouble(b, "pfrac"), asDouble(b, "ls"), asDouble(b, "ra"), asDouble(b, "lp"), asDouble(b, "lpp"), asDouble(b, "tpo"), asDouble(b, "tppo"), asDouble(b, "h"), asDouble(b, "d"), asDouble(b, "vt"), asDouble(b, "tv"), asDouble(b, "tbkr"), asDouble(b, "lfac"), asDouble(b, "compPF"), asDouble(b, "vbrkr"), asDouble(b, "vc1off"), asDouble(b, "vc2off"), asDouble(b, "vc1on"), asDouble(b, "vc2on")));
            } catch (Exception e) {
                LOG.warn("Skipping LoadMotor {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_LOAD_GENERIC_NL)).forEach(b -> {
            try {
                m.add(new LoadGenericNonLinear(resourceId(b), asString(b, "energyConsumerId"), asString(b, "genericNonLinearLoadModelType"), asDouble(b, "bs"), asDouble(b, "bt"), asDouble(b, "ls"), asDouble(b, "lt"), asDouble(b, "pt"), asDouble(b, "qt"), asDouble(b, "tp"), asDouble(b, "tq")));
            } catch (Exception e) {
                LOG.warn("Skipping LoadGenericNonLinear {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_MECH_LOAD1)).forEach(b -> {
            try {
                m.add(new MechLoad1(resourceId(b), asAssocId(b, ASYNC_MACHINE_ID), asDouble(b, "a"), asDouble(b, "b"), asDouble(b, "d"), asDouble(b, "e")));
            } catch (Exception e) {
                LOG.warn("Skipping MechLoad1 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    // =========================================================================
    // HVDC
    // =========================================================================

    private void loadHvdc(CgmesDyModel m) {
        query(loadQuery(CLASS_CS_CONVERTER)).forEach(b -> {
            try {
                m.add(new CsConverterDynamics(resourceId(b), asAssocId(b, "csConverterId"), asDouble(b, "alpha"), asDouble(b, "gamma"), asDouble(b, "maxAlpha"), asDouble(b, "minAlpha"), asDouble(b, "maxGamma"), asDouble(b, "minGamma")));
            } catch (Exception e) {
                LOG.warn("Skipping CsConverter {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_VS_CONVERTER)).forEach(b -> {
            try {
                m.add(new VsConverterDynamics(resourceId(b), asAssocId(b, "vsConverterId"), asDouble(b, "droop"), asDouble(b, "droopCompensation"), asDouble(b, "pPccControl"), asDouble(b, "qPccControl"), asDouble(b, "maxModulationIndex"), asDouble(b, "maxValveCurrent")));
            } catch (Exception e) {
                LOG.warn("Skipping VsConverter {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    // =========================================================================
    // Protection / Limiters
    // =========================================================================

    private void loadProtection(CgmesDyModel m) {
        query(loadQuery(CLASS_DISC_EXC_DEC1A)).forEach(b -> {
            try {
                m.add(new DiscExcContIEEEDEC1A(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "vtlmt"), asDouble(b, "vomax"), asDouble(b, "vomin"), asDouble(b, "vdis"), asDouble(b, "vethr"), asDouble(b, "vanmax"), asDouble(b, "vtm"), asDouble(b, "vtn"), asDouble(b, "vsmin"), asDouble(b, "escrv"), asDouble(b, "kan"), asDouble(b, "td"), asDouble(b, "vsmax"), asDouble(b, "tan"), asDouble(b, "esc")));
            } catch (Exception e) {
                LOG.warn("Skipping DiscExcDEC1A {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_DISC_EXC_DEC2A)).forEach(b -> {
            try {
                m.add(new DiscExcContIEEEDEC2A(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "td1"), asDouble(b, "td2"), asDouble(b, "vdmax"), asDouble(b, "vdmin")));
            } catch (Exception e) {
                LOG.warn("Skipping DiscExcDEC2A {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_DISC_EXC_DEC3A)).forEach(b -> {
            try {
                m.add(new DiscExcContIEEEDEC3A(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "tdr"), asDouble(b, "vtmin")));
            } catch (Exception e) {
                LOG.warn("Skipping DiscExcDEC3A {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_OEL_IEEE)).forEach(b -> {
            try {
                m.add(new OverexcLimIEEE(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "ifdmax"), asDouble(b, "ifdlim"), asDouble(b, "itfpu"), asDouble(b, "hyst"), asDouble(b, "kcd"), asDouble(b, "kramp")));
            } catch (Exception e) {
                LOG.warn("Skipping OelIEEE {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_OEL_X)).forEach(b -> {
            try {
                m.add(new OverexcLimX(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "efd1"), asDouble(b, "efd2"), asDouble(b, "efd3"), asDouble(b, "efddes"), asDouble(b, "efdrated"), asDouble(b, "kmx"), asDouble(b, "srew"), asDouble(b, "t1"), asDouble(b, "t2"), asDouble(b, "t3"), asDouble(b, "vlow")));
            } catch (Exception e) {
                LOG.warn("Skipping OelX {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_UEL_IEEE1)).forEach(b -> {
            try {
                m.add(new UnderexcLimIEEE1(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "kur"), asDouble(b, "kuc"), asDouble(b, "kuf"), asDouble(b, "vurmax"), asDouble(b, "vuimax"), asDouble(b, "vuimin"), asDouble(b, "tu1"), asDouble(b, "tu2"), asDouble(b, "tu3"), asDouble(b, "tu4")));
            } catch (Exception e) {
                LOG.warn("Skipping UelIEEE1 {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_UEL_IEEE2)).forEach(b -> {
            try {
                m.add(new UnderexcLimIEEE2(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "k1"), asDouble(b, "k2"), asDouble(b, "kfb"), asDouble(b, "kuf"), asDouble(b, "kui"), asDouble(b, "kul"), asDouble(b, "p0"), asDouble(b, "p1"), asDouble(b, "q0"), asDouble(b, "q1"), asDouble(b, "tu1"), asDouble(b, "tu2"), asDouble(b, "tu3"), asDouble(b, "tu4"), asDouble(b, "vuimax"), asDouble(b, "vuimin")));
            } catch (Exception e) {
                LOG.warn("Skipping UelIEEE2 {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_UEL_X1)).forEach(b -> {
            try {
                m.add(new UnderexcLimX1(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "k"), asDouble(b, "kf2"), asDouble(b, "km"), asDouble(b, "melmax"), asDouble(b, "tf2"), asDouble(b, "tm")));
            } catch (Exception e) {
                LOG.warn("Skipping UelX1 {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_UEL_X2)).forEach(b -> {
            try {
                m.add(new UnderexcLimX2(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "ki"), asDouble(b, "kuf"), asDouble(b, "kui"), asDouble(b, "kul"), asDouble(b, "p0"), asDouble(b, "p1"), asDouble(b, "q0"), asDouble(b, "q1"), asDouble(b, "tu1"), asDouble(b, "tu2"), asDouble(b, "tu3"), asDouble(b, "tu4"), asDouble(b, "vuimax"), asDouble(b, "vuimin")));
            } catch (Exception e) {
                LOG.warn("Skipping UelX2 {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_VOLTAGE_ADJ)).forEach(b -> {
            try {
                m.add(new VoltageAdjusterIEEE(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "ka"), asDouble(b, "tamax"), asDouble(b, "tamin"), asDouble(b, "vimax"), asDouble(b, "smax")));
            } catch (Exception e) {
                LOG.warn("Skipping VoltageAdjuster {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_VOLTAGE_COMP)).forEach(b -> {
            try {
                m.add(new VoltageCompensatorIEEE(resourceId(b), asAssocId(b, EXCITER_ID), asDouble(b, "tr"), asDouble(b, "rc"), asDouble(b, "xc")));
            } catch (Exception e) {
                LOG.warn("Skipping VoltageCompensator {}: {}", resourceId(b), e.getMessage());
            }
        });
        query(loadQuery(CLASS_VCOMP_IEEE_TYPE1)).forEach(b -> {
            try {
                m.add(new VCompIEEEType1(resourceId(b), asAssocId(b, EXCITER_ID),
                        asDouble(b, "tr"), asDouble(b, "rc"), asDouble(b, "xc")));
            } catch (Exception e) {
                LOG.warn("Skipping VCompIEEEType1 {}: {}", resourceId(b), e.getMessage());
            }
        });
    }

    // =========================================================================
    // User-defined models
    // =========================================================================

//    private void loadUserDefined(CgmesDyModel model) {
//        for (String cls : new String[]{
//            CLASS_EXCITER_USER_DEF, CLASS_GOV_USER_DEF, CLASS_PSS_USER_DEF,
//            CLASS_LOAD_USER_DEF, CLASS_SYNC_USER_DEF, CLASS_ASYNC_USER_DEF,
//            CLASS_VSC_USER_DEF, CLASS_CSC_USER_DEF,
//            CLASS_WIND_PLANT_USER_DEF, CLASS_WIND_TYPE1OR2_USER_DEF, CLASS_WIND_TYPE3OR4_USER_DEF}) {
//            query(loadQuery(cls)).forEach(b -> {
//                try {
//                    String assocId = Arrays.stream(new String[]{SYNC_MACHINE_ID, ASYNC_MACHINE_ID, EXCITER_ID, POWER_PLANT_ID})
//                        .map(f -> asStringOrNull(b, f)).filter(v -> v != null && !v.isEmpty()).findFirst().orElse("");
//                    model.add(new UserDefinedModel(resourceId(b), cls, assocId, asBoolean(b, "proprietary")));
//                } catch (Exception e) {
//                    LOG.warn("Skipping UserDefined {} {}: {}", cls, resourceId(b), e.getMessage());
//                }
//            });
//        }
//    }

    // =========================================================================
    // Summary
    // =========================================================================

    private String buildSummary(CgmesDyModel m) {
        return String.format(
            "steamGov=%d hydroGov=%d gasGov=%d ieeeExc=%d vendorExc=%d pss=%d syncMach=%d asyncMach=%d wind=%d load=%d hvdc=%d protection=%d",
            m.govSteam0List().size() + m.govSteam1List().size() + m.govSteam2List().size() + m.govSteamCCList().size() + m.govSteamEUList().size() + m.govSteamFV2List().size() + m.govSteamFV3List().size() + m.govSteamFV4List().size() + m.govSteamIEEE1List().size() + m.govSteamSGOList().size(),
            m.govHydro1List().size() + m.govHydro2List().size() + m.govHydro3List().size() + m.govHydro4List().size() + m.govHydroDDList().size() + m.govHydroFrancisList().size() + m.govHydroIEEE0List().size() + m.govHydroIEEE2List().size() + m.govHydroPeltonList().size() + m.govHydroPIDList().size() + m.govHydroPID2List().size() + m.govHydroRList().size() + m.govHydroWEHList().size() + m.govHydroWPIDList().size(),
            m.govGASTList().size() + m.govGAST1List().size() + m.govGAST2List().size() + m.govGAST3List().size() + m.govGAST4List().size() + m.govGASTWDList().size() + m.govCT1List().size() + m.govCT2List().size(),
            m.excIEEEDC1AList().size() + m.excIEEEDC2AList().size() + m.excIEEEDC3AList().size() + m.excIEEEDC4BList().size() + m.excIEEEAC1AList().size() + m.excIEEEAC2AList().size() + m.excIEEEAC3AList().size() + m.excIEEEAC4AList().size() + m.excIEEEAC5AList().size() + m.excIEEEAC6AList().size() + m.excIEEEAC7BList().size() + m.excIEEEAC8BList().size() + m.excIEEEST1AList().size() + m.excIEEEST2AList().size() + m.excIEEEST3AList().size() + m.excIEEEST4BList().size() + m.excIEEEST5BList().size() + m.excIEEEST6BList().size() + m.excIEEEST7BList().size(),
            m.excAVR1List().size() + m.excAVR2List().size() + m.excAVR3List().size() + m.excAVR4List().size() + m.excAVR5List().size() + m.excAVR7List().size() + m.excBBCList().size() + m.excCZList().size() + m.excDC1AList().size() + m.excDC2AList().size() + m.excDC3AList().size() + m.excELIN1List().size() + m.excELIN2List().size() + m.excHUList().size() + m.excNIList().size() + m.excOEX3TList().size() + m.excPICList().size() + m.excREXSList().size() + m.excRQBList().size() + m.excSCRXList().size() + m.excSEXSList().size() + m.excSKList().size() + m.excST1AList().size() + m.excST2AList().size() + m.excST3List().size() + m.excST4BList().size() + m.excST6BList().size() + m.excST7BList().size() + m.excSYMPTRList().size() + m.excAC1AList().size() + m.excAC2AList().size() + m.excAC3AList().size() + m.excAC4AList().size() + m.excAC5AList().size() + m.excAC6AList().size() + m.excAC8BList().size(),
            m.pssSB4List().size() + m.pssIEEE1AList().size() + m.pssIEEE2BList().size() + m.pssIEEE3BList().size() + m.pssIEEE4BList().size() + m.pss1List().size() + m.pss1AList().size() + m.pss2BList().size() + m.pss2STList().size() + m.pss5List().size() + m.pssPTIST1List().size() + m.pssPTIST3List().size() + m.pssELIN2List().size() + m.pssSHList().size() + m.pssWECCList().size() + m.pssRQBList().size(),
            m.syncSimplifiedList().size() + m.syncDetailedList().size() + m.syncEquivCircuitList().size() + m.syncTimeConstReactanceList().size(),
//                    + m.syncUserDefinedList().size()
            m.asyncTimeConstReactanceList().size() + m.asyncEquivCircuitList().size(),
//                    m.asyncUserDefinedList().size(),
            m.windType1aList().size() + m.windType1bList().size() + m.windType2List().size() + m.windType3aList().size() + m.windType3bList().size() + m.windType4aList().size() + m.windType4bList().size(),
            m.loadStaticList().size() + m.loadCompositeList().size() + m.loadAggregateList().size() + m.loadMotorList().size() + m.loadGenericNLList().size() + m.mechLoad1List().size(),
            m.csConverterList().size() + m.vsConverterList().size(),
            m.discExcDEC1AList().size() + m.discExcDEC2AList().size() + m.discExcDEC3AList().size() + m.oelIEEEList().size() + m.oelXList().size() + m.uelIEEE1List().size() + m.uelIEEE2List().size() + m.uelX1List().size() + m.uelX2List().size() + m.voltageAdjList().size() + m.voltageCompList().size() + m.vCompIEEEType1List().size()
//            m.userDefinedList().size()
        );
    }
}
