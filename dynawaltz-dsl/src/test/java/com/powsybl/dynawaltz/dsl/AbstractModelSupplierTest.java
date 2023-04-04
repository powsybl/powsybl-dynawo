package com.powsybl.dynawaltz.dsl;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractModelSupplierTest {

    private static final String GROOVY_EXTENSION = ".groovy";

    abstract String getFolderName();

    protected InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(AbstractModelSupplierTest.class.getResourceAsStream(getFolderName() + name + GROOVY_EXTENSION));
    }

}
