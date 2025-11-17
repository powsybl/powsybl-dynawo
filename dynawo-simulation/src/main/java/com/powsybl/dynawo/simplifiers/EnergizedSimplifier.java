package com.powsybl.dynawo.simplifiers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystem;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystem;
import com.powsybl.dynawo.models.hvdc.BaseHvdc;
import com.powsybl.iidm.network.*;

import java.util.function.Predicate;

/**
 * Filter energized equipment models, namely :
 * <ul>
 *     <li>Equipment terminals are all connected (except dangling sides)</li>
 *     <li>Each terminal buses have a voltage level on</li>
 * </ul>
 * Filter overload management systems :
 * <ul>
 *     <li>monitored branch is energized</li>
 *     <li>measuring point is energized</li>
 * </ul>
 *
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(ModelsRemovalSimplifier.class)
public final class EnergizedSimplifier implements ModelsRemovalSimplifier {

    private static final ModelSimplifierInfo MODEL_INFO = new ModelSimplifierInfo("energizedEquipment",
            "Filter equipment with all terminals connected (except dangling sides) and terminal buses with a voltage level on",
            SIMPLIFIER_TYPE);

    @Override
    public ModelSimplifierInfo getSimplifierInfo() {
        return MODEL_INFO;
    }

    @Override
    public Predicate<BlackBoxModel> getModelRemovalPredicate(ReportNode reportNode) {
        ReportNode simplifierReport = SimplifierReports.createEnergizedSimplifierReportNode(reportNode);
        return model -> switch (model) {
            case EquipmentBlackBoxModel eq -> isEnergized(eq, simplifierReport);
            case DynamicTwoLevelOverloadManagementSystem tl -> isEnergized(tl, simplifierReport);
            case DynamicOverloadManagementSystem ov -> isEnergized(ov, simplifierReport);
            default -> true;
        };
    }

    private static boolean isEnergized(EquipmentBlackBoxModel bbm, ReportNode reportNode) {
        return switch (bbm.getEquipment()) {
            case Injection<?> inj -> isEnergized(inj.getTerminal(), reportNode, bbm);
            case Branch<?> br -> isEnergized(br.getTerminal1(), reportNode, bbm) && isEnergized(br.getTerminal2(), reportNode, bbm);
            case HvdcLine l
                when bbm instanceof BaseHvdc hvdcBbm -> hvdcBbm.getConnectedStations().stream()
                    .map(st -> isEnergized(st.getTerminal(), reportNode, bbm))
                    .reduce(true, (e1, e2) -> e1 && e2);
            case Bus b -> isEnergized(b, reportNode, bbm);
            default -> true;
        };
    }

    private static boolean isEnergized(DynamicTwoLevelOverloadManagementSystem bbm, ReportNode reportNode) {
        return isEnergized((DynamicOverloadManagementSystem) bbm, reportNode)
                && isEnergized(bbm.getSecondMeasuredTerminal(), reportNode, bbm);
    }

    private static boolean isEnergized(DynamicOverloadManagementSystem bbm, ReportNode reportNode) {
        Branch<?> controlledBranch = bbm.getControlledBranch();
        return isEnergized(controlledBranch.getTerminal1(), reportNode, bbm)
                && isEnergized(controlledBranch.getTerminal2(), reportNode, bbm)
                && isEnergized(bbm.getMeasuredTerminal(), reportNode, bbm);
    }

    private static boolean isEnergized(Terminal terminal, ReportNode reportNode, BlackBoxModel bbm) {
        if (!terminal.isConnected()) {
            SimplifierReports.reportDisconnectedTerminal(reportNode, bbm.getName(), bbm.getDynamicModelId(),
                    terminal.getConnectable().getId());
            return false;
        }
        return isEnergized(terminal.getBusBreakerView().getBus(), reportNode, bbm);
    }

    private static boolean isEnergized(Bus bus, ReportNode reportNode, BlackBoxModel bbm) {
        if (Double.isNaN(bus.getV())) {
            SimplifierReports.reportVoltageLevelOff(reportNode, bbm.getName(), bbm.getDynamicModelId(), bus.getId());
            return false;
        }
        return true;
    }
}
