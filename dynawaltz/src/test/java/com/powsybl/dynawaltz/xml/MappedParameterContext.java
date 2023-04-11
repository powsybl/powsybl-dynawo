/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

public class MappedParameterContext implements ParameterContext {

    private final int index;
    private final Parameter parameter;
    private final Optional<Object> target;

    public MappedParameterContext(int index, Parameter parameter, Optional<Object> target) {
        this.index = index;
        this.parameter = parameter;
        this.target = target;
    }

    @Override
    public boolean isAnnotated(Class<? extends Annotation> annotationType) {
        return AnnotationUtils.isAnnotated(parameter, annotationType);
    }

    @Override
    public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
        return Optional.empty();
    }

    @Override
    public <A extends Annotation> List<A> findRepeatableAnnotations(Class<A> annotationType) {
        return null;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public Optional<Object> getTarget() {
        return target;
    }
}
