package com.powsybl.dynaflow.xml;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.iidm.network.*;
import com.powsybl.security.LimitViolation;
import com.powsybl.security.LimitViolationType;

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

public final class ConstraintsReader {

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
                float limitReduction = 1f;
                switch (reader.getLocalName()) {
                    case CONSTRAINT_ELEMENT_NAME:
                        String name = reader.getAttributeValue(null, MODEL_NAME);
                        String description = reader.getAttributeValue(null, DESCRIPTION);
                        String type = reader.getAttributeValue(null, TYPE);
                        String kind = reader.getAttributeValue(null, KIND);
                        double limit = XmlUtil.readOptionalDoubleAttribute(reader, LIMIT);
                        double value = XmlUtil.readOptionalDoubleAttribute(reader, VALUE);
                        Integer side = XmlUtil.readOptionalIntegerAttribute(reader, SIDE);
                        Integer acceptableDuration = XmlUtil.readOptionalIntegerAttribute(reader, ACCEPTABLE_DURATION);
                        Identifiable id = network.getIdentifiable(name);
                        LimitViolation limitViolation;
                        if (id instanceof Branch) {
                            Branch<?> branch = (Branch<?>) id;
                            limitViolation = new LimitViolation(branch.getId(), branch.getOptionalName().orElse(null),
                                    toLimitViolationType(kind), kind, acceptableDuration != null ? acceptableDuration : Integer.MAX_VALUE,
                                    limit, limitReduction, value, toBranchSide(side));
                            limitViolations.add(limitViolation);
                        } else if (id instanceof Bus) {
                            VoltageLevel vl = ((Bus) id).getVoltageLevel();
                            limitViolation = new LimitViolation(vl.getId(), vl.getOptionalName().orElse(null),
                                    toLimitViolationType(kind), limit, limitReduction, value);
                            limitViolations.add(limitViolation);
                        }
                        break;

                    default:
                        throw new AssertionError();
                }
            });
            return limitViolations;
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
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
