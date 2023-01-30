package com.powsybl.dynawo.commons;

import com.google.auto.service.AutoService;
import com.powsybl.tools.AbstractVersion;
import com.powsybl.tools.Version;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(Version.class)
public class PowsyblDynawoVersion extends AbstractVersion {

    public PowsyblDynawoVersion() {
        super("powsybl-dynawo", "${project.version}", "${buildNumber}", "${scmBranch}", Long.parseLong("${timestamp}"));
    }
}
