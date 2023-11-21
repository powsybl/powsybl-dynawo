package com.powsybl.dynawaltz.models.defaultmodels;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DefaultModelFactoryInterface<T> {

    T getDefaultModel(String staticId);

}
