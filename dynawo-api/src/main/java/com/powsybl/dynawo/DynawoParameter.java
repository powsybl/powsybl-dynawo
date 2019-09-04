package com.powsybl.dynawo;

public class DynawoParameter {

    private final boolean reference;

    private final String name;
    private final String type;
    private String value;
    private String origName;
    private String origData;

    public DynawoParameter(String name, String type, String value) {
        this.reference = false;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public DynawoParameter(String name, String type, String origData, String origName) {
        this.reference = true;
        this.name = name;
        this.type = type;
        this.origData = origData;
        this.origName = origName;
    }

    public boolean isReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getOrigName() {
        return origName;
    }

    public String getOrigData() {
        return origData;
    }

}
