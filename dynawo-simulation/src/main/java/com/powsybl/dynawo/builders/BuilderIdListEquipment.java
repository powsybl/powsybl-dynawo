package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.iidm.network.Identifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents an equipment field identified by a list of alternative static ID in a builder
 * Verifies if an equipment corresponding to one of the ids with the specified type exists, log the error otherwise
 *
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class BuilderIdListEquipment<T extends Identifiable<?>> extends BuilderEquipment<T> {

    public BuilderIdListEquipment(String equipmentType, String fieldName, ReportNode reportNode) {
        super(equipmentType, fieldName, reportNode);
    }

    public BuilderIdListEquipment(String equipmentType, ReportNode reportNode) {
        super(equipmentType, reportNode);
    }

    public void addEquipment(Collection<String> equipmentIds, Function<String, T> equipmentSupplier, EquipmentChecker<T> equipmentChecker) {
        addEquipment(equipmentIds, equipmentSupplier, equipmentChecker, BuilderReports::reportStaticIdListUnknown);
    }

    public void addEquipment(Collection<String> equipmentIds, Function<String, T> equipmentSupplier,
                             EquipmentChecker<T> equipmentChecker, StaticIdListUnknownReportNodeBuilder reportNodeBuilder) {
        equipmentIds.stream().map(equipmentSupplier)
                .filter(Objects::nonNull)
                .filter(eq -> equipmentChecker.test(eq, fieldName, reportNode))
                .findFirst()
                .ifPresentOrElse(
                        eq -> {
                            equipment = eq;
                            staticId = equipment.getId();
                        },
                        () -> {
                            equipment = null;
                            staticId = equipmentIds.toString();
                            reportNodeBuilder.buildReportNode(reportNode, fieldName, staticId, equipmentType);
                        });
    }

    public void addEquipment(Collection<String> equipmentIds, Function<String, T> equipmentSupplier) {
        equipmentIds.stream().map(equipmentSupplier)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(
                        eq -> {
                            equipment = eq;
                            staticId = equipment.getId();
                        },
                        () -> {
                            equipment = null;
                            staticId = equipmentIds.toString();
                            BuilderReports.reportStaticIdListUnknown(reportNode, fieldName, staticId, equipmentType);
                        });
    }

    public void addEquipment(String[] equipmentIds, Function<String, T> equipmentSupplier,
                             EquipmentChecker<T> equipmentChecker, StaticIdListUnknownReportNodeBuilder reportNodeBuilder) {
        addEquipment(Arrays.asList(equipmentIds), equipmentSupplier, equipmentChecker, reportNodeBuilder);
    }

    public void addEquipment(String[] equipmentIds, Function<String, T> equipmentSupplier,
                             EquipmentChecker<T> equipmentChecker) {
        addEquipment(Arrays.asList(equipmentIds), equipmentSupplier, equipmentChecker, BuilderReports::reportStaticIdListUnknown);
    }

    public void addEquipment(String[] equipmentIds, Function<String, T> equipmentSupplier) {
        addEquipment(Arrays.asList(equipmentIds), equipmentSupplier);
    }
}
