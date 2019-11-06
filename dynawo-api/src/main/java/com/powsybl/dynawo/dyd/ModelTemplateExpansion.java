/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ModelTemplateExpansion extends DydParComponent implements DynawoDynamicModel {

    private final String templateId;

    public ModelTemplateExpansion(String id, String templateId, String parametersFile, int parametersId) {
        super(id, parametersFile, parametersId);
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }

}
