/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticid.staticref.generators;

import com.powsybl.dynawaltz.dynamicmodels.staticid.staticref.BlackBoxModelWithStaticRef;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface GeneratorModel extends BlackBoxModelWithStaticRef {
    String getTerminalVarName();

    String getSwitchOffSignalNodeVarName();

    String getSwitchOffSignalEventVarName();

    String getSwitchOffSignalAutomatonVarName();

    String getRunningVarName();

    public class GeneratorParameters {
        private final String terminalVarName;
        private final String switchOffSignalNodeVarName;
        private final String switchOffSignalEventVarName;
        private final String switchOffSignalAutomatonVarName;
        private final String runningVarName;

        public GeneratorParameters(String terminalVarName, String switchOffSignalNodeVarName, String switchOffSignalEventVarName, String switchOffSignalAutomatonVarName, String runningVarName) {
            this.terminalVarName = terminalVarName;
            this.switchOffSignalNodeVarName = switchOffSignalNodeVarName;
            this.switchOffSignalEventVarName = switchOffSignalEventVarName;
            this.switchOffSignalAutomatonVarName = switchOffSignalAutomatonVarName;
            this.runningVarName = runningVarName;
        }

        public String getTerminalVarName() {
            return terminalVarName;
        }

        public String getSwitchOffSignalNodeVarName() {
            return switchOffSignalNodeVarName;
        }

        public String getSwitchOffSignalEventVarName() {
            return switchOffSignalEventVarName;
        }

        public String getSwitchOffSignalAutomatonVarName() {
            return switchOffSignalAutomatonVarName;
        }

        public String getRunningVarName() {
            return runningVarName;
        }
    }
}
