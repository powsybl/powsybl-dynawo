package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton;
import com.powsybl.iidm.network.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerBlockingAutomatonBuilder> {

    public static final String LIB = "TapChangerBlockingAutomaton";

    private final List<Load> loads = new ArrayList<>();
    private final List<TwoWindingsTransformer> transformers = new ArrayList<>();
    private final List<String> tapChangerAutomatonIds = new ArrayList<>();
    private List<Bus> uMeasurements;

    public TapChangerBlockingAutomatonBuilder(Network network, String lib, Reporter reporter) {
        super(network, lib, reporter);
    }

    public TapChangerBlockingAutomatonBuilder transformers(String[] staticIds) {
        Arrays.stream(staticIds).forEach(id -> {
            Identifiable<?> equipment = network.getIdentifiable(id);
            if (equipment == null) {
                tapChangerAutomatonIds.add(id);
            } else {
                switch (equipment.getType()) {
                    case LOAD -> loads.add((Load) equipment);
                    case TWO_WINDINGS_TRANSFORMER -> transformers.add((TwoWindingsTransformer) equipment);
                    default -> Reporters.reportStaticIdUnknown(reporter, "uMeasurements", id, "LOAD/TWO_WINDINGS_TRANSFORMER");
                }
            }
        });
        return self();
    }

    public TapChangerBlockingAutomatonBuilder uMeasurements(String[] staticIds) {
        uMeasurements = Arrays.stream(staticIds)
                .map(id -> {
                    Bus bus = network.getBusBreakerView().getBus(id);
                    if (bus == null) {
                        Reporters.reportStaticIdUnknown(reporter, "uMeasurements", id, IdentifiableType.BUS.toString());
                    }
                    return bus;
                })
                .filter(Objects::nonNull)
                .toList();
        return self();
    }

    @Override
    protected void checkData() {
        if (uMeasurements == null) {
            Reporters.reportFieldNotSet(reporter, "uMeasurements");
            isInstantiable = false;
        } else if (uMeasurements.isEmpty()) {
            Reporters.reportEmptyList(reporter, "uMeasurements");
            isInstantiable = false;
        }
        if (loads.isEmpty() && transformers.isEmpty() && tapChangerAutomatonIds.isEmpty()) {
            Reporters.reportEmptyList(reporter, "transformers");
            isInstantiable = false;
        }
    }

    @Override
    public TapChangerBlockingAutomaton build() {
        return isInstantiable() ? new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId, transformers, loads, tapChangerAutomatonIds, uMeasurements) : null;
    }

    @Override
    protected TapChangerBlockingAutomatonBuilder self() {
        return this;
    }
}
