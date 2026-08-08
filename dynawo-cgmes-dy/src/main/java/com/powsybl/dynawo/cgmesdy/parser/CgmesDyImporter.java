/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.parser;

import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.dynawo.cgmesdy.CgmesDyConstants;
import com.powsybl.dynawo.cgmesdy.CgmesDyModel;
import com.powsybl.triplestore.api.TripleStore;
import com.powsybl.triplestore.api.TripleStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Top-level entry point for CGMES DY (Dynamics) profile parsing.
 *
 * <p>Builds a powsybl {@link TripleStore} from one or more RDF/XML DY files,
 * then delegates to {@link CgmesDyModelLoader} to populate a {@link CgmesDyModel}.</p>
 *
 * <h3>Typical usage</h3>
 * <pre>{@code
 *   // From a single .xml file on the file system:
 *   CgmesDyImporter importer = new CgmesDyImporter();
 *   CgmesDyModel model = importer.importDy(Path.of("network_DY.xml"));
 *
 *   // Process governors:
 *   model.govSteam1List().forEach(gs -> System.out.println(gs.id()));
 *
 *   // From a powsybl DataSource (e.g. a zip archive):
 *   CgmesDyModel model2 = importer.importDy(dataSource, "network_DY.xml");
 * }</pre>
 *
 * <h3>Namespace auto-detection</h3>
 * <p>The importer tries CIM16 first. If the triplestore yields no results for
 * a sentinel class (GovSteam0), it retries with CIM17. Set the namespace
 * explicitly via {@link #importDy(Path, String)} to skip detection.</p>
 *
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public final class CgmesDyImporter {

    private static final Logger LOG = LoggerFactory.getLogger(CgmesDyImporter.class);

    /** Context name used when loading RDF documents into the triplestore. */
    private static final String DY_CONTEXT = "urn:powsybl:cgmes-dy";

    /**
     * Detect the triplestore implementation to use.
     * Falls back gracefully to the first available implementation if "rdf4j" is absent.
     */
    private static final String TRIPLESTORE_IMPL = resolveTriplestoreImpl();

    private static String resolveTriplestoreImpl() {
        try {
            Class.forName("com.powsybl.triplestore.impl.rdf4j.TripleStoreRDF4J");
            return "rdf4j";
        } catch (ClassNotFoundException ignored) { }
        return "jena"; // default
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Parse a single DY RDF/XML file and return the populated model.
     * The CIM namespace is auto-detected.
     *
     * @param dyFilePath path to the DY .xml / .rdf file
     * @return populated {@link CgmesDyModel}
     * @throws IOException if the file cannot be read
     */
    public CgmesDyModel importDy(Path dyFilePath) throws IOException {
        Objects.requireNonNull(dyFilePath, "dyFilePath must not be null");
        LOG.info("Importing CGMES DY profile from: { }", dyFilePath);
        try (InputStream is = Files.newInputStream(dyFilePath)) {
            return importDy(is, autoDetectNamespace(dyFilePath));
        }
    }

    /**
     * Parse a single DY file with an explicit CIM namespace URI.
     *
     * @param dyFilePath  path to the DY file
     * @param cimNamespace e.g. {@link CgmesDyConstants#CIM16_NS} or {@link CgmesDyConstants#CIM17_NS}
     */
    public CgmesDyModel importDy(Path dyFilePath, String cimNamespace) throws IOException {
        Objects.requireNonNull(dyFilePath);
        Objects.requireNonNull(cimNamespace);
        LOG.info("Importing CGMES DY profile from: { } using namespace: { }", dyFilePath, cimNamespace);
        try (InputStream is = Files.newInputStream(dyFilePath)) {
            return importDy(is, cimNamespace);
        }
    }

    /**
     * Parse from a powsybl {@link ReadOnlyDataSource} (e.g. a CGMES zip archive).
     *
     * @param dataSource    powsybl data source
     * @param dyEntryName   name of the DY entry inside the data source
     */
    public CgmesDyModel importDy(ReadOnlyDataSource dataSource, String dyEntryName) throws IOException {
        Objects.requireNonNull(dataSource);
        Objects.requireNonNull(dyEntryName);
        LOG.info("Importing CGMES DY profile '{ }' from datasource", dyEntryName);
        try (InputStream is = dataSource.newInputStream(dyEntryName)) {
            TripleStore ts = buildTripleStore(is);
            String ns = detectNamespace(ts);
            return new CgmesDyModelLoader(ts, ns).load();
        }
    }

    /**
     * Low-level: parse directly from an {@link InputStream}.
     *
     * @param inputStream  RDF/XML stream of the DY profile
     * @param cimNamespace CIM namespace URI
     */
    public CgmesDyModel importDy(InputStream inputStream, String cimNamespace) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(cimNamespace);
        TripleStore ts = buildTripleStore(inputStream);
        return new CgmesDyModelLoader(ts, cimNamespace).load();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Build a fresh {@link TripleStore}, load the DY RDF/XML stream into it,
     * and return the store ready for querying.
     */
    private static TripleStore buildTripleStore(InputStream is) {
        TripleStore ts = TripleStoreFactory.create(TRIPLESTORE_IMPL);
        try {
            ts.read(is, CgmesDyConstants.RDF_NS, DY_CONTEXT);
        } catch (Exception e) {
            throw new CgmesDyParseException("Failed to load DY RDF/XML into triplestore", e);
        }
        return ts;
    }

    /**
     * Sniff the namespace from the file name: files containing "CIM17" or "CGMES3"
     * in their name are assumed to be CIM17.  All others default to CIM16.
     */
    private static String autoDetectNamespace(Path filePath) {
        String name = filePath.getFileName().toString().toUpperCase();
        if (name.contains("CIM17") || name.contains("CGMES3") || name.contains("IEC61970-600")) {
            LOG.debug("Auto-detected CIM17 namespace from file name: { }", filePath.getFileName());
            return CgmesDyConstants.CIM17_NS;
        }
        LOG.debug("Defaulting to CIM16 namespace for file: { }", filePath.getFileName());
        return CgmesDyConstants.CIM16_NS;
    }

    /**
     * Detect namespace by running a cheap sentinel query.
     * If the CIM16 prefix produces no results for the GovSteam0 class, fall back to CIM17.
     */
    private static String detectNamespace(TripleStore ts) {
        String sentinelQuery16 = CgmesDyConstants.SPARQL_PREFIXES_CIM16 +
            "SELECT (COUNT(?id) AS ?n) WHERE { ?id rdf:type cim:GovSteam0 . }";
        String sentinelQuery17 = CgmesDyConstants.SPARQL_PREFIXES_CIM17 +
            "SELECT (COUNT(?id) AS ?n) WHERE { ?id rdf:type cim:GovSteam0 . }";
        try {
            // Try a broader presence check: any dynamic model instance
            String cim16AnyQuery = CgmesDyConstants.SPARQL_PREFIXES_CIM16 +
                "SELECT ?id WHERE { ?id rdf:type ?t . FILTER(STRSTARTS(STR(?t), \"" + CgmesDyConstants.CIM16_NS + "\")) } LIMIT 1";
            if (!ts.query(cim16AnyQuery).isEmpty()) {
                LOG.debug("Namespace detection: CIM16 matches found, using CIM16");
                return CgmesDyConstants.CIM16_NS;
            }
        } catch (Exception ignored) { }
        LOG.debug("Namespace detection: falling back to CIM17");
        return CgmesDyConstants.CIM17_NS;
    }
}
