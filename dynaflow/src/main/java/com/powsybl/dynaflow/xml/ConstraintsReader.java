package com.powsybl.dynaflow.xml;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.iidm.network.*;
import com.powsybl.security.LimitViolation;
import com.powsybl.security.LimitViolationType;
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

    private static List<LimitViolation> read(Network network, InputStream is) {
        List<LimitViolation> limitViolations = new ArrayList<>();

        try {
            XMLStreamReader reader = XML_INPUT_FACTORY_SUPPLIER.get().createXMLStreamReader(is);
            int state = reader.next();
            while (state == XMLStreamConstants.COMMENT) {
                state = reader.next();
            }

            XmlUtil.readUntilEndElement(CONSTRAINTS_ELEMENT_NAME, reader, () -> {
                if (!reader.getLocalName().equals(CONSTRAINT_ELEMENT_NAME)) {
                    throw new AssertionError();
                }
                String name = reader.getAttributeValue(null, MODEL_NAME);
                String description = reader.getAttributeValue(null, DESCRIPTION);
                String type = reader.getAttributeValue(null, TYPE);
                String kind = reader.getAttributeValue(null, KIND);
                double limit = XmlUtil.readOptionalDoubleAttribute(reader, LIMIT);
                double value = XmlUtil.readOptionalDoubleAttribute(reader, VALUE);
                Integer side = XmlUtil.readOptionalIntegerAttribute(reader, SIDE);
                Integer acceptableDuration = XmlUtil.readOptionalIntegerAttribute(reader, ACCEPTABLE_DURATION);

                getLimitViolation(network, name, kind, limit, 1f, value, side, acceptableDuration)
                        .ifPresent(limitViolations::add);
            });
            return limitViolations;
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static Optional<LimitViolation> getLimitViolation(Network network, String name, String kind, double limit,
                                                              float limitReduction, double value, Integer side, Integer acceptableDuration) {
        if (name.matches(DYN_CALCULATED_BUS_PREFIX + ".*_\\d*")) {
            // FIXME: the voltage level information should be directly referenced
            // The naming corresponds to buses which are calculated in dynawo: https://github.com/dynawo/dynawo/blob/8f1e20e43db7ec4d2e4982deac8307dfa8d0dbec/dynawo/sources/Modeler/DataInterface/PowSyblIIDM/DYNVoltageLevelInterfaceIIDM.cpp#L290
            String vlId = name.substring(DYN_CALCULATED_BUS_PREFIX.length(), name.lastIndexOf("_"));
            VoltageLevel vl = network.getVoltageLevel(vlId);
            if (vl != null) {
                return Optional.of(new LimitViolation(vl.getId(), vl.getOptionalName().orElse(null),
                        toLimitViolationType(kind), limit, limitReduction, value));
            } else {
                LOGGER.warn("Constraint on dynawo-calculated bus {} with unknown voltage level {}", name, vlId);
                return Optional.empty();
            }
        } else {
            Identifiable<?> identifiable = network.getIdentifiable(name);
            if (identifiable instanceof Branch) {
                Branch<?> branch = (Branch<?>) identifiable;
                return Optional.of(new LimitViolation(branch.getId(), branch.getOptionalName().orElse(null),
                        toLimitViolationType(kind), kind, acceptableDuration != null ? acceptableDuration : Integer.MAX_VALUE,
                        limit, limitReduction, value, toBranchSide(side)));
            } else if (identifiable instanceof Bus) {
                VoltageLevel vl = ((Bus) identifiable).getVoltageLevel();
                return Optional.of(new LimitViolation(vl.getId(), vl.getOptionalName().orElse(null),
                        toLimitViolationType(kind), limit, limitReduction, value));
            } else if (identifiable != null) {
                return Optional.of(new LimitViolation(identifiable.getId(), identifiable.getOptionalName().orElse(null),
                        toLimitViolationType(kind), limit, limitReduction, value));
            } else {
                LOGGER.warn("Unknown equipment/bus {} for limit violation in result constraints file", name);
                return Optional.empty();
            }
        }
    }

    private static Branch.Side toBranchSide(int side) {
        if (side == 1) {
            return Branch.Side.ONE;
        } else if (side == 2) {
            return Branch.Side.TWO;
        } else {
            return null;
        }
    }

    private static LimitViolationType toLimitViolationType(String kind) {
        if (kind.equals("UInfUmin")) {
            return LimitViolationType.LOW_VOLTAGE;
        } else if (kind.equals("USupUmax")) {
            return LimitViolationType.HIGH_VOLTAGE;
        } else if (kind.equals("OverloadOpen") || kind.equals("OverloadUp") || kind.equals("PATL")) {
            return LimitViolationType.CURRENT;
        } else {
            throw new PowsyblException("Unexpect violation type " + kind);
        }
    }

    private ConstraintsReader() {
    }
}
