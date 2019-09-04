package com.powsybl.dynawo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DynawoParameterSet {

    private final int id;
    private final List<DynawoParameter> parameters;

    public DynawoParameterSet(int id, DynawoParameter... parameters) {
        this(id, Arrays.asList(parameters));
    }

    public DynawoParameterSet(int id, List<DynawoParameter> parameters) {
        this.id = id;
        this.parameters = new ArrayList<>(Objects.requireNonNull(parameters));
    }

    public int getId() {
        return id;
    }

    public List<DynawoParameter> getParameters() {
        return parameters;
    }
}
