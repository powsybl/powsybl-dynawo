/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoDynamicModel {

    private final boolean blackBoxModel;

    private String blackBoxModelId;
    private String blackBoxModelLib;
    private String parametersFile;
    private int parametersId;
    private String staticId;

    private String connectionId1;
    private String connectionVar1;
    private String connectionId2;
    private String connectionVar2;

    public DynawoDynamicModel(String id, String lib, String parameters, int parametersId) {
        this(id, lib, parameters, parametersId, null);
    }

    public DynawoDynamicModel(String id, String lib, String parameters, int parametersId, String staticId) {
        this.blackBoxModel = true;
        this.blackBoxModelId = id;
        this.blackBoxModelLib = lib;
        this.parametersFile = parameters;
        this.parametersId = parametersId;
        this.staticId = staticId;
    }

    public DynawoDynamicModel(String id1, String var1, String id2, String var2) {
        this.blackBoxModel = false;
        this.connectionId1 = id1;
        this.connectionVar1 = var1;
        this.connectionId2 = id2;
        this.connectionVar2 = var2;
    }

    public boolean isBlackBoxModel() {
        return blackBoxModel;
    }

    public String getBlackBoxModelId() {
        return blackBoxModelId;
    }

    public String getBlackBoxModelLib() {
        return blackBoxModelLib;
    }

    public String getParametersFile() {
        return parametersFile;
    }

    public int getParametersId() {
        return parametersId;
    }

    public String getStaticId() {
        return staticId;
    }

    public String getConnectionId1() {
        return connectionId1;
    }

    public String getConnectionVar1() {
        return connectionVar1;
    }

    public String getConnectionId2() {
        return connectionId2;
    }

    public String getConnectionVar2() {
        return connectionVar2;
    }

}
