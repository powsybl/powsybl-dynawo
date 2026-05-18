/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.ParameterInfo;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CustomParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        if (isExecutedOnAfterOrBeforeMethod(parameterContext)) {
            ParameterInfo parameterInfo = ParameterInfo.get(extensionContext);
            return parameterInfo != null
                && parameterContext.getIndex() < parameterInfo.getArguments().size();
        }
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        ParameterInfo parameterInfo = ParameterInfo.get(extensionContext);
        if (parameterInfo == null) {
            throw new ParameterResolutionException(
                "No ParameterInfo found in ExtensionContext for parameterized test");
        }
        int index = parameterContext.getIndex();
        return parameterInfo.getArguments().get(index);
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
