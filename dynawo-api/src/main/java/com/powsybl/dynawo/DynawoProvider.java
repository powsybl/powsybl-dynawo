package com.powsybl.dynawo;

import java.util.List;

public interface DynawoProvider {

    List<DynawoJob> getDynawoJob();

    List<DynawoCurve> getDynawoCurves();

    List<DynawoDynamicModel> getDynawoDynamicModels();

    List<DynawoParameterSet> getDynawoParameterSets();

    List<DynawoParameterSet> getDynawoSolverParameterSets();
}
