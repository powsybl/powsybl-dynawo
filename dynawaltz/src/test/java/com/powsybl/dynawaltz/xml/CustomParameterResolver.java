/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.engine.execution.BeforeEachMethodAdapter;
import org.junit.jupiter.engine.extension.ExtensionRegistry;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

public class CustomParameterResolver implements BeforeEachMethodAdapter, ParameterResolver {

    private ParameterResolver parameterisedTestParameterResolver = null;

    @Override
    public void invokeBeforeEachMethod(ExtensionContext context, ExtensionRegistry registry)
            throws Throwable {
        Optional<ParameterResolver> resolverOptional = registry.getExtensions(ParameterResolver.class)
                .stream()
                .filter(parameterResolver ->
                        parameterResolver.getClass().getName()
                                .contains("ParameterizedTestParameterResolver")
                )
                .findFirst();
        if (!resolverOptional.isPresent()) {
            throw new IllegalStateException(
                    "ParameterizedTestParameterResolver missed in the registry. Probably it's not a Parameterized Test");
        } else {
            parameterisedTestParameterResolver = resolverOptional.get();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        if (isExecutedOnAfterOrBeforeMethod(parameterContext)) {
            ParameterContext pContext = getMappedContext(parameterContext, extensionContext);
            return parameterisedTestParameterResolver.supportsParameter(pContext, extensionContext);
        }
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterisedTestParameterResolver.resolveParameter(
                getMappedContext(parameterContext, extensionContext), extensionContext);
    }

    private MappedParameterContext getMappedContext(ParameterContext parameterContext,
                                                    ExtensionContext extensionContext) {
        return new MappedParameterContext(
                parameterContext.getIndex(),
                extensionContext.getRequiredTestMethod().getParameters()[parameterContext.getIndex()],
                Optional.of(parameterContext.getTarget()));
    }

    private boolean isExecutedOnAfterOrBeforeMethod(ParameterContext parameterContext) {
        return Arrays.stream(parameterContext.getDeclaringExecutable().getDeclaredAnnotations())
                .anyMatch(this::isAfterEachOrBeforeEachAnnotation);
    }

    private boolean isAfterEachOrBeforeEachAnnotation(Annotation annotation) {
        return annotation.annotationType() == BeforeEach.class
                || annotation.annotationType() == AfterEach.class;
    }
}
