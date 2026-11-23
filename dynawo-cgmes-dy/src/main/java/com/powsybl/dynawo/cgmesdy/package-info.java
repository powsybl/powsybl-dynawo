/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 *
 *  CGMES DY (Dynamics) profile parser for powsybl-dynawo.
 *
 * <p>This module provides:
 * <ul>
 *   <li>{@link com.powsybl.dynawo.cgmesdy.parser.CgmesDyImporter} – top-level entry point.</li>
 *   <li>{@link com.powsybl.dynawo.cgmesdy.CgmesDyModel} – container of all parsed model instances.</li>
 *   <li>Java record types in sub-packages ({@code governors}, {@code exciters}, {@code pss},
 *       {@code synchronous}, {@code asynchronous}, {@code wind}, {@code load}, {@code hvdc},
 *       {@code protection}, {@code userdef}) – one record per IEC 61970-302 CIM class.</li>
 *   <li>{@link com.powsybl.dynawo.cgmesdy.CgmesDyConstants} – CIM namespace URIs and class name constants.</li>
 * </ul>
 * </p>
 *
 * <h3>Dependency on powsybl-core</h3>
 * <p>All RDF/XML I/O is delegated to the powsybl-core triplestore infrastructure
 * ({@code powsybl-triplestore-api} + a runtime implementation such as Jena or RDF4J).
 * No custom XML parsing is performed.</p>
 *
 * @see com.powsybl.dynawo.cgmesdy.parser.CgmesDyImporter
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
package com.powsybl.dynawo.cgmesdy;
