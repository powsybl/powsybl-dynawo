package com.powsybl.dynawo.models;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
public interface GeneratorModel extends InjectionModel {
    String getTerminalVarName();
}
