/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.parser;

import com.powsybl.triplestore.api.PropertyBag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Utilities for safely extracting typed values from powsybl {@link PropertyBag}.
 *
 * <p>All methods are null-safe and log a warning rather than throwing when
 * a mandatory field is missing, allowing partial parsing to continue.</p>
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public final class PropertyBagUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyBagUtils.class);

    private PropertyBagUtils() { }

    /** Extract a {@code double} value; returns {@code Double.NaN} if missing or unparseable. */
    public static double asDouble(PropertyBag bag, String field) {
        try {
            String v = bag.get(field);
            if (v == null || v.isBlank()) {
                LOG.trace("Field '{}' is absent in resource '{}'", field, bag.getId("id"));
                return Double.NaN;
            }
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Cannot parse double for field '{}', raw value: '{}'", field, bag.get(field));
            return Double.NaN;
        }
    }

    /** Extract a {@code double} with an explicit default when missing. */
    public static double asDouble(PropertyBag bag, String field, double defaultValue) {
        double v = asDouble(bag, field);
        return Double.isNaN(v) ? defaultValue : v;
    }

    /** Extract a nullable boxed {@code Double} (for truly optional CIM attributes). */
    @Nullable
    public static Double asDoubleOrNull(PropertyBag bag, String field) {
        String v = bag.get(field);
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Extract a {@code boolean} value; false when absent. */
    public static boolean asBoolean(PropertyBag bag, String field) {
        String v = bag.get(field);
        return v != null && (v.equalsIgnoreCase("true") || v.equals("1"));
    }

    /** Extract a {@code boolean} with an explicit default when missing. */
    public static boolean asBoolean(PropertyBag bag, String field, boolean defaultValue) {
        String v = bag.get(field);
        if (v == null || v.isBlank()) {
            return defaultValue;
        }
        return v.equalsIgnoreCase("true") || v.equals("1");
    }

    /** Extract an {@code int} value; returns {@code 0} if missing. */
    public static int asInt(PropertyBag bag, String field) {
        try {
            String v = bag.get(field);
            if (v == null || v.isBlank()) {
                return 0;
            }
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Cannot parse int for field '{}', raw value: '{}'", field, bag.get(field));
            return 0;
        }
    }

    /** Extract a required {@code String} field; logs a warning and returns empty string if absent. */
    public static String asString(PropertyBag bag, String field) {
        String v = bag.get(field);
        if (v == null) {
            LOG.warn("Required string field '{}' is absent in resource '{}'", field, bag.getId("id"));
            return "";
        }
        return v.trim();
    }

    /** Extract an optional {@code String}; returns null if absent. */
    @Nullable
    public static String asStringOrNull(PropertyBag bag, String field) {
        String v = bag.get(field);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    /**
     * Convenience: extract the resource ID field ({@code ?id} in SPARQL projections).
     * Strips any leading/trailing angle brackets that some triple-store implementations add.
     */
    public static String resourceId(PropertyBag bag) {
        String raw = bag.getId("id");
        if (raw == null) {
            return "";
        }
        return raw.startsWith("<") ? raw.substring(1, raw.length() - 1) : raw;
    }

    /**
     * Extract a CIM <em>association</em> ID — i.e. a SPARQL variable bound to a resource
     * URI rather than a plain literal.
     *
     * <p>The Jena triplestore returns resource-valued bindings as their full URI string,
     * e.g. {@code urn:test:gov-dy#_sm-gov-1} or {@code <urn:test:gov-dy#_sm-gov-1>}.
     * This method strips angle brackets and everything up-to-and-including the last
     * {@code #} or {@code /} separator so that only the local-name fragment is stored,
     * e.g. {@code _sm-gov-1}.  This is consistent with the fragment IDs assigned in the
     * source RDF/XML ({@code rdf:ID="_sm-gov-1"}) and with what {@link #resourceId}
     * returns for the subject {@code ?id} variable.</p>
     *
     * <p>Returns an empty string (logged at TRACE level) when the field is absent —
     * the caller should treat a blank result as "no association present".</p>
     *
     * @param bag   the SPARQL result row
     * @param field the SPARQL variable name bound to the resource reference
     * @return the local-name fragment of the URI, or {@code ""} if absent
     */
    public static String asAssocId(PropertyBag bag, String field) {
        String v = bag.get(field);
        if (v == null || v.isBlank()) {
            LOG.trace("Association field '{}' is absent in resource '{}'", field, bag.getId("id"));
            return "";
        }
        return stripUri(v.trim());
    }

    // ── internal ──────────────────────────────────────────────────────────────

    /**
     * Strip angle brackets and URI path/namespace prefix, returning only the local name.
     * <ul>
     *   <li>{@code <urn:test:x#_foo>}         → {@code _foo}</li>
     *   <li>{@code urn:test:x#_foo}            → {@code _foo}</li>
     *   <li>{@code http://example.com/ns#_foo} → {@code _foo}</li>
     *   <li>{@code http://example.com/ns/_foo} → {@code _foo}</li>
     *   <li>{@code _foo}                       → {@code _foo} (already local)</li>
     * </ul>
     */
    static String stripUri(String raw) {
        String s = raw;
        if (s.startsWith("<") && s.endsWith(">")) {
            s = s.substring(1, s.length() - 1);
        }
        int hash = s.lastIndexOf('#');
        if (hash >= 0) {
            s = s.substring(hash + 1);
            if (!s.isEmpty() && s.charAt(0) == '_') {
                s = s.substring(1);
            }
            return s;
        }
        int slash = s.lastIndexOf('/');
        if (slash >= 0) {
            s = s.substring(slash + 1);
            if (!s.isEmpty() && s.charAt(0) == '_') {
                s = s.substring(1);
            }
            return s;
        }
        if (!s.isEmpty() && s.charAt(0) == '_') {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * Extract a CIM association ID (e.g. {@code ?synchronousMachineId}).
     * Returns empty string when absent, consistent with {@link #asString}.
     */
    public static String associationId(PropertyBag bag, String field) {
        return asString(bag, field);
    }
}
