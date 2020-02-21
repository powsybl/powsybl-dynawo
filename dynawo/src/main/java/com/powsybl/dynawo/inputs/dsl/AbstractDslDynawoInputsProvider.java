/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.powsybl.dynawo.inputs.model.DynawoInputsProvider;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractDslDynawoInputsProvider implements DynawoInputsProvider {

    protected final GroovyCodeSource script;

    /**
     * Creates a Dynawo inputs model by reading DSL from a UTF-8 encoded file.
     */
    protected AbstractDslDynawoInputsProvider(final Path path) {
        Objects.requireNonNull(path);
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            script = new GroovyCodeSource(reader, "script", GroovyShell.DEFAULT_CODE_BASE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates a Dynawo inputs model by reading the DSL content from a UTF-8 encoded input stream.
     */
    protected AbstractDslDynawoInputsProvider(final InputStream input) {
        Objects.requireNonNull(input);
        script = new GroovyCodeSource(new InputStreamReader(input, StandardCharsets.UTF_8), "script", GroovyShell.DEFAULT_CODE_BASE);
    }

}
