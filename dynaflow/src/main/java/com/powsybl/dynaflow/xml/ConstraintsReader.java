/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.xml;

import com.google.common.base.Suppliers;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.iidm.network.*;
import com.powsybl.security.LimitViolation;
import com.powsybl.security.LimitViolationType;
import com.powsybl.security.comparator.LimitViolationComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public final class ConstraintsReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintsReader.class);

    private static final String CONSTRAINTS_ELEMENT_NAME = "constraints";
    private static final String CONSTRAINT_ELEMENT_NAME = "constraint";
    private static final String MODEL_NAME = "modelName";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";
    private static final String KIND = "kind";
    private static final String LIMIT = "limit";
    private static final String VALUE = "value";
    private static final String SIDE = "side";
    private static final String ACCEPTABLE_DURATION = "acceptableDuration";

    private static final Supplier<XMLInputFactory> XML_INPUT_FACTORY_SUPPLIER = Suppliers.memoize(XMLInputFactory::newInstance);
    public static final String DYN_CALCULATED_BUS_PREFIX = "calculatedBus_";

    public static List<LimitViolation> read(Network network, Path xmlFile) {
        try (InputStream is = Files.newInputStream(xmlFile)) {
            return read(network, is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<LimitViolation> read(Network network, InputStream is) {
        List<LimitViolation> limitViolations = new ArrayList<>();

        try {
            XMLStreamReader reader = XML_INPUT_FACTORY_SUPPLIER.get().createXMLStreamReader(is);
            int state = reader.next();
            while (state == XMLStreamConstants.COMMENT) {
                state = reader.next();
            }

            if (!CONSTRAINTS_ELEMENT_NAME.equals(reader.getLocalName())) {
                throw new PowsyblException("Unknown element name '" + reader.getLocalName() + "' in constraints file");
            }
            XmlUtil.readSubElements(reader, elementName -> {
                try {
                    if (!elementName.equals(CONSTRAINT_ELEMENT_NAME)) {
                        throw new PowsyblException("Unknown element name '" + elementName + "' in constraints tag");
                    }
                    String name = reader.getAttributeValue(null, MODEL_NAME);
                    reader.getAttributeValue(null, DESCRIPTION); // description: unused
                    reader.getAttributeValue(null, TYPE); // type: unused
                    String kind = reader.getAttributeValue(null, KIND);
                    double limit = XmlUtil.readDoubleAttribute(reader, LIMIT, Double.NaN);
                    double value = XmlUtil.readDoubleAttribute(reader, VALUE, Double.NaN);
                    Integer side = XmlUtil.readIntegerAttribute(reader, SIDE);
                    int acceptableDuration = XmlUtil.readIntAttribute(reader, ACCEPTABLE_DURATION, Integer.MAX_VALUE);
                    XmlUtil.readEndElementOrThrow(reader);

                    getLimitViolation(network, name, kind, limit, 1f, value, side, acceptableDuration)
                            .ifPresent(lvRead -> addOrDismiss(lvRead, limitViolations));

                } catch (XMLStreamException e) {
                    throw new UncheckedXmlStreamException(e);
                }
            });
            return limitViolations;
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void addOrDismiss(LimitViolation lvRead, List<LimitViolation> limitViolations) {
        LimitViolationComparator comparator = new LimitViolationComparator();
        limitViolations.stream().filter(lv -> comparator.compare(lvRead, lv) == 0).findFirst()
                .ifPresentOrElse(
                    lv -> replaceLimitViolationIfStronger(lvRead, lv, limitViolations),
                    () -> limitViolations.add(lvRead));
    }

    private static void replaceLimitViolationIfStronger(LimitViolation newLimitViolation, LimitViolation similarLimitViolation,
                                                        List<LimitViolation> limitViolations) {
        if (newLimitViolation.getAcceptableDuration() < similarLimitViolation.getAcceptableDuration()) {
            limitViolations.remove(similarLimitViolation);
            limitViolations.add(newLimitViolation);
        }
    }

    private static Optional<LimitViolation> getLimitViolation(Network network, String name, String kind, double limit,
                                                              float limitReduction, double value, Integer side, Integer acceptableDuration) {

        return getLimitViolationIdentifiable(network, name)
                .map(identifiable -> new LimitViolation(
                        identifiable.getId(), identifiable.getOptionalName().orElse(null),
                        toLimitViolationType(kind), kind, acceptableDuration,
                        limit, limitReduction, value, toThreeSides(side)));
    }

    private static Optional<Identifiable<?>> getLimitViolationIdentifiable(Network network, String name) {
        if (name.matches(DYN_CALCULATED_BUS_PREFIX + ".*_\\d*")) {
            // FIXME: the voltage level information should be directly referenced
            // The naming corresponds to buses which are calculated in dynawo: https://github.com/dynawo/dynawo/blob/8f1e20e43db7ec4d2e4982deac8307dfa8d0dbec/dynawo/sources/Modeler/DataInterface/PowSyblIIDM/DYNVoltageLevelInterfaceIIDM.cpp#L290
            String vlId = name.substring(DYN_CALCULATED_BUS_PREFIX.length(), name.lastIndexOf("_"));
            VoltageLevel vl = network.getVoltageLevel(vlId); // Limit violation on buses are identified by their voltage level id
            if (vl == null) {
                LOGGER.warn("Constraint on dynawo-calculated bus {} with unknown voltage level {}", name, vlId);
            }
            return Optional.ofNullable(vl);
        } else {
            Identifiable<?> identifiable = network.getIdentifiable(name);
            if (identifiable == null) {
                LOGGER.warn("Unknown equipment/bus {} for limit violation in result constraints file", name);
            }
            if (identifiable instanceof Bus bus) {
                identifiable = bus.getVoltageLevel(); // Limit violation on buses are identified by their voltage level id
            }
            return Optional.ofNullable(identifiable);
        }
    }

    private static ThreeSides toThreeSides(Integer side) {
        if (side == null) {
            return null;
        }
        return ThreeSides.valueOf(side);
    }

    private static LimitViolationType toLimitViolationType(String kind) {
        return switch (kind) {
            case "UInfUmin" -> LimitViolationType.LOW_VOLTAGE;
            case "USupUmax" -> LimitViolationType.HIGH_VOLTAGE;
            case "OverloadOpen", "OverloadUp", "PATL" -> LimitViolationType.CURRENT;
            default -> throw new PowsyblException("Unexpect violation type " + kind);
        };
    }

    private ConstraintsReader() {
    }
}
